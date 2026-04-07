package com.example.pcstore.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Chi tiết đơn hàng")
public class AdminOrderResponse {

    @Schema(description = "ID đơn hàng (cart ID)")
    private String orderId;

    @Schema(description = "Tên đăng nhập khách hàng", example = "nguyenvan01")
    private String username;

    @Schema(description = "Họ tên khách hàng", example = "Nguyễn Văn")
    private String customerName;

    @Schema(description = "Email khách hàng", example = "nguyenvan01@gmail.com")
    private String customerEmail;

    @Schema(description = "Số điện thoại khách hàng", example = "0901234567")
    private String customerPhone;

    @Schema(description = "Địa chỉ giao hàng")
    private String shippingAddress;

    @Schema(description = "Trạng thái đơn hàng", example = "PROCESSING",
            allowableValues = {"PENDING", "PROCESSING", "SHIPPED", "COMPLETED", "CANCELLED"})
    private String status;

    @Schema(description = "Nhãn trạng thái tiếng Việt", example = "Đang xử lý")
    private String statusLabel;

    @Schema(description = "Danh sách sản phẩm trong đơn hàng")
    private List<AdminOrderItemResponse> items;

    @Schema(description = "Tổng tiền đơn hàng (VNĐ)", example = "31980000")
    private BigDecimal totalAmount;

    @Schema(description = "Thông tin thanh toán VNPay (chỉ có khi xem chi tiết)")
    private AdminOrderPaymentResponse payment;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Thời điểm tạo đơn hàng")
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Thời điểm cập nhật cuối")
    private LocalDateTime updatedAt;
}
