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
@Schema(description = "Yêu cầu đổi mật khẩu")
public class ChangePasswordRequest {

    @Schema(description = "Mật khẩu hiện tại", example = "OldPass@123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Mật khẩu cũ không được để trống")
    @NotEmpty(message = "Mật khẩu cũ không được để trống")
    private String oldPassword;

    @Schema(description = "Mật khẩu mới", example = "NewPass@456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Mật khẩu mới không được để trống")
    @NotEmpty(message = "Mật khẩu mới không được để trống")
    private String newPassword;

}

