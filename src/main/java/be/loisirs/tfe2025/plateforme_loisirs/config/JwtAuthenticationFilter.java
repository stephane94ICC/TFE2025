package be.loisirs.tfe2025.plateforme_loisirs.config;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityEventType;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Role;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.repository.UserRepository;
import be.loisirs.tfe2025.plateforme_loisirs.service.ActivityLogService;
import be.loisirs.tfe2025.plateforme_loisirs.service.AuthCookieService;
import be.loisirs.tfe2025.plateforme_loisirs.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

/**
 * Authentifie chaque requête à partir du jeton de connexion.
 *
 * Le jeton est cherché d'abord dans l'en-tête "Authorization: Bearer"
 * (tests, clients hors navigateur), puis dans le cookie BL_AUTH (navigateur).
 *
 * Les rôles sont relus en base à chaque requête, pas dans le jeton :
 * un rôle retiré par l'administrateur prend effet immédiatement.
 * Cela ne coûte aucune requête de plus : l'utilisateur est déjà chargé
 * pour vérifier son état, et ses rôles sont chargés avec lui (EAGER).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final AuthCookieService authCookieService;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   AuthCookieService authCookieService,
                                   UserRepository userRepository,
                                   ActivityLogService activityLogService) {
        this.jwtService = jwtService;
        this.authCookieService = authCookieService;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
    }

    // Seules les routes /api/** ont besoin d'authentification : pages, scripts et images passent sans lecture en base.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Optional<String> optionalToken = resolveToken(request);

        if (optionalToken.isEmpty() || !jwtService.isTokenValid(optionalToken.get())) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = jwtService.extractAllClaims(optionalToken.get());
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

        // Déconnexion faite depuis un appareil : les jetons de l'ancienne version sont refusés partout.
        if (!hasCurrentSessionVersion(claims, user)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Rôles lus en base, jamais dans le jeton.
        Collection<SimpleGrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(Role::getName)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    /**
     * L'en-tête est prioritaire sur le cookie. Le navigateur n'envoie jamais
     * cet en-tête de lui-même : il n'expose donc pas aux attaques CSRF.
     */
    private Optional<String> resolveToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            return Optional.of(authorizationHeader.substring(BEARER_PREFIX.length()));
        }

        return authCookieService.read(request);
    }

    private boolean hasCurrentSessionVersion(Claims claims, User user) {
        Integer tokenVersion = claims.get(JwtService.SESSION_VERSION_CLAIM, Integer.class);

        return tokenVersion != null && tokenVersion == user.getSessionVersion();
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