package com.example.pcstore.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderPaymentResponse {

    private String paymentId;
    private String invoiceNo;
    private String cardType;
    private BigInteger amount;
    private String transactionNo;
    private String transactionStatus;
    private String transactionCreateDate;
    private String status;
}
