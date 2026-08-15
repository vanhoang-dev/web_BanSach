package com.example.web_bansach.module.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// Chứa dữ liệu admin được phép thay đổi trên một tài khoản.
public class AdminUpdateUserRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 150, message = "Họ tên tối đa 150 ký tự")
    private String fullName;

    @Email(message = "Email không đúng định dạng")
    private String email;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phone;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String address;

    private Boolean isActive;
}
