package be.loisirs.tfe2025.plateforme_loisirs.api.controller.auth;

import be.loisirs.tfe2025.plateforme_loisirs.dto.AuthResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.AuthResult;
import be.loisirs.tfe2025.plateforme_loisirs.dto.LoginRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.RegisterRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.service.AuthCookieService;
import be.loisirs.tfe2025.plateforme_loisirs.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthService authService;
    private final AuthCookieService authCookieService;

    public AuthApiController(AuthService authService, AuthCookieService authCookieService) {
        this.authService = authService;
        this.authCookieService = authCookieService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return withAuthCookie(authService.register(registerRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return withAuthCookie(authService.login(loginRequestDTO));
    }

    // Anonyme (cookie absent ou expiré) : il ne reste qu'à effacer le cookie.
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Principal principal) {
        if (principal != null) {
            authService.logout(principal.getName());
        }

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, authCookieService.clear().toString())
                .build();
    }

    private ResponseEntity<AuthResponseDTO> withAuthCookie(AuthResult result) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookieService.create(result.token()).toString())
                .body(result.response());
    }
}