package com.example.web_bansach.module.user.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Data;

@Data
// Trả hồ sơ, trạng thái và quyền tài khoản cho frontend.
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Set<String> roles;
}
