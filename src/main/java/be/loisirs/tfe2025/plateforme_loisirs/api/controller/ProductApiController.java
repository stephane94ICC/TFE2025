package be.loisirs.tfe2025.plateforme_loisirs.api.controller;

import be.loisirs.tfe2025.plateforme_loisirs.dto.ProductDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Product;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ProductMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductApiController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    // Produits publics affichés dans la boutique
    @GetMapping
    public List<ProductDTO> getActiveProducts() {
        return productService.getActiveProducts()
                .stream()
                .map(productMapper::toDTO)
                .toList();
    }

    // Détail public d’un produit actif
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Product product = productService.getProduct(id);

        if (product == null || !Boolean.TRUE.equals(product.getActive())) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(productMapper.toDTO(product));
    }
}