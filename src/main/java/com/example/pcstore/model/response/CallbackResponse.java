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
public class CallbackResponse {

    private BigInteger amount;
    private String responseCode;
    private String message;
    private String invoiceNo;
    private String paymentTime;

}

