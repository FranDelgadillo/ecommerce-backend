package com.shirtcompany.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

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
}