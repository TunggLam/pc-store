package com.example.pcstore.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private String id;

    private String imageUrl;

    private String name;

    private BigDecimal price;

    private Integer quantity;

    private String description;

    private CategoryResponse category;

}
