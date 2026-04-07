package com.example.pcstore.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin hồ sơ người dùng")
public class UserProfileResponse {

    @Schema(description = "Keycloak User ID")
    private String keycloakId;

    @Schema(description = "Tên", example = "Văn")
    private String firstName;

    @Schema(description = "Họ và tên đệm", example = "Nguyễn")
    private String lastName;

    @Schema(description = "Địa chỉ email", example = "nguyenvan01@gmail.com")
    private String email;

    @Schema(description = "Số điện thoại", example = "0901234567")
    private String phoneNumber;

    @Schema(description = "Địa chỉ giao hàng", example = "123 Nguyễn Huệ, Q1, TP.HCM")
    private String address;

    @Schema(description = "Tên đăng nhập", example = "nguyenvan01")
    private String username;

    @Schema(description = "Tài khoản đang hoạt động hay bị khóa", example = "true")
    private Boolean isActive;

    @Schema(description = "Thời điểm tạo tài khoản")
    private LocalDateTime createdAt;

}

