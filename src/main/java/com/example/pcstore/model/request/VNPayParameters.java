package com.example.pcstore.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VNPayParameters {

    @JsonProperty("vnp_Amount")
    private BigInteger amount;
    @JsonProperty("vnp_BankCode")
    private String bankCode;
    @JsonProperty("vnp_BankTranNo")
    private String bankTranNo;
    @JsonProperty("vnp_CardType")
    private String cardType;
    @JsonProperty("vnp_OrderInfo")
    private String orderInfo;
    @JsonProperty("vnp_PayDate")
    private String payDate;
    @JsonProperty("vnp_ResponseCode")
    private String responseCode;
    @JsonProperty("vnp_SecureHash")
    private String secureHash;
    @JsonProperty("vnp_TmnCode")
    private String tmnCode;
    @JsonProperty("vnp_TransactionNo")
    private String transactionNo;
    @JsonProperty("vnp_TransactionStatus")
    private String transactionStatus;
    @JsonProperty("vnp_TxnRef")
    private String txnRef;

}

