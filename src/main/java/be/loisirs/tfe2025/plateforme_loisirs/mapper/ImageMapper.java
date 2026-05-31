package be.loisirs.tfe2025.plateforme_loisirs.mapper;

import be.loisirs.tfe2025.plateforme_loisirs.dto.image.ImageResponseDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ActivityImage;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ProductImage;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {

    public ImageResponseDTO toResponseDTO(ProductImage image) {
        return new ImageResponseDTO(image.getId(), image.getUrl());
    }

    public ImageResponseDTO toResponseDTO(ActivityImage image) {
        return new ImageResponseDTO(image.getId(), image.getUrl());
    }
}