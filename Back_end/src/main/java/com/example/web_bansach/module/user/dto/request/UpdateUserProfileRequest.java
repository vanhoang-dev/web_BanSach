package com.example.web_bansach.module.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Dữ liệu yêu cầu cập nhật hồ sơ cá nhân.
 */
@Data
public class UpdateUserProfileRequest {

    @Size(max = 150, message = "Họ tên tối đa 150 ký tự")
    private String fullName;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phone;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String address;
}
