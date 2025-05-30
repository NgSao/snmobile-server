package com.snd.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyRequest {

    @NotBlank(message = "Email không được bỏ trống")
    @Email(message = "Email không hợp lệ")
    @Schema(description = "Địa chỉ email của người dùng", example = "nguyensaobe.java@gmail.com")
    private String email;

    @NotBlank(message = "Mã xác thực không được bỏ trống")
    @Schema(description = "Mã OTP xác thực tài khoản", example = "123456")
    private String otp;
}
