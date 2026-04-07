package com.example.pcstore.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Kết quả đăng nhập — chứa JWT token và thông tin phiên")
public class LoginResponse {

    @Schema(description = "JWT access token dùng để gọi các API yêu cầu xác thực")
    @JsonProperty("accessToken")
    private String accessToken;

    @Schema(description = "Thời gian hết hạn của access token (giây)", example = "300")
    @JsonProperty("expiresIn")
    private long expiresIn;

    @Schema(description = "Thời gian hết hạn của refresh token (giây)", example = "1800")
    @JsonProperty("refreshExpiresIn")
    private long refreshExpiresIn;

    @Schema(description = "Refresh token dùng để lấy access token mới")
    @JsonProperty("refreshToken")
    private String refreshToken;

    @Schema(description = "Loại token", example = "Bearer")
    @JsonProperty("tokenType")
    private String tokenType;

    @JsonProperty("idToken")
    private String idToken;

    @JsonProperty("notBeforePolicy")
    private int notBeforePolicy;

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("scope")
    private String scope;

    @Schema(description = "Mã lỗi (nếu đăng nhập thất bại)")
    @JsonProperty("error")
    private String error;

    @Schema(description = "Mô tả lỗi")
    @JsonProperty("errorDescription")
    private String errorDescription;

    @JsonProperty("errorUri")
    private String errorUri;

    @Schema(description = "Danh sách vai trò của người dùng", example = "[\"ROLE_USER\"]")
    @JsonProperty("roles")
    private List<String> roles;
}
