package com.shirtcompany.product.service;

import jakarta.validation.Path;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final java.nio.file.Path rootLocation = Paths.get("uploads");

    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    public Mono<String> store(FilePart filePart, Long productId) {
        String filename = filePart.filename();
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        if (!List.of("jpg", "jpeg", "png", "webp").contains(extension)) {
            return Mono.error(new IllegalArgumentException("Solo se permiten archivos JPG, JPEG , PNG o WebP"));
        }

        String newFilename = productId + "_" + UUID.randomUUID() + "." + extension;
        java.nio.file.Path destination = rootLocation.resolve(newFilename);

        return filePart.transferTo(destination)
                .thenReturn(newFilename);
    }

    private Mono<Void> transferTo(FilePart filePart, Path destination) {
        return Mono.fromRunnable(() -> {
            try {
                filePart.transferTo((File) destination).block();
            } catch (Exception e) {
                throw new RuntimeException("Failed to store file: " + destination, e);
            }
        });
    }

    public Mono<Resource> loadAsResource(String filename) {
        try {
            java.nio.file.Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return Mono.just(resource);
            } else {
                return Mono.error(new RuntimeException("Could not read file: " + filename));
            }
        } catch (MalformedURLException e) {
            return Mono.error(new RuntimeException("Could not read file: " + filename, e));
        }
    }

    public Mono<Void> delete(String filename) {
        return Mono.fromRunnable(() -> {
            try {
                java.nio.file.Path file = rootLocation.resolve(filename);
                Files.deleteIfExists(file);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file: " + filename, e);
            }
        });
    }
}
