package com.example.pcstore.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User mới đăng ký")
public class RecentUserItem {

    @Schema(description = "Tên đăng nhập", example = "lam123")
    private String username;

    @Schema(description = "Họ và tên đầy đủ", example = "Nguyễn Lâm")
    private String fullName;

    @Schema(description = "Địa chỉ email", example = "nguyentunglam230203@gmail.com")
    private String email;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "Thời điểm đăng ký")
    private LocalDateTime createdAt;
}
