package com.example.web_bansach.module.user.service;

import com.example.web_bansach.module.auth.dto.request.ForgotPasswordRequest;
import com.example.web_bansach.module.auth.dto.request.LoginRequest;
import com.example.web_bansach.module.auth.dto.request.ResetPasswordRequest;
import com.example.web_bansach.module.auth.dto.request.UserRequest;
import com.example.web_bansach.module.auth.dto.response.LoginResponse;

// Định nghĩa các nghiệp vụ xác thực và quản lý mật khẩu mà controller được phép gọi.
public interface AuthService {

    // Tạo tài khoản mới sau khi kiểm tra username, email và gán quyền người dùng.
    void taoTaiKhoanMoi(UserRequest request);

    // Xác thực thông tin đăng nhập và trả dữ liệu phiên đăng nhập.
    LoginResponse dangNhap(LoginRequest request);

    // Tạo token đặt lại mật khẩu và gửi liên kết khôi phục tới email.
    void quenMatKhau(ForgotPasswordRequest request);

    // Đổi mật khẩu bằng reset token còn hiệu lực.
    void datLaiMatKhau(ResetPasswordRequest request);

    // So sánh mật khẩu thuần với mật khẩu đã mã hóa trong database.
    boolean verifyPassword(String rawPassword, String encodedPassword);
}
