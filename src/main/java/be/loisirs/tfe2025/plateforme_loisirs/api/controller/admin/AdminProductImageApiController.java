package be.loisirs.tfe2025.plateforme_loisirs.api.controller.admin;

import be.loisirs.tfe2025.plateforme_loisirs.dto.image.ImageResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ProductImage;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ImageMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.ProductImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products/{productId}/images")
public class AdminProductImageApiController {

    private final ProductImageService productImageService;
    private final ImageMapper imageMapper;

    public AdminProductImageApiController(ProductImageService productImageService,
                                          ImageMapper imageMapper) {
        this.productImageService = productImageService;
        this.imageMapper = imageMapper;
    }

    @PostMapping
    public ResponseEntity<ImageResponseDTO> addImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file
    ) {
        ProductImage image = productImageService.addImage(productId, file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(imageMapper.toResponseDTO(image));
    }

    @GetMapping
    public List<ImageResponseDTO> getImages(@PathVariable Long productId) {
        return productImageService.getImages(productId)
                .stream()
                .map(imageMapper::toResponseDTO)
                .toList();
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {
        productImageService.deleteImage(productId, imageId);

        return ResponseEntity.noContent().build();
    }
}