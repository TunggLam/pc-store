package com.example.pcstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.pcstore.model.request.SendOTPRequest;
import com.example.pcstore.model.request.VerifyOTPForgotPasswordRequest;
import com.example.pcstore.service.OTPService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/otp")
@Tag(name = "OTP", description = "Gửi và xác thực mã OTP qua email")
public class OTPController {

    private final OTPService otpService;

    @Operation(
            summary = "Gửi mã OTP",
            description = """
                    Gửi mã OTP 6 chữ số tới email. Hỗ trợ 2 loại:
                    - `REGISTER` — xác thực khi đăng ký tài khoản mới
                    - `FORGOT_PASSWORD` — xác thực khi quên mật khẩu

                    Giới hạn: tối đa **3 lần/ngày** mỗi email.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gửi OTP thành công"),
            @ApiResponse(responseCode = "400", description = "Email không tồn tại hoặc đã vượt quá giới hạn gửi", content = @Content)
    })
    @PostMapping("/send")
    public ResponseEntity<Void> sendOTP(@Valid @RequestBody SendOTPRequest request) {
        otpService.sendOTP(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Xác thực OTP quên mật khẩu",
            description = "Xác thực mã OTP đã gửi qua email để cho phép đặt lại mật khẩu. Sau khi xác thực thành công, gọi `/api/authentication/forgot-password` để đặt mật khẩu mới."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xác thực OTP thành công"),
            @ApiResponse(responseCode = "400", description = "OTP sai, hết hạn hoặc đã bị khóa", content = @Content)
    })
    @PostMapping("/forgot-password/verify")
    public ResponseEntity<Void> verifyForgotPassword(@Valid @RequestBody VerifyOTPForgotPasswordRequest request) {
        otpService.verifyOTPForgotPassword(request);
        return ResponseEntity.ok().build();
    }
}
