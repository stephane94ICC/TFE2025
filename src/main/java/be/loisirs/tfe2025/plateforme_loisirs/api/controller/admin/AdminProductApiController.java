package be.loisirs.tfe2025.plateforme_loisirs.api.controller.admin;

import be.loisirs.tfe2025.plateforme_loisirs.dto.ProductDTO;
import be.loisirs.tfe2025.plateforme_loisirs.entity.Product;
import be.loisirs.tfe2025.plateforme_loisirs.mapper.ProductMapper;
import be.loisirs.tfe2025.plateforme_loisirs.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductApiController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public AdminProductApiController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    // Liste complète des produits pour l’administration
    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts()
                .stream()
                .map(productMapper::toDTO)
                .toList();
    }

    // Détail d’un produit pour l’administration
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Product product = productService.getProduct(id);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(productMapper.toDTO(product));
    }

    // Création d’un produit
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        if (dto.getPrice() == null) {
            return ResponseEntity.badRequest().build();
        }

        Product product = productMapper.toEntity(dto);
        Product savedProduct = productService.addProduct(product);

        return ResponseEntity.ok(productMapper.toDTO(savedProduct));
    }

    // Modification d’un produit
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        Product existing = productService.getProduct(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        productMapper.updateEntity(dto, existing);
        Product updatedProduct = productService.updateProduct(existing);

        return ResponseEntity.ok(productMapper.toDTO(updatedProduct));
    }

    // Suppression d’un produit
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Product existing = productService.getProduct(id);

        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
}
