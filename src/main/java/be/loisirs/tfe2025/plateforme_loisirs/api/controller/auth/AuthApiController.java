package be.loisirs.tfe2025.plateforme_loisirs.api.controller.auth;

import be.loisirs.tfe2025.plateforme_loisirs.dto.AuthResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.LoginRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.RegisterRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthApiController {

    private final AuthService authService;

    public AuthApiController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        AuthResponseDTO response = authService.register(registerRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        AuthResponseDTO response = authService.login(loginRequestDTO);
        return ResponseEntity.ok(response);
    }
}