package com.example.pcstore.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotNull(message = "Tên sản phẩm không được để trống")
    @NotEmpty(message = "Tên sản phẩm không được để trống")
    private String name;

    private String description;

    private MultipartFile image;

    @NotNull(message = "Số lượng sản phẩm không được để trống")
    private int quantity;

    @NotNull(message = "Số tiền sản phẩm không được để trống")
    private BigDecimal price;

    @NotNull(message = "Mã loại sản phẩm không được để trống")
    @NotEmpty(message = "Mã loại sản phẩm không được để trống")
    private String categoryId;

}

