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
@Schema(description = "Chi tiết một đơn hàng trong lịch sử")
public class OrderHistoryDetailResponse {

    @Schema(description = "ID đơn hàng (cart ID)")
    private String orderId;

    @Schema(description = "Trạng thái đơn hàng", example = "COMPLETED",
            allowableValues = {"PENDING", "PROCESSING", "SHIPPED", "COMPLETED", "CANCELLED"})
    private String status;

    @Schema(description = "Nhãn trạng thái tiếng Việt", example = "Hoàn thành")
    private String statusLabel;

    @Schema(description = "Tổng tiền đơn hàng (VNĐ)", example = "3980000")
    private BigDecimal totalAmount;

    @Schema(description = "Tổng số loại sản phẩm trong đơn", example = "2")
    private int itemCount;

    @Schema(description = "Danh sách sản phẩm trong đơn hàng")
    private List<OrderHistoryItemResponse> items;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Thời điểm đặt hàng")
    private LocalDateTime createdAt;
}
