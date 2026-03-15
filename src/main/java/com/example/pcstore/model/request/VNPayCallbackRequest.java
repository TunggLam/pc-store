package com.example.pcstore.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VNPayCallbackRequest {

    @NotNull(message = "Mã đơn hàng không được để trống")
    @NotEmpty(message = "Mã đơn hàng không được để trống")
    private String invoiceNo;

}

