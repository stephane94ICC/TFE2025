package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.api.exception.ResourceNotFoundException;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Product;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ProductImage;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ProductImageRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductImageService {

    private static final String DEFAULT_PRODUCT_IMAGE =
            "/uploads/products/default-product.png";

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

    @Transactional
    public ProductImage addImage(Long productId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable."));

        String imageUrl = imageStorageService.storeImage(file, "products", productId);

        removeDefaultImage(product);

        ProductImage productImage = new ProductImage();
        productImage.setUrl(imageUrl);
        productImage.setProduct(product);

        product.getImages().add(productImage);
        return productImageRepository.save(productImage);
    }

    public List<ProductImage> getImages(Long productId) {
        return productImageRepository.findByProductId(productId);
    }

    @Transactional
    public void deleteImage(Long productId, Long imageId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable."));

        ProductImage productImage = product.getImages()
                .stream()
                .filter(image -> image.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Image introuvable."));

        if (DEFAULT_PRODUCT_IMAGE.equals(productImage.getUrl())) {
            throw new IllegalArgumentException("L'image par défaut ne peut pas être supprimée.");
        }

        imageStorageService.deleteImage(productImage.getUrl());
        product.getImages().remove(productImage);

        if (product.getImages().isEmpty()) {
            ProductImage defaultImage = new ProductImage();
            defaultImage.setUrl(DEFAULT_PRODUCT_IMAGE);
            defaultImage.setProduct(product);
            product.getImages().add(defaultImage);
            productImageRepository.save(defaultImage);
        }
    }

    private void removeDefaultImage(Product product) {
        product.getImages().removeIf(
                image -> DEFAULT_PRODUCT_IMAGE.equals(image.getUrl())
        );
    }
}
