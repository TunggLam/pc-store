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
@Schema(description = "Yêu cầu cập nhật số lượng sản phẩm trong giỏ hàng")
public class UpdateCartRequest {

    @Schema(description = "Loại cập nhật: `ADD` (tăng 1) hoặc `DECREASE` (giảm 1)", example = "ADD",
            allowableValues = {"ADD", "DECREASE"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Loại cập nhật không được để trống")
    @NotEmpty(message = "Loại cập nhật không được để trống")
    private String type;

    @Schema(description = "ID sản phẩm cần cập nhật", example = "abc123-uuid", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID sản phẩm không được để trống")
    @NotEmpty(message = "ID sản phẩm không được để trống")
    private String productId;
}

