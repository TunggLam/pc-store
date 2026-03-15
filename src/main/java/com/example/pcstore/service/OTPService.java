package com.example.pcstore.service;

import com.example.pcstore.model.request.SendOTPRequest;
import com.example.pcstore.model.request.VerifyOTPForgotPasswordRequest;

public interface OTPService {
    void sendOTP(SendOTPRequest request);

    void verifyOTPForgotPassword(VerifyOTPForgotPasswordRequest request);
}
