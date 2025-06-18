package com.shirtcompany.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String size;
    private String color;
    private String category;
    private String description;
    private Double price;
    private Integer stock;
    private List<String> imageUrls;
}