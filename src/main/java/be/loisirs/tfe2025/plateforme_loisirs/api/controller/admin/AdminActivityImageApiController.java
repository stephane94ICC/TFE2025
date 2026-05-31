package be.loisirs.tfe2025.plateforme_loisirs.api.controller.admin;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityImage;
import be.loisirs.tfe2025.plateforme_loisirs.service.ActivityImageService;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.multipart.MultipartFile;
import java.util.*;
@RestController @RequestMapping("/api/admin/activities/{activityId}/images")
public class AdminActivityImageApiController {
 private final ActivityImageService service;
 public AdminActivityImageApiController(ActivityImageService service) { this.service = service; }
 @PostMapping public ResponseEntity<Map<String,Object>> addImage(@PathVariable Long activityId,@RequestParam("file") MultipartFile file) { ActivityImage image=service.addImage(activityId,file); return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id",image.getId(),"url",image.getUrl())); }
 @GetMapping public List<Map<String,Object>> getImages(@PathVariable Long activityId) { return service.getImages(activityId).stream().map(image -> Map.<String,Object>of("id",image.getId(),"url",image.getUrl())).toList(); }
 @DeleteMapping("/{imageId}") public ResponseEntity<Void> deleteImage(@PathVariable Long activityId,@PathVariable Long imageId) { service.deleteImage(activityId,imageId); return ResponseEntity.noContent().build(); }
}