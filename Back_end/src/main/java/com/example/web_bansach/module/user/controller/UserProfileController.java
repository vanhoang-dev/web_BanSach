package com.example.web_bansach.module.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.module.user.dto.request.ChangePasswordRequest;
import com.example.web_bansach.module.user.dto.request.UpdateUserRequest;
import com.example.web_bansach.module.user.dto.response.UserResponse;
import com.example.web_bansach.module.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserProfileController {

    @Autowired
    private UserService service;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(ApiResponse.success(service.getCurrentUserProfile(username)));
    }

    // Cập nhật thông tin tài khoản của người dùng hiện tại
    @PutMapping("/update-profile")
    public ResponseEntity<ApiResponse<?>> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        service.updateCurrentUserProfile(username, updateUserRequest);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin thành công", null));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        service.changeCurrentUserPassword(username, request);
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
    }
}
