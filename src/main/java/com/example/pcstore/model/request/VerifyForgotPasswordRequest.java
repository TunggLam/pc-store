package com.example.pcstore.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyForgotPasswordRequest {

    @NotNull(message = "Mã xác thực không được để trống")
    @NotEmpty(message = "Mã xác thực không được để trống")
    private String otp;

    @NotNull(message = "Địa chỉ email không được để trống")
    @NotEmpty(message = "Địa chỉ email không được để trống")
    private String email;

    @NotNull(message = "Mật khẩu không được để trống")
    @NotEmpty(message = "Mật khẩu không được để trống")
    private String password;

}

