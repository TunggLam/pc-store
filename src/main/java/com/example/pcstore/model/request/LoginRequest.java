package com.example.pcstore.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin đăng nhập")
public class LoginRequest {

    @Schema(description = "Tên đăng nhập", example = "nguyenvan01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Mật khẩu", example = "Password@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
