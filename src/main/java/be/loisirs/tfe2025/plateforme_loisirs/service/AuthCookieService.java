package be.loisirs.tfe2025.plateforme_loisirs.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;


@Service
public class AuthCookieService {

    // En HTTPS, le préfixe __Host- fait exiger par le navigateur Secure, Path=/ et aucun domaine.
    public static final String HTTPS_COOKIE_NAME = "__Host-BL_AUTH";
    // En local (http://localhost), Chrome refuse ce préfixe : nom simple, sans Secure.
    public static final String LOCAL_COOKIE_NAME = "BL_AUTH";

    private final boolean secure;
    private final String cookieName;
    private final Duration maxAge;

    public AuthCookieService(@Value("${app.auth.cookie-secure}") boolean secure,
                             JwtService jwtService) {
        this.secure = secure;
        this.cookieName = secure ? HTTPS_COOKIE_NAME : LOCAL_COOKIE_NAME;
        this.maxAge = jwtService.getExpiration();
    }

    public String getCookieName() {
        return cookieName;
    }

    /** Cookie posé à la connexion et à l'inscription. */
    public ResponseCookie create(String token) {
        return builder(token).maxAge(maxAge).build();
    }

    /** Cookie vide et déjà expiré : le navigateur supprime le cookie existant. */
    public ResponseCookie clear() {
        return builder("").maxAge(Duration.ZERO).build();
    }

    /** Valeur du cookie dans la requête, s'il est présent et non vide. */
    public Optional<String> read(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.isBlank())
                .findFirst();
    }

    /** Attributs communs : la suppression doit reprendre le même nom et le même chemin. */
    private ResponseCookie.ResponseCookieBuilder builder(String value) {
        return ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/");
    }
}