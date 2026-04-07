package com.example.pcstore.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderItemResponse {

    private String productId;
    private String productName;
    private String imageUrl;
    private int price;
    private int quantity;
    private BigDecimal totalPrice;
}
