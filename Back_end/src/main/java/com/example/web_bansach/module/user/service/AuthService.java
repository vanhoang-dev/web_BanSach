package com.example.web_bansach.module.user.service;

import com.example.web_bansach.module.auth.dto.request.ForgotPasswordRequest;
import com.example.web_bansach.module.auth.dto.request.LoginRequest;
import com.example.web_bansach.module.auth.dto.request.ResetPasswordRequest;
import com.example.web_bansach.module.auth.dto.request.UserRequest;
import com.example.web_bansach.module.auth.dto.response.LoginResponse;

public interface AuthService {

    void taoTaiKhoanMoi(UserRequest request);

    LoginResponse dangNhap(LoginRequest request);

    void quenMatKhau(ForgotPasswordRequest request);

    void datLaiMatKhau(ResetPasswordRequest request);

    boolean verifyPassword(String rawPassword, String encodedPassword);
}
