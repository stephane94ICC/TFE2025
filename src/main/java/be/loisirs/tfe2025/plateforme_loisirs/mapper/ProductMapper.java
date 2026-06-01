package be.loisirs.tfe2025.plateforme_loisirs.mapper;

import be.loisirs.tfe2025.plateforme_loisirs.dto.ProductDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setActive(product.getActive());
        dto.setCreatedAt(product.getCreatedAt());

        if (product.getImages() != null) {
            dto.setImageUrls(
                    product.getImages()
                            .stream()
                            .map(image -> image.getUrl())
                            .toList()
            );
        }

        return dto;
    }

    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setActive(dto.getActive());
        product.setCreatedAt(dto.getCreatedAt());

        return product;
    }

    public void updateEntity(ProductDTO dto, Product existing) {
        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        if (dto.getPrice() != null) {
            existing.setPrice(dto.getPrice());
        }

        if (dto.getStockQuantity() != null) {
            existing.setStockQuantity(dto.getStockQuantity());
        }

        if (dto.getActive() != null) {
            existing.setActive(dto.getActive());
        }
    }
}