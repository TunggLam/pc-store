package com.example.pcstore.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Yêu cầu làm mới token")
public class RefreshTokenRequest {

    @Schema(description = "Refresh token nhận được từ lần đăng nhập trước", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;

    @Schema(description = "Tên đăng nhập", example = "nguyenvan01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
}
