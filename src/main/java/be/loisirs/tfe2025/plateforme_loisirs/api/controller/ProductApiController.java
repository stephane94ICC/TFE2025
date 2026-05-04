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

    // ========================
    // GET ACTIVE PRODUCTS
    // Boutique publique
    // ========================
    @GetMapping
    public List<ProductDTO> getActiveProducts() {
        return productService.getActiveProducts()
                .stream()
                .map(productMapper::toDTO)
                .toList();
    }

    // ========================
    // GET ALL PRODUCTS
    // Plus tard : admin / partenaire
    // ========================
    @GetMapping("/all")
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts()
                .stream()
                .map(productMapper::toDTO)
                .toList();
    }

    // ========================
    // GET PRODUCT BY ID
    // ========================
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Product product = productService.getProduct(id);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(productMapper.toDTO(product));
    }

    // ========================
    // CREATE PRODUCT
    // Plus tard : admin / partenaire
    // ========================
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

    // ========================
    // UPDATE PRODUCT
    // Plus tard : admin / partenaire
    // ========================
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

    // ========================
    // DELETE PRODUCT
    // Plus tard : admin / partenaire
    // ========================
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
