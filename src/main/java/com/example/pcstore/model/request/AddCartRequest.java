package com.example.pcstore.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCartRequest {

    @NotNull(message = "ID sản phẩm không được để trống")
    @NotEmpty(message = "ID sản phẩm không được để trống")
    private String productId;

    private Integer quantity = 1;
}

