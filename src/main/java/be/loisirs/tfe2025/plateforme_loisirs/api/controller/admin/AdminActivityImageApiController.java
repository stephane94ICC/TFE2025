package be.loisirs.tfe2025.plateforme_loisirs.api.controller.admin;

import be.loisirs.tfe2025.plateforme_loisirs.dto.image.ImageResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityImage;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ImageMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.ActivityImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/activities/{activityId}/images")
public class AdminActivityImageApiController {

    private final ActivityImageService activityImageService;
    private final ImageMapper imageMapper;

    public AdminActivityImageApiController(
            ActivityImageService activityImageService,
            ImageMapper imageMapper
    ) {
        this.activityImageService = activityImageService;
        this.imageMapper = imageMapper;
    }

    @PostMapping
    public ResponseEntity<ImageResponseDTO> addImage(
            @PathVariable Long activityId,
            @RequestParam("file") MultipartFile file
    ) {
        ActivityImage image = activityImageService.addImage(activityId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(imageMapper.toResponseDTO(image));
    }

    @GetMapping
    public List<ImageResponseDTO> getImages(@PathVariable Long activityId) {
        return activityImageService.getImages(activityId)
                .stream()
                .map(imageMapper::toResponseDTO)
                .toList();
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long activityId,
            @PathVariable Long imageId
    ) {
        activityImageService.deleteImage(activityId, imageId);
        return ResponseEntity.noContent().build();
    }
}