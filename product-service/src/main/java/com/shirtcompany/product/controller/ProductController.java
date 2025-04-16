package com.shirtcompany.product.controller;

import com.shirtcompany.product.dto.ProductResponseDTO;
import com.shirtcompany.product.model.Product;
import com.shirtcompany.product.service.ProductService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/listAll")
    public Flux<ProductResponseDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/getProductById/{id}")
    public Mono<ProductResponseDTO> getProductById(
            @Parameter(description = "ID del producto") @PathVariable("id") Long id) {
        return productService.getProductById(id);
    }

    @PostMapping("/create")
    public Mono<Product> createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/updateProductById/{id}")
    public Mono<ProductResponseDTO> updateProduct(
            @Parameter(description = "ID del producto a actualizar", required = true)
            @PathVariable("id") Long id,
            @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/deleteProductById/{id}")
    public Mono<Void> deleteProduct(
            @Parameter(description = "ID del producto a eliminar", required = true)
            @PathVariable("id") Long id) {
        return productService.deleteProduct(id);
    }
}