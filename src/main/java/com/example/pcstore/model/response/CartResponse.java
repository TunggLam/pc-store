package com.example.pcstore.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private String cartId;
    private List<CartItemResponse> cartItems = new ArrayList<>();
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private String address;

    public CartResponse(String address) {
        this.address = address;
    }
}

