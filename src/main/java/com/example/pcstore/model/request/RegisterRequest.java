package com.example.pcstore.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin đăng ký tài khoản mới")
public class RegisterRequest {

    @Schema(description = "Tên", example = "Văn", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @Schema(description = "Họ và tên đệm", example = "Nguyễn", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @Schema(description = "Địa chỉ email", example = "nguyenvan01@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Địa chỉ giao hàng", example = "123 Nguyễn Huệ, Q1, TP.HCM")
    private String address;

    @Schema(description = "Số điện thoại", example = "0901234567", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;

    @Schema(description = "Tên đăng nhập (không dấu, không khoảng trắng)", example = "nguyenvan01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Mật khẩu (tối thiểu 6 ký tự)", example = "Password@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "Mã OTP 6 chữ số đã được gửi tới email", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String otp;
}
