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
@Schema(description = "Doanh thu theo tháng")
public class MonthlyRevenueItem {

    @Schema(description = "Tháng theo định dạng YYYY-MM", example = "2026-04")
    private String month;

    @Schema(description = "Doanh thu trong tháng (VNĐ)", example = "31000000")
    private BigDecimal revenue;

    @Schema(description = "Số đơn hàng hoàn thành trong tháng", example = "25")
    private long orderCount;
}
