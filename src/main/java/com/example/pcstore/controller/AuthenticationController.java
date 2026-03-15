package com.example.pcstore.controller;

import com.example.pcstore.aop.Secured;
import com.example.pcstore.enums.RoleEnum;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.model.request.*;
import com.example.pcstore.model.response.LoginResponse;
import com.example.pcstore.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authentication")
public class AuthenticationController {

    private static final Logger LOGGER = LoggingFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        LOGGER.info("[AUTHENTICATION][{}][REGISTER][STARTING...] Request: {}", request.getUsername(), request);
        authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LOGGER.info("[AUTHENTICATION][{}][LOGIN][STARTING...] Request: {}", request.getUsername(), request);
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        LOGGER.info("[AUTHENTICATION][{}][LOGOUT][STARTING...] Request: {}", request.getUsername(), request);
        authenticationService.logout(request.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshTokenRequest request) {
        LOGGER.info("[AUTHENTICATION][{}][REFRESH][STARTING...] Request: {}", request.getUsername(), request);
        return ResponseEntity.ok(authenticationService.refresh(request));
    }

    @Secured(role = RoleEnum.ALL)
    @PostMapping("/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authenticationService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> changeForgotPassword(@Valid @RequestBody VerifyForgotPasswordRequest request) {
        authenticationService.verifyForgotPassword(request);
        return ResponseEntity.ok().build();
    }

}
