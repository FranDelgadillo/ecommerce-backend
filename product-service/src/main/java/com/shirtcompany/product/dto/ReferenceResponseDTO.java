package com.shirtcompany.product.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReferenceResponseDTO {
    private List<SizeReferenceDTO> sizes;
    private List<ColorReferenceDTO> colors;
    private List<CategoryReferenceDTO> categories;

    @Data
    public static class SizeReferenceDTO {
        private Long id;
        private String name;
    }

    @Data
    public static class ColorReferenceDTO {
        private Long id;
        private String name;
    }

    @Data
    public static class CategoryReferenceDTO {
        private Long id;
        private String name;
    }
}
