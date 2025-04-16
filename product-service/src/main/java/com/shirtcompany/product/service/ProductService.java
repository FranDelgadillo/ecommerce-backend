package com.shirtcompany.product.service;

import com.shirtcompany.product.dto.ProductResponseDTO;
import com.shirtcompany.product.model.Product;
import com.shirtcompany.product.model.Size;
import com.shirtcompany.product.model.Color;
import com.shirtcompany.product.model.Category;
import com.shirtcompany.product.repository.ProductRepository;
import com.shirtcompany.product.repository.SizeRepository;
import com.shirtcompany.product.repository.ColorRepository;
import com.shirtcompany.product.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Flux<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .flatMap(this::mapToDTO);
    }

    public Mono<ProductResponseDTO> getProductById(Long id) {
        return productRepository.findById(id)
                .flatMap(this::mapToDTO);
    }

    public Mono<Product> createProduct(Product product) {
        return productRepository.save(product);
    }

    public Mono<ProductResponseDTO> updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    existingProduct.setName(updatedProduct.getName());
                    existingProduct.setDescription(updatedProduct.getDescription());
                    existingProduct.setPrice(updatedProduct.getPrice());
                    existingProduct.setStock(updatedProduct.getStock());
                    existingProduct.setSizeId(updatedProduct.getSizeId());
                    existingProduct.setColorId(updatedProduct.getColorId());
                    existingProduct.setCategoryId(updatedProduct.getCategoryId());
                    return productRepository.save(existingProduct);
                })
                .flatMap(this::mapToDTO);
    }

    public Mono<Void> deleteProduct(Long id) {
        return productRepository.deleteById(id);
    }

    private Mono<ProductResponseDTO> mapToDTO(Product product) {
        Mono<String> sizeName = sizeRepository.findById(product.getSizeId())
                .map(Size::getName)
                .defaultIfEmpty("N/A");

        Mono<String> colorName = colorRepository.findById(product.getColorId())
                .map(Color::getName)
                .defaultIfEmpty("N/A");

        Mono<String> categoryName = categoryRepository.findById(product.getCategoryId())
                .map(Category::getName)
                .defaultIfEmpty("N/A");

        return Mono.zip(sizeName, colorName, categoryName)
                .map(tuple -> new ProductResponseDTO(
                        product.getId(),
                        product.getName(),
                        tuple.getT1(),
                        tuple.getT2(),
                        tuple.getT3(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getStock()
                ));
    }
}