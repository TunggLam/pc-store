package com.example.pcstore.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thống kê tổng quan đơn hàng")
public class OrderStatisticsResponse {

    @Schema(description = "Tổng số đơn hàng trong hệ thống", example = "150")
    private long totalOrders;

    @Schema(description = "Số đơn chờ xác nhận (PENDING)", example = "30")
    private long pendingOrders;

    @Schema(description = "Số đơn đang xử lý (PROCESSING)", example = "20")
    private long processingOrders;

    @Schema(description = "Số đơn đang giao hàng (SHIPPED)", example = "15")
    private long shippedOrders;

    @Schema(description = "Số đơn đã hoàn thành (COMPLETED)", example = "75")
    private long completedOrders;

    @Schema(description = "Số đơn đã hủy (CANCELLED)", example = "10")
    private long cancelledOrders;

    @Schema(description = "Tổng doanh thu từ các đơn đã hoàn thành (VNĐ)", example = "1199250000")
    private BigDecimal totalRevenue;
}
