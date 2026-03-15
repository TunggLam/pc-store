package com.example.pcstore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_history", indexes = {@Index(columnList = "username"), @Index(columnList = "paymentId")})
@EqualsAndHashCode(callSuper = false)
public class PaymentHistory extends BaseEntity {

    private String username;

    private String orderHistoryId;

    private String paymentId;

}

