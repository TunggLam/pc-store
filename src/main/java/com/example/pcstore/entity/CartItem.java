package com.example.pcstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_item", indexes = {@Index(columnList = "username")})
public class CartItem extends BaseEntity {

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "cart_id", nullable = false)
    private String cartId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;
}



