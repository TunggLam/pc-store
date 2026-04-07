package com.example.pcstore.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sản phẩm bán chạy nhất")
public class TopSellingProductItem {

    @Schema(description = "ID sản phẩm")
    private String productId;

    @Schema(description = "Tên sản phẩm", example = "RAM Gskill Trident Z 16GB")
    private String productName;

    @Schema(description = "URL ảnh sản phẩm")
    private String imageUrl;

    @Schema(description = "Tổng số lượng đã bán", example = "25")
    private long totalSold;

    @Schema(description = "Tổng doanh thu từ sản phẩm này (VNĐ)", example = "50000000")
    private BigDecimal totalRevenue;
}
