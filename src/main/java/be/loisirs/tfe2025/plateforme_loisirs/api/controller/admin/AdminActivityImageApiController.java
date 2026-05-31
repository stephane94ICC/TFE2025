package be.loisirs.tfe2025.plateforme_loisirs.api.controller.admin;

import be.loisirs.tfe2025.plateforme_loisirs.dto.image.ImageResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityImage;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ImageMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.ActivityImageService;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController @RequestMapping("/api/admin/activities/{activityId}/images")
public class AdminActivityImageApiController {
 private final ActivityImageService service;
 private final ImageMapper mapper;

 public AdminActivityImageApiController(ActivityImageService service, ImageMapper mapper) {
  this.service=service; this.mapper=mapper;
 }

 @PostMapping
  public ResponseEntity<ImageResponseDTO> addImage(@PathVariable Long activityId,@RequestParam("file") MultipartFile file) {
   ActivityImage image=service.addImage(activityId,file); return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDTO(image)); }

 @GetMapping
  public List<ImageResponseDTO> getImages(@PathVariable Long activityId) {
   return service.getImages(activityId).stream().map(mapper::toResponseDTO).toList();
 }

 @DeleteMapping("/{imageId}")
 public ResponseEntity<Void> deleteImage(@PathVariable Long activityId,@PathVariable Long imageId) {
  service.deleteImage(activityId,imageId); return ResponseEntity.noContent().build(); }
}