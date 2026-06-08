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
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/dang-ky")
    public ResponseEntity<ApiResponse<?>> dangKy(@Valid @RequestBody UserRequest request) {
        service.taoTaiKhoanMoi(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo tài khoản thành công", null));
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<ApiResponse<LoginResponse>> dangNhap(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = service.dangNhap(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
    }

    @PostMapping("/quen-mat-khau")
    public ResponseEntity<ApiResponse<?>> quenMatKhau(@Valid @RequestBody ForgotPasswordRequest request) {
        service.quenMatKhau(request);
        return ResponseEntity.ok(ApiResponse.success("Đã gửi email đặt lại mật khẩu nếu tài khoản tồn tại", null));
    }

    @PostMapping("/dat-lai-mat-khau")
    public ResponseEntity<ApiResponse<?>> datLaiMatKhau(@Valid @RequestBody ResetPasswordRequest request) {
        service.datLaiMatKhau(request);
        return ResponseEntity.ok(ApiResponse.success("Đặt lại mật khẩu thành công", null));
    }
}
