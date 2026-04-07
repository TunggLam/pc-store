package com.example.pcstore.controller;

import com.example.pcstore.aop.Secured;
import com.example.pcstore.enums.RoleEnum;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.model.request.*;
import com.example.pcstore.model.response.LoginResponse;
import com.example.pcstore.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "Đăng ký, đăng nhập, quản lý phiên và mật khẩu")
public class AuthenticationController {

    private static final Logger LOGGER = LoggingFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Đăng ký tài khoản",
            description = "Tạo tài khoản mới. Yêu cầu đã gửi và xác thực OTP trước đó qua `/api/otp/send`."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Đăng ký thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc tài khoản đã tồn tại", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        LOGGER.info("[AUTHENTICATION][{}][REGISTER][STARTING...] Request: {}", request.getUsername(), request);
        authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Đăng nhập",
            description = "Xác thực tài khoản và trả về JWT access token + refresh token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Sai tài khoản hoặc mật khẩu", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LOGGER.info("[AUTHENTICATION][{}][LOGIN][STARTING...] Request: {}", request.getUsername(), request);
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @Operation(
            summary = "Đăng xuất",
            description = "Hủy token hiện tại khỏi Redis, kết thúc phiên đăng nhập."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đăng xuất thành công"),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        LOGGER.info("[AUTHENTICATION][{}][LOGOUT][STARTING...] Request: {}", request.getUsername(), request);
        authenticationService.logout(request.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Làm mới token",
            description = "Dùng refresh token để lấy access token mới mà không cần đăng nhập lại."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Làm mới token thành công",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Refresh token không hợp lệ hoặc đã hết hạn", content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshTokenRequest request) {
        LOGGER.info("[AUTHENTICATION][{}][REFRESH][STARTING...] Request: {}", request.getUsername(), request);
        return ResponseEntity.ok(authenticationService.refresh(request));
    }

    @Operation(
            summary = "Đổi mật khẩu",
            description = "Đổi mật khẩu cho tài khoản đang đăng nhập. Yêu cầu Bearer token.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đổi mật khẩu thành công"),
            @ApiResponse(responseCode = "400", description = "Mật khẩu cũ không đúng", content = @Content),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực hoặc token hết hạn", content = @Content)
    })
    @Secured(role = RoleEnum.ALL)
    @PostMapping("/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authenticationService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Đặt lại mật khẩu (quên mật khẩu)",
            description = "Đặt lại mật khẩu mới sau khi đã xác thực OTP qua `/api/otp/forgot-password/verify`."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đặt lại mật khẩu thành công"),
            @ApiResponse(responseCode = "400", description = "OTP không hợp lệ hoặc đã hết hạn", content = @Content)
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> changeForgotPassword(@Valid @RequestBody VerifyForgotPasswordRequest request) {
        authenticationService.verifyForgotPassword(request);
        return ResponseEntity.ok().build();
    }

}
