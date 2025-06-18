package com.shirtcompany.product.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("products")
public class Product {
    @Id
    private Long id;
    private String name;
    private Long sizeId;
    private Long colorId;
    private Long categoryId;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrls;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> getImageList() {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(imageUrls, new TypeReference<List<String>>(){});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing image URLs", e);
        }
    }

    public void setImageList(List<String> images) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.imageUrls = mapper.writeValueAsString(images);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing image URLs", e);
        }
    }
}