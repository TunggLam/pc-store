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
@Schema(description = "Sản phẩm trong đơn hàng")
public class OrderHistoryItemResponse {

    @Schema(description = "Tên sản phẩm", example = "RAM Gskill Trident Z 16GB")
    private String productName;

    @Schema(description = "URL ảnh sản phẩm")
    private String imageUrl;

    @Schema(description = "Đơn giá (VNĐ)", example = "1990000")
    private int price;

    @Schema(description = "Số lượng", example = "2")
    private int quantity;

    @Schema(description = "Thành tiền (VNĐ)", example = "3980000")
    private BigDecimal totalPrice;
}
