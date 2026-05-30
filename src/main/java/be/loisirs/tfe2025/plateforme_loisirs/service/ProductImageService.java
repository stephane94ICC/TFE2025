package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Product;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ProductImage;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ProductImageRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ProductRepository;
import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ImageStorageService imageStorageService;

    public ProductImageService(ProductRepository productRepository,
                               ProductImageRepository productImageRepository,
                               ImageStorageService imageStorageService) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.imageStorageService = imageStorageService;
    }

    public ProductImage addImage(Long productId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable."));

        String imageUrl = imageStorageService.storeImage(file, "products", productId);

        ProductImage productImage = new ProductImage();
        productImage.setUrl(imageUrl);
        productImage.setProduct(product);

        return productImageRepository.save(productImage);
    }

    public List<ProductImage> getImages(Long productId) {
        return productImageRepository.findByProductId(productId);
    }

    public void deleteImage(Long productId, Long imageId) {
        ProductImage productImage = productImageRepository
                .findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Image introuvable."));
        if ("/uploads/products/default-product.png".equals(productImage.getUrl())) {
            throw new IllegalArgumentException(
                    "L'image par défaut ne peut pas être supprimée."
            );
        }
        imageStorageService.deleteImage(productImage.getUrl());
        productImageRepository.delete(productImage);
    }
}