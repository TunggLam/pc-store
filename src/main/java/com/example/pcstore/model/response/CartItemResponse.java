package com.example.pcstore.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Một sản phẩm trong giỏ hàng")
public class CartItemResponse {

    @Schema(description = "ID sản phẩm")
    private String productId;

    @Schema(description = "Tên sản phẩm", example = "CPU Intel Core i9-14900K")
    private String productName;

    @Schema(description = "Đơn giá (VNĐ)", example = "15990000")
    private int price;

    @Schema(description = "Số lượng", example = "2")
    private int quantity;

    @Schema(description = "URL ảnh sản phẩm")
    private String imageUrl;

    @Schema(description = "Thành tiền = đơn giá × số lượng (VNĐ)", example = "31980000")
    private BigDecimal totalPrice;

}

