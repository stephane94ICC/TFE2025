package be.loisirs.tfe2025.plateforme_loisirs.api.controller.member;


import be.loisirs.tfe2025.plateforme_loisirs.entity.User;
import be.loisirs.tfe2025.plateforme_loisirs.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/member/profile")
public class MemberProfileApiController {
    private final UserService userService;
    public MemberProfileApiController(UserService userService) { this.userService = userService; }

    @PutMapping("/image")
    public ResponseEntity<Map<String, Object>> updateProfileImage(@RequestParam("file") MultipartFile file, Principal principal) {
        User user = userService.updateProfileImage(principal.getName(), file);
        return ResponseEntity.ok(Map.of("id", user.getId(), "profileImageUrl", user.getProfileImageUrl()));
    }
}