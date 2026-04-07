package com.example.pcstore.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin cập nhật hồ sơ. Trường nào null sẽ giữ nguyên giá trị cũ.")
public class UpdateProfileRequest {

    @Size(min = 1, max = 50, message = "Tên phải từ 1 đến 50 ký tự")
    @Schema(description = "Tên", example = "Văn")
    private String firstName;

    @Size(min = 1, max = 50, message = "Họ và tên đệm phải từ 1 đến 50 ký tự")
    @Schema(description = "Họ và tên đệm", example = "Nguyễn")
    private String lastName;

    @Email(message = "Địa chỉ email không đúng định dạng")
    @Schema(description = "Địa chỉ email", example = "nguyenvan01@gmail.com")
    private String email;

    @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0")
    @Schema(description = "Số điện thoại Việt Nam (10 số, bắt đầu bằng 0)", example = "0901234567")
    private String phoneNumber;

    @Size(max = 500, message = "Địa chỉ không được vượt quá 500 ký tự")
    @Schema(description = "Địa chỉ giao hàng", example = "123 Nguyễn Huệ, Q1, TP.HCM")
    private String address;
}
