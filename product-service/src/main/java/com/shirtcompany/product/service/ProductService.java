package com.shirtcompany.product.service;

import com.shirtcompany.product.dto.ProductResponseDTO;
import com.shirtcompany.product.dto.ReferenceResponseDTO;
import com.shirtcompany.product.model.*;
import com.shirtcompany.product.repository.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GenderRepository genderRepository;

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

    public Mono<Void> processCsv(Mono<FilePart> filePartMono) {
        return filePartMono.flatMapMany(filePart -> filePart.content())
                .collectList()
                .flatMap(dataBuffers -> {
                    byte[] bytes = dataBuffers.stream()
                            .map(dataBuffer -> {
                                byte[] array = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(array);
                                DataBufferUtils.release(dataBuffer);
                                return array;
                            })
                            .reduce(new byte[0], (a, b) -> {
                                byte[] combined = new byte[a.length + b.length];
                                System.arraycopy(a, 0, combined, 0, a.length);
                                System.arraycopy(b, 0, combined, a.length, b.length);
                                return combined;
                            });

                    return Mono.fromCallable(() -> {
                        List<String> errors = new ArrayList<>();
                        try (CSVParser parser = CSVParser.parse(
                                new InputStreamReader(new ByteArrayInputStream(bytes)),
                                CSVFormat.DEFAULT.builder()
                                        .setHeader("name", "size_id", "color_id", "category_id", "gender_id", "description", "price", "stock")
                                        .setSkipHeaderRecord(true)
                                        .setTrim(true)
                                        .build())) {

                            Flux.fromIterable(parser)
                                    .flatMap(csvRecord -> processCsvRecord(csvRecord, errors))
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .blockLast();
                        }

                        if (!errors.isEmpty()) {
                            throw new RuntimeException("Errores en CSV:\n" + String.join("\n", errors));
                        }
                        return null;
                    }).subscribeOn(Schedulers.boundedElastic());
                })
                .then();
    }

    private Mono<Void> processCsvRecord(CSVRecord csvRecord, List<String> errors) {
        try {
            Product product = new Product();
            product.setName(csvRecord.get("name"));

            Long sizeId = Long.parseLong(csvRecord.get("size_id"));
            Long colorId = Long.parseLong(csvRecord.get("color_id"));
            Long categoryId = Long.parseLong(csvRecord.get("category_id"));
            Long genderId = Long.parseLong(csvRecord.get("gender_id"));

            Mono<Boolean> sizeExists = sizeRepository.existsById(sizeId);
            Mono<Boolean> colorExists = colorRepository.existsById(colorId);
            Mono<Boolean> categoryExists = categoryRepository.existsById(categoryId);
            Mono<Boolean> genderExists = genderRepository.existsById(genderId);

            return Mono.zip(sizeExists, colorExists, categoryExists, genderExists)
                    .flatMap(tuple -> {
                        boolean sizeValid = tuple.getT1();
                        boolean colorValid = tuple.getT2();
                        boolean categoryValid = tuple.getT3();
                        boolean genderValid = tuple.getT4();

                        List<String> recordErrors = new ArrayList<>();
                        if (!sizeValid) recordErrors.add("Talla ID " + sizeId + " no existe");
                        if (!colorValid) recordErrors.add("Color ID " + colorId + " no existe");
                        if (!categoryValid) recordErrors.add("Categoría ID " + categoryId + " no existe");
                        if (!genderValid) recordErrors.add("Gender ID " + genderId + " no existe");

                        if (!recordErrors.isEmpty()) {
                            errors.add("Fila " + csvRecord.getRecordNumber() + ": " + String.join(", ", recordErrors));
                            return Mono.empty();
                        }

                        product.setSizeId(sizeId);
                        product.setColorId(colorId);
                        product.setCategoryId(categoryId);
                        product.setGenderId(genderId);

                        product.setDescription(csvRecord.get("description"));
                        product.setPrice(Double.parseDouble(csvRecord.get("price")));
                        product.setStock(Integer.parseInt(csvRecord.get("stock")));

                        return productRepository.save(product).then();
                    })
                    .onErrorResume(e -> {
                        errors.add("Fila " + csvRecord.getRecordNumber() + ": Error de formato - " + e.getMessage());
                        return Mono.empty();
                    });
        } catch (Exception e) {
            errors.add("Fila " + csvRecord.getRecordNumber() + ": Error de formato - " + e.getMessage());
            return Mono.empty();
        }
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
                    existingProduct.setGenderId(updatedProduct.getGenderId());
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

        Mono<String> genderName = genderRepository.findById(product.getGenderId())
                .map(Gender::getName)
                .defaultIfEmpty("N/A");

        List<String> imageUrls = product.getImageList();

        return Mono.zip(sizeName, colorName, categoryName, genderName)
                .map(tuple -> new ProductResponseDTO(
                        product.getId(),
                        product.getName(),
                        tuple.getT1(),
                        tuple.getT2(),
                        tuple.getT3(),
                        tuple.getT4(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getStock(),
                        imageUrls
                ));
    }

    public Mono<ReferenceResponseDTO> getReferences() {
        Mono<List<ReferenceResponseDTO.SizeReferenceDTO>> sizes = sizeRepository.findAll()
                .map(size -> {
                    ReferenceResponseDTO.SizeReferenceDTO dto = new ReferenceResponseDTO.SizeReferenceDTO();
                    dto.setId(size.getId());
                    dto.setName(size.getName());
                    return dto;
                })
                .collectList();

        Mono<List<ReferenceResponseDTO.ColorReferenceDTO>> colors = colorRepository.findAll()
                .map(color -> {
                    ReferenceResponseDTO.ColorReferenceDTO dto = new ReferenceResponseDTO.ColorReferenceDTO();
                    dto.setId(color.getId());
                    dto.setName(color.getName());
                    return dto;
                })
                .collectList();

        Mono<List<ReferenceResponseDTO.CategoryReferenceDTO>> categories = categoryRepository.findAll()
                .map(category -> {
                    ReferenceResponseDTO.CategoryReferenceDTO dto = new ReferenceResponseDTO.CategoryReferenceDTO();
                    dto.setId(category.getId());
                    dto.setName(category.getName());
                    return dto;
                })
                .collectList();

        Mono<List<ReferenceResponseDTO.GenderReferenceDTO>> genders = genderRepository.findAll()
                .map(gender -> {
                    ReferenceResponseDTO.GenderReferenceDTO dto = new ReferenceResponseDTO.GenderReferenceDTO();
                    dto.setId(gender.getId());
                    dto.setName(gender.getName());
                    return dto;
                })
                .collectList();

        return Mono.zip(sizes, colors, categories, genders)
                .map(tuple -> {
                    ReferenceResponseDTO response = new ReferenceResponseDTO();
                    response.setSizes(tuple.getT1());
                    response.setColors(tuple.getT2());
                    response.setCategories(tuple.getT3());
                    response.setGenders(tuple.getT4());
                    return response;
                });
    }
}