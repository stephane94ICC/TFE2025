package be.loisirs.tfe2025.plateforme_loisirs.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Double soumission : le front renvoie la valeur du cookie XSRF-TOKEN dans l'en-tête X-XSRF-TOKEN.
        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfTokenRepository.setCookieCustomizer(cookie -> cookie.sameSite("Lax"));

        // Le navigateur d'un autre site ne peut pas ajouter cet en-tête : pas de risque CSRF.
        RequestMatcher bearerRequest = SecurityConfig::hasBearerToken;

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                        // Stripe signe ses appels : la signature remplace le jeton CSRF.
                        .ignoringRequestMatchers("/api/stripe/webhook")
                        .ignoringRequestMatchers(bearerRequest)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Anonyme ou jeton refusé : 401 (le front renvoie vers la connexion). Rôle insuffisant : 403.
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .authorizeHttpRequests(auth -> auth
                        //routes frontend controllé via Routeur
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/error",
                                "/login",
                                "/register",
                                "/politique-confidentialite",
                                "/conditions-generales",
                                "/activities/**",
                                "/shop",
                                "/products/**",
                                "/cart",
                                "/partner",
                                "/partner/**",
                                "/partners/**",
                                "/admin",
                                "/admin/**",
                                "/profile",
                                "/reservations",
                                "/uploads/**",
                                "/assets/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/img/**",
                                "/payment/success",
                                "/payment/cancel",
                                "/favicon.ico"
                        ).permitAll()

                            // Authentification publique
                            .requestMatchers("/api/auth/**").permitAll()

                            // API publiques
                            .requestMatchers(HttpMethod.GET, "/api/activities/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/partners/**").permitAll()

                            // API protégées
                            .requestMatchers("/api/admin/**").hasRole("ADMIN")
                            .requestMatchers("/api/partner/**" ).hasRole("PARTNER")
                            .requestMatchers( "/api/member/**" ).hasAnyRole("MEMBER","ADMIN","PARTNER")
                            .requestMatchers("/api/orders/**").hasRole("ADMIN")

                            //Webhook
                            .requestMatchers("/api/stripe/webhook").permitAll()

                            // Le reste doit etre connecté
                            .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static boolean hasBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        return authorization != null && authorization.startsWith("Bearer ");
    }
}