package com.example.pcstore.model.request;

import com.example.pcstore.enums.OTPTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu gửi mã OTP qua email")
public class SendOTPRequest {

    @Schema(description = "Số điện thoại (không bắt buộc)", example = "0901234567")
    private String phoneNumber;

    @Schema(description = "Tên đăng nhập (không bắt buộc)", example = "nguyenvan01")
    private String username;

    @Schema(description = "Địa chỉ email nhận OTP", example = "nguyenvan01@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Địa chỉ email không được để trống")
    @NotEmpty(message = "Địa chỉ email không được để trống")
    private String email;

    @Schema(description = "Loại OTP: REGISTER (đăng ký) hoặc FORGOT_PASSWORD (quên mật khẩu)", example = "REGISTER", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Loại gửi mã OTP không được để trống")
    private OTPTypeEnum type;

}

