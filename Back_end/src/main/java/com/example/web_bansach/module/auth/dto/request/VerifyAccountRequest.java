package com.example.web_bansach.module.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyAccountRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Mã xác nhận không được để trống")
    @Pattern(regexp = "^[0-9]{6}$", message = "Mã xác nhận phải gồm 6 chữ số")
    private String code;
}
