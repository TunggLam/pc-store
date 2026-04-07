package com.example.pcstore.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Yêu cầu cập nhật trạng thái đơn hàng")
public class UpdateOrderStatusRequest {

    @Schema(
            description = "Trạng thái mới của đơn hàng",
            example = "PROCESSING",
            allowableValues = {"PENDING", "PROCESSING", "SHIPPED", "COMPLETED", "CANCELLED"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Trạng thái đơn hàng không được để trống")
    private String status;
}
