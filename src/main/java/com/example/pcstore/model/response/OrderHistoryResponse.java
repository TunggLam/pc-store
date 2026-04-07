package com.example.pcstore.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Lịch sử đơn hàng của người dùng")
public class OrderHistoryResponse {

    @Schema(description = "Số trang hiện tại", example = "0")
    private int page;

    @Schema(description = "Số đơn hàng mỗi trang", example = "10")
    private int size;

    @Schema(description = "Tổng số đơn hàng", example = "25")
    private int total;

    @Schema(description = "Danh sách đơn hàng")
    private List<OrderHistoryDetailResponse> orders;
}
