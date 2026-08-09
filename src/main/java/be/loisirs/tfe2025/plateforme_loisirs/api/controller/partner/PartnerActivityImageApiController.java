package be.loisirs.tfe2025.plateforme_loisirs.api.controller.partner;

import be.loisirs.tfe2025.plateforme_loisirs.dto.image.ImageResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityImage;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ImageMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.ActivityImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/partner/activities/{activityId}/images")
public class PartnerActivityImageApiController {

    private final ActivityImageService activityImageService;
    private final ImageMapper imageMapper;

    public PartnerActivityImageApiController(
            ActivityImageService activityImageService,
            ImageMapper imageMapper
    ) {
        this.activityImageService = activityImageService;
        this.imageMapper = imageMapper;
    }

    @PostMapping
    public ResponseEntity<ImageResponseDTO> addImage(
            @PathVariable Long activityId,
            @RequestParam("file") MultipartFile file,
            Principal principal
    ) {
        ActivityImage image = activityImageService.addImage(activityId, principal.getName(), file);
        return ResponseEntity.status(HttpStatus.CREATED).body(imageMapper.toResponseDTO(image));
    }

    @GetMapping
    public List<ImageResponseDTO> getImages(
            @PathVariable Long activityId,
            Principal principal
    ) {
        return activityImageService.getImages(activityId, principal.getName())
                .stream()
                .map(imageMapper::toResponseDTO)
                .toList();
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long activityId,
            @PathVariable Long imageId,
            Principal principal
    ) {
        activityImageService.deleteImage(activityId, imageId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}