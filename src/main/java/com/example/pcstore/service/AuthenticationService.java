package com.example.pcstore.service;

import com.example.pcstore.model.request.*;
import com.example.pcstore.model.response.LoginResponse;

public interface AuthenticationService {
    void register(RegisterRequest registerRequest);

    LoginResponse login(LoginRequest loginRequest);

    void logout(String username);

    LoginResponse refresh(RefreshTokenRequest refreshTokenRequest);

    void changePassword(ChangePasswordRequest changePasswordRequest);

    void verifyForgotPassword(VerifyForgotPasswordRequest request);

}
