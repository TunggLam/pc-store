package com.example.pcstore.controller;

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
@Tag(name = "OTP Controller", description = "Danh sách API phục vụ One Time Password")
public class OTPController {

    private final OTPService otpService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendOTP(@Valid @RequestBody SendOTPRequest request) {
        otpService.sendOTP(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password/verify")
    public ResponseEntity<Void> verifyForgotPassword(@Valid @RequestBody VerifyOTPForgotPasswordRequest request) {
        otpService.verifyOTPForgotPassword(request);
        return ResponseEntity.ok().build();
    }
}

