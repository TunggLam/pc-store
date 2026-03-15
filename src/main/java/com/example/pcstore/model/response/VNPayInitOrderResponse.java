package com.example.pcstore.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VNPayInitOrderResponse {

    /**
     * Đường link dẫn đến web thanh toán của VN Pay
     */
    @JsonProperty("target")
    private String target;

    /**
     * Mã đơn hàng
     * Tên trường được đặt theo param của VNPAY cho dễ tìm kiếm và trace
     */
    @JsonProperty("txnRef")
    private String txnRef;

}
