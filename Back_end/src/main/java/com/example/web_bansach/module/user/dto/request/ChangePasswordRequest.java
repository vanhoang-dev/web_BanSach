package com.example.web_bansach.module.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
// Chứa mật khẩu hiện tại, mật khẩu mới và xác nhận khi đổi mật khẩu.
public class ChangePasswordRequest {

    @NotBlank(message = "Mật khẩu hiện tại không được để trống")
    private String currentPassword;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 8, max = 50, message = "Mật khẩu mới phải từ 8 đến 50 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).*$", message = "Mật khẩu mới phải có chữ hoa, chữ thường, số và ký tự đặc biệt")
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;
}
