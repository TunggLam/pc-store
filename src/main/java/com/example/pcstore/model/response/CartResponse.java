package com.example.pcstore.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin giỏ hàng của người dùng")
public class CartResponse {

    @Schema(description = "ID giỏ hàng (UUID)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private String cartId;

    @Schema(description = "Danh sách sản phẩm trong giỏ")
    private List<CartItemResponse> cartItems = new ArrayList<>();

    @Schema(description = "Tổng tiền của đơn hàng (VNĐ)", example = "31980000")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Schema(description = "Địa chỉ giao hàng mặc định của tài khoản", example = "123 Nguyễn Huệ, Q1, TP.HCM")
    private String address;

    public CartResponse(String address) {
        this.address = address;
    }
}

