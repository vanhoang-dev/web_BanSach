package com.example.web_bansach.module.auth.dto.response;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// Trả JWT và thông tin định danh/quyền cho frontend sau khi đăng nhập thành công.
public class LoginResponse {
    private String jwt;
    private Long userId;
    private String username;
    private Set<String> roles;
}
