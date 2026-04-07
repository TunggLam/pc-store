package com.example.pcstore.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu thêm sản phẩm vào giỏ hàng")
public class AddCartRequest {

    @Schema(description = "ID sản phẩm", example = "abc123-uuid", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID sản phẩm không được để trống")
    @NotEmpty(message = "ID sản phẩm không được để trống")
    private String productId;

    @Schema(description = "Số lượng thêm vào giỏ (mặc định = 1)", example = "2", defaultValue = "1")
    private Integer quantity = 1;
}

