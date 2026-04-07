package com.example.pcstore.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sản phẩm sắp hết hàng")
public class LowStockProductItem {

    @Schema(description = "ID sản phẩm")
    private String productId;

    @Schema(description = "Tên sản phẩm", example = "Case Lian Li O11 Dynamic")
    private String productName;

    @Schema(description = "URL ảnh sản phẩm")
    private String imageUrl;

    @Schema(description = "Số lượng tồn kho còn lại", example = "2")
    private int quantity;

    @Schema(description = "Tên danh mục", example = "Case PC")
    private String categoryName;
}
