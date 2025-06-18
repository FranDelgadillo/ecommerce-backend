package com.shirtcompany.product.service;

import com.shirtcompany.product.model.Product;
import com.shirtcompany.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageStorageService {
    private final Path rootLocation = Paths.get("uploads");
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public ImageStorageService(ProductRepository productRepository,
                               FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
        this.fileStorageService.init();
    }

    public Mono<Product> addProductImage(Long productId, String imageFilename) {
        return productRepository.findById(productId)
                .flatMap(product -> {
                    List<String> images = product.getImageList();

                    if (images.size() >= 5) {
                        return Mono.error(new RuntimeException("Máximo 5 imágenes por producto"));
                    }

                    images.add(imageFilename);
                    product.setImageList(images);
                    return productRepository.save(product);
                });
    }

    public Mono<Void> removeProductImage(Long productId, String imageFilename) {
        return productRepository.findById(productId)
                .flatMap(product -> {
                    List<String> images = product.getImageList();

                    if (images.remove(imageFilename)) {
                        product.setImageList(images);
                        return productRepository.save(product)
                                .then(fileStorageService.delete(imageFilename))
                                .then();
                    }
                    return Mono.error(new RuntimeException("Imagen no encontrada"));
                });
    }

    public Mono<Void> deleteAllProductImages(Long productId) {
        return productRepository.findById(productId)
                .flatMap(product -> {
                    List<String> images = product.getImageList();
                    product.setImageList(new ArrayList<>());
                    return productRepository.save(product)
                            .thenMany(Flux.fromIterable(images))
                            .flatMap(fileStorageService::delete)
                            .then();
                });
    }

    public Mono<Resource> loadImageResource(String filename) {
        return fileStorageService.loadAsResource(filename);
    }
}