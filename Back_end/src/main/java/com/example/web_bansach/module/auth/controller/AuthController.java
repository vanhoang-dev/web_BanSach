package com.example.web_bansach.module.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.module.auth.dto.request.ForgotPasswordRequest;
import com.example.web_bansach.module.auth.dto.request.LoginRequest;
import com.example.web_bansach.module.auth.dto.request.ResetPasswordRequest;
import com.example.web_bansach.module.auth.dto.request.UserRequest;
import com.example.web_bansach.module.auth.dto.response.LoginResponse;
import com.example.web_bansach.module.user.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tai-khoan")
// Cung cấp các API công khai cho đăng ký, đăng nhập và khôi phục mật khẩu.
public class AuthController {

    private final AuthService service;

    // Khởi tạo controller với service xử lý nghiệp vụ tài khoản.
    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/dang-ky")
    // Tiếp nhận thông tin đăng ký và tạo một tài khoản người dùng mới.
    public ResponseEntity<ApiResponse<?>> dangKy(@Valid @RequestBody UserRequest request) {
        service.taoTaiKhoanMoi(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo tài khoản thành công", null));
    }

    @PostMapping("/dang-nhap")
    // Xác thực email, mật khẩu và trả JWT cùng thông tin quyền cho frontend.
    public ResponseEntity<ApiResponse<LoginResponse>> dangNhap(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = service.dangNhap(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
    }

    @PostMapping("/quen-mat-khau")
    // Tạo yêu cầu khôi phục mật khẩu và gửi liên kết đặt lại qua email.
    public ResponseEntity<ApiResponse<?>> quenMatKhau(@Valid @RequestBody ForgotPasswordRequest request) {
        service.quenMatKhau(request);
        return ResponseEntity.ok(ApiResponse.success("Đã gửi email đặt lại mật khẩu nếu tài khoản tồn tại", null));
    }

    @PostMapping("/dat-lai-mat-khau")
    // Kiểm tra reset token và cập nhật mật khẩu mới cho tài khoản.
    public ResponseEntity<ApiResponse<?>> datLaiMatKhau(@Valid @RequestBody ResetPasswordRequest request) {
        service.datLaiMatKhau(request);
        return ResponseEntity.ok(ApiResponse.success("Đặt lại mật khẩu thành công", null));
    }
}
