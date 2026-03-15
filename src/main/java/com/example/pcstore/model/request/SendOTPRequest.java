package com.example.pcstore.model.request;

import com.example.pcstore.enums.OTPTypeEnum;
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
public class SendOTPRequest {

    private String phoneNumber;
    private String username;

    @NotNull(message = "Địa chỉ email không được để trống")
    @NotEmpty(message = "Địa chỉ email không được để trống")
    private String email;

    @NotNull(message = "Loại gửi mã OTP không được để trống")
    private OTPTypeEnum type;

}

