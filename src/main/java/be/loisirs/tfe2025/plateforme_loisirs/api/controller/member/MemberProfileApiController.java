package be.loisirs.tfe2025.plateforme_loisirs.api.controller.member;

import be.loisirs.tfe2025.plateforme_loisirs.dto.user.AccountDeletionRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.user.MemberProfileResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.user.MemberProfileUpdateDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.user.PasswordChangeRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.user.ProfileImageResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.service.AuthCookieService;
import be.loisirs.tfe2025.plateforme_loisirs.service.MemberProfileService;
import be.loisirs.tfe2025.plateforme_loisirs.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/member/profile")
public class MemberProfileApiController {

    private final UserService userService;
    private final MemberProfileService memberProfileService;
    private final AuthCookieService authCookieService;

    public MemberProfileApiController(UserService userService,
                                      MemberProfileService memberProfileService,
                                      AuthCookieService authCookieService) {
        this.userService = userService;
        this.memberProfileService = memberProfileService;
        this.authCookieService = authCookieService;
    }

    @GetMapping
    public ResponseEntity<MemberProfileResponseDTO> getProfile(Principal principal) {
        return ResponseEntity.ok(
                memberProfileService.getProfile(principal.getName())
        );
    }

    @PutMapping
    public ResponseEntity<MemberProfileResponseDTO> updateProfile(
            @Valid @RequestBody MemberProfileUpdateDTO request,
            Principal principal
    ) {
        return ResponseEntity.ok(
                memberProfileService.updateProfile(principal.getName(), request)
        );
    }

    /**
     * Changement du mot de passe de l'utilisateur connecté (MEMBER, PARTNER, ADMIN :
     * /api/member/** est ouvert aux trois rôles dans SecurityConfig).
     * 204 si succès ; 400 si mot de passe actuel incorrect, nouveau trop faible ou identique.
     * En cas de succès, le cookie est effacé : le jeton est révoqué par le changement,
     * l'utilisateur se reconnecte avec son nouveau mot de passe.
     */
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody PasswordChangeRequestDTO request,
            Principal principal
    ) {
        memberProfileService.changePassword(principal.getName(), request);

        return noContentClearingAuthCookie();
    }

    /** Suppression du compte : le cookie est effacé, la session n'a plus de titulaire. */
    @PostMapping("/deletion")
    public ResponseEntity<Void> deleteAccount(
            @Valid @RequestBody AccountDeletionRequestDTO request,
            Principal principal
    ) {
        memberProfileService.deleteAccount(principal.getName(), request);

        return noContentClearingAuthCookie();
    }

    @PutMapping("/image")
    public ResponseEntity<ProfileImageResponseDTO> updateProfileImage(
            @RequestParam("file") MultipartFile file,
            Principal principal
    ) {
        User user = userService.updateProfileImage(principal.getName(), file);

        return ResponseEntity.ok(
                new ProfileImageResponseDTO(
                        user.getId(),
                        user.getProfileImageUrl()
                )
        );
    }

    private ResponseEntity<Void> noContentClearingAuthCookie() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, authCookieService.clear().toString())
                .build();
    }
}