package com.shirtcompany.product.controller;

import com.shirtcompany.product.dto.ProductResponseDTO;
import com.shirtcompany.product.dto.ReferenceResponseDTO;
import com.shirtcompany.product.model.Product;
import com.shirtcompany.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public Flux<ProductResponseDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Mono<ProductResponseDTO> getProductById(
            @Parameter(description = "ID del producto") @PathVariable("id") Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> createProduct(@RequestBody @Valid Product product) {
        return productService.createProduct(product);
    }

    @PostMapping(value = "/upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> uploadCsv(@RequestPart("file") Mono<FilePart> filePartMono) {
        return productService.processCsv(filePartMono)
                .thenReturn(ResponseEntity.ok("CSV procesado exitosamente"))
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error: " + e.getMessage())
                ));
    }

    @PutMapping("/{id}")
    public Mono<ProductResponseDTO> updateProduct(
            @Parameter(description = "ID del producto a actualizar", required = true)
            @PathVariable("id") Long id,
            @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(
            @Parameter(description = "ID del producto a eliminar", required = true)
            @PathVariable("id") Long id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/references")
    @Operation(
            summary = "Obtener referencias predefinidas",
            description = "Devuelve los IDs y nombres de tallas, colores y categorías disponibles",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Referencias obtenidas exitosamente"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public Mono<ReferenceResponseDTO> getReferences() {
        return productService.getReferences();
    }
}