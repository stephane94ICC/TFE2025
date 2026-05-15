package be.loisirs.tfe2025.plateforme_loisirs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        //routes frontend controllé via Routeur
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/activities/**",
                                "/shop",
                                "/products/**",
                                "/cart",
                                "/partner",
                                "/admin",
                                "/admin/products",
                                "/profile",
                                "/users",
                                "/assets/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()

                            // Authentification publique
                            .requestMatchers("/api/auth/**").permitAll()

                            // API publiques
                            .requestMatchers(HttpMethod.GET, "/api/activities/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                            // API protégées
                            .requestMatchers("/api/admin/**").hasRole("ADMIN")
                            .requestMatchers("/api/partner/**" ).hasRole("PARTNER")
                            .requestMatchers( "/api/member/**" ).hasAnyRole("MEMBER","ADMIN","PARTNER")


                            // Le reste
                            .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}