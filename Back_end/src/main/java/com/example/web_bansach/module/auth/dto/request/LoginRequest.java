package com.example.web_bansach.module.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Không được để trống email")
    private String email;

    @NotBlank(message = "Không được để trống mật khẩu")
    private String password;
}
