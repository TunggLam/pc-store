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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Đơn hàng gần đây")
public class RecentOrderItem {

    @Schema(description = "ID đơn hàng")
    private String orderId;

    @Schema(description = "Tên khách hàng", example = "Nguyễn Lâm")
    private String customerName;

    @Schema(description = "Tên đăng nhập", example = "lam123")
    private String username;

    @Schema(description = "Tổng tiền đơn hàng (VNĐ)", example = "6233221")
    private BigDecimal totalAmount;

    @Schema(description = "Trạng thái đơn hàng", example = "COMPLETED")
    private String status;

    @Schema(description = "Nhãn trạng thái tiếng Việt", example = "Hoàn thành")
    private String statusLabel;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Thời điểm đặt hàng")
    private LocalDateTime createdAt;
}
