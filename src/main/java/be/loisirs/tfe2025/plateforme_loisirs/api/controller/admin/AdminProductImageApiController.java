package be.loisirs.tfe2025.plateforme_loisirs.api.controller.admin;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ProductImage;
import be.loisirs.tfe2025.plateforme_loisirs.service.ProductImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/admin/products/{productId}/images")
public class AdminProductImageApiController {

    private final ProductImageService productImageService;

    public AdminProductImageApiController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) {

        ProductImage image = productImageService.addImage(productId, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "id", image.getId(),
                        "url", image.getUrl()
                )
        );
    }

    @GetMapping
    public List<Map<String, Object>> getImages(@PathVariable Long productId) {
        return productImageService.getImages(productId)
                .stream()
                .map(image -> Map.<String, Object>of(
                        "id", image.getId(),
                        "url", image.getUrl()
                ))
                .toList();
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {

        productImageService.deleteImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }
}