package be.loisirs.tfe2025.plateforme_loisirs.config;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.UserRepository;
import be.loisirs.tfe2025.plateforme_loisirs.service.ActivityLogService;
import be.loisirs.tfe2025.plateforme_loisirs.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserRepository userRepository,
                                   ActivityLogService activityLogService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = jwtService.extractAllClaims(token);
        String email = claims.getSubject();

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        User user = optionalUser.get();

        if (!Boolean.TRUE.equals(user.getActive())) {
            activityLogService.logForEmail(
                    ActivityEventType.ACCESS_DENIED_INACTIVE_ACCOUNT,
                    user.getId(),
                    email,
                    "Jeton présenté sur " + request.getMethod() + " " + request.getRequestURI()
            );

            filterChain.doFilter(request, response);
            return;
        }

        // Jeton émis avant le dernier changement de mot de passe : refusé.
        // Pas de journalisation ici : un onglet resté ouvert écrirait une ligne à chaque requête.
        if (issuedBeforePasswordChange(claims, user)) {
            filterChain.doFilter(request, response);
            return;
        }

        List<String> roles = claims.get("roles", List.class);

        Collection<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    /**
     * Compare la date d'émission du jeton (iat, à la seconde) à la date du dernier
     * changement de mot de passe (enregistrée à la seconde). Un jeton émis dans la même
     * seconde que le changement est accepté : c'est le cas de la reconnexion immédiate.
     */
    private boolean issuedBeforePasswordChange(Claims claims, User user) {
        LocalDateTime passwordChangedAt = user.getPasswordChangedAt();

        if (passwordChangedAt == null) {
            return false;
        }

        Date issuedAt = claims.getIssuedAt();

        if (issuedAt == null) {
            return true; // Jeton sans date d'émission : refusé par prudence.
        }

        LocalDateTime tokenIssuedAt = LocalDateTime.ofInstant(issuedAt.toInstant(), ZoneId.systemDefault());

        return tokenIssuedAt.isBefore(passwordChangedAt);
    }
}