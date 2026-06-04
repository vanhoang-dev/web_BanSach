package com.example.web_bansach.module.user.service;

import com.example.web_bansach.module.auth.dto.request.LoginRequest;
import com.example.web_bansach.module.auth.dto.request.ForgotPasswordRequest;
import com.example.web_bansach.module.auth.dto.request.RefreshTokenRequest;
import com.example.web_bansach.module.auth.dto.request.ResetPasswordRequest;
import com.example.web_bansach.module.auth.dto.request.UserRequest;
import com.example.web_bansach.module.auth.dto.response.LoginResponse;

/**
 * Service xử lý authentication - tách riêng từ UserService
 * Quản lý: Registration, Login, Token Management
 */
public interface AuthService {

    /**
     * Đăng ký tài khoản mới
     * 
     * @param request - UserRequest chứa username, password, email, fullName
     * @throws BusinessException nếu user đã tồn tại hoặc validation thất bại
     */
    void taoTaiKhoanMoi(UserRequest request);

    /**
     * Đăng nhập
     * 
     * @param request - LoginRequest chứa username/email và password
     * @return LoginResponse chứa jwt, userId, username
     * @throws BadCredentialsException nếu credentials sai
     */
    LoginResponse dangNhap(LoginRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

    void dangXuat(RefreshTokenRequest request);

    void quenMatKhau(ForgotPasswordRequest request);

    void datLaiMatKhau(ResetPasswordRequest request);

    /**
     * Verify password cho user
     * 
     * @param rawPassword     - Mật khẩu plaintext
     * @param encodedPassword - Mật khẩu đã encode
     * @return true nếu khớp
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);
}
