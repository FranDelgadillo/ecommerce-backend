package com.shirtcompany.product.controller;

import com.shirtcompany.product.service.FileStorageService;
import com.shirtcompany.product.service.ImageStorageService;
import com.shirtcompany.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/products")
public class ImageStorageController {

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private FileStorageService fileStorageService;

    @Operation(summary = "Subir imágenes para un producto",
            description = "Permite subir hasta 5 imágenes para un producto")
    @ApiResponse(responseCode = "200", description = "Imágenes subidas exitosamente")
    @ApiResponse(responseCode = "400", description = "Error en formato de archivo o límite excedido")
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> uploadImages(
            @Parameter(description = "ID del producto", required = true, example = "1")
            @PathVariable("id") Long id,

            @Parameter(description = "Archivos de imagen (máx. 5)",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))))
            @RequestPart("files") Flux<FilePart> files) {

        return files.limitRate(5)
                .flatMap(file -> fileStorageService.store(file, id)
                        .flatMap(filename -> imageStorageService.addProductImage(id, filename)))
                .collectList()
                .map(results -> ResponseEntity.ok("Imágenes subidas exitosamente"))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @Operation(summary = "Eliminar una imagen específica",
            description = "Elimina una imagen específica de un producto")
    @ApiResponse(responseCode = "204", description = "Imagen eliminada")
    @ApiResponse(responseCode = "404", description = "Producto o imagen no encontrada")
    @DeleteMapping("/{productId}/images/{imageName}")
    public Mono<ResponseEntity<Void>> deleteImage(
            @Parameter(description = "ID del producto", required = true, example = "123")
            @PathVariable("productId") Long productId,

            @Parameter(description = "Nombre del archivo de imagen",
                    required = true,
                    example = "123_abcd.jpg")
            @PathVariable("imageName") String imageName) {

        return imageStorageService.removeProductImage(productId, imageName)
                .thenReturn(ResponseEntity.noContent().<Void>build())
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @Operation(summary = "Obtener una imagen",
            description = "Devuelve el archivo de imagen solicitado")
    @ApiResponse(responseCode = "200", description = "Imagen encontrada",
            content = @Content(mediaType = "image/*"))
    @ApiResponse(responseCode = "404", description = "Imagen no encontrada")
    @GetMapping("/images/{filename:.+}")
    public Mono<ResponseEntity<Resource>> getImage(
            @Parameter(description = "Nombre completo del archivo",
                    required = true,
                    example = "123_abcd.jpg")
            @PathVariable("filename") String filename) {

        return fileStorageService.loadAsResource(filename)
                .map(resource -> {
                    String contentType = determineContentType(filename);
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                            .body(resource);
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }

    @Operation(summary = "Eliminar todas las imágenes",
            description = "Elimina todas las imágenes asociadas a un producto")
    @ApiResponse(responseCode = "204", description = "Imágenes eliminadas")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @DeleteMapping("/{id}/images")
    public Mono<ResponseEntity<Void>> deleteAllImages(
            @Parameter(description = "ID del producto", required = true, example = "123")
            @PathVariable("id") Long id) {

        return imageStorageService.deleteAllProductImages(id)
                .thenReturn(ResponseEntity.noContent().<Void>build())
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }
}