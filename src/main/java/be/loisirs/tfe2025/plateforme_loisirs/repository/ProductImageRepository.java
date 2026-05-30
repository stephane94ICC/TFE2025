package be.loisirs.tfe2025.plateforme_loisirs.repository;

import be.loisirs.tfe2025.plateforme_loisirs.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductId(Long productId);
    Optional<ProductImage> findByIdAndProductId(Long imageId, Long productId);
}
