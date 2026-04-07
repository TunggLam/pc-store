package com.example.pcstore.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    PENDING("PENDING", "Chờ xác nhận"),
    PROCESSING("PROCESSING", "Đang xử lý"),
    SHIPPED("SHIPPED", "Đang giao hàng"),
    COMPLETED("COMPLETED", "Hoàn thành"),
    CANCELLED("CANCELLED", "Đã hủy");

    private final String value;
    private final String label;

    public static boolean isValid(String value) {
        for (OrderStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
