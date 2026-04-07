package com.example.pcstore.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin sản phẩm")
public class ProductResponse {

    @Schema(description = "ID sản phẩm (UUID)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private String id;

    @Schema(description = "URL ảnh sản phẩm (lưu trên MinIO)")
    private String imageUrl;

    @Schema(description = "Tên sản phẩm", example = "CPU Intel Core i9-14900K")
    private String name;

    @Schema(description = "Giá bán (VNĐ)", example = "15990000")
    private BigDecimal price;

    @Schema(description = "Số lượng tồn kho", example = "50")
    private Integer quantity;

    @Schema(description = "Mô tả sản phẩm")
    private String description;

    @Schema(description = "Danh mục sản phẩm")
    private CategoryResponse category;

}
