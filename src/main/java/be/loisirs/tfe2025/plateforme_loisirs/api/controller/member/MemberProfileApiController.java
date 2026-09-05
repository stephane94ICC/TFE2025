package be.loisirs.tfe2025.plateforme_loisirs.api.controller.member;

import be.loisirs.tfe2025.plateforme_loisirs.dto.user.AccountDeletionRequestDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.user.MemberProfileResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.user.MemberProfileUpdateDTO;
import be.loisirs.tfe2025.plateforme_loisirs.dto.user.ProfileImageResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.service.MemberProfileService;
import be.loisirs.tfe2025.plateforme_loisirs.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/member/profile")
public class MemberProfileApiController {

    private final UserService userService;
    private final MemberProfileService memberProfileService;

    public MemberProfileApiController(UserService userService,
                                      MemberProfileService memberProfileService) {
        this.userService = userService;
        this.memberProfileService = memberProfileService;
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

    @PostMapping("/deletion")
    public ResponseEntity<Void> deleteAccount(
            @Valid @RequestBody AccountDeletionRequestDTO request,
            Principal principal
    ) {
        memberProfileService.deleteAccount(principal.getName(), request);

        return ResponseEntity.noContent().build();
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
}