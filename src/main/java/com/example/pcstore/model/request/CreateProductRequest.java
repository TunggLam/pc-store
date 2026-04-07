package com.example.pcstore.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin tạo sản phẩm mới (multipart/form-data)")
public class CreateProductRequest {

    @Schema(description = "Tên sản phẩm", example = "CPU Intel Core i9-14900K", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Tên sản phẩm không được để trống")
    @NotEmpty(message = "Tên sản phẩm không được để trống")
    private String name;

    @Schema(description = "Mô tả chi tiết sản phẩm", example = "Bộ vi xử lý Intel thế hệ 14 với 24 nhân")
    private String description;

    @Schema(description = "Ảnh sản phẩm (file upload)", type = "string", format = "binary")
    private MultipartFile image;

    @Schema(description = "Số lượng tồn kho", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Số lượng sản phẩm không được để trống")
    private int quantity;

    @Schema(description = "Giá bán (VNĐ)", example = "15990000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Số tiền sản phẩm không được để trống")
    private BigDecimal price;

    @Schema(description = "ID danh mục sản phẩm", example = "uuid-category", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Mã loại sản phẩm không được để trống")
    @NotEmpty(message = "Mã loại sản phẩm không được để trống")
    private String categoryId;

}

