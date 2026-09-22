package be.loisirs.tfe2025.plateforme_loisirs.service;

import be.loisirs.tfe2025.plateforme_loisirs.entity.Product;
import be.loisirs.tfe2025.plateforme_loisirs.entity.ProductImage;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ProductImageRepository;
import be.loisirs.tfe2025.plateforme_loisirs.repository.ProductRepository;
import be.loisirs.tfe2025.plateforme_loisirs.util.VatRates;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public ProductService(ProductRepository productRepository,
                          ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Transactional
    public Product addProduct(Product product) {
        requireValidVatRate(product);

        Product savedProduct = productRepository.save(product);

        ProductImage defaultImage = new ProductImage();
        defaultImage.setUrl("/uploads/products/default-product.png");
        defaultImage.setProduct(savedProduct);
        productImageRepository.save(defaultImage);

        return savedProduct;
    }

    public Product updateProduct(Product product) {
        requireValidVatRate(product);

        return productRepository.save(product);
    }

    private void requireValidVatRate(Product product) {
        if (!VatRates.isAllowed(product.getVatRate())) {
            throw new IllegalArgumentException(
                    "Le taux de TVA doit être 0, 6, 12 ou 21 %.");
        }
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}