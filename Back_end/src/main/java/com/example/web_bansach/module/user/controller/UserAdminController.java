package com.example.web_bansach.module.user.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.common.response.PageResponse;
import com.example.web_bansach.module.user.dto.request.AdminUpdateUserRequest;
import com.example.web_bansach.module.user.dto.response.UserResponse;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/user_for_admin/")
// Cung cấp API quản trị danh sách, hồ sơ và trạng thái tài khoản người dùng.
public class UserAdminController {
    @Autowired
    private UserService userService;

    // Lấy người dùng theo ID người dùng
    @GetMapping("user/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    // Trả thông tin người dùng theo ID cho admin.
    public ResponseEntity<ApiResponse<Users>> getUserId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.layNguoiDungTheoId(id)));
    }

    // Lấy tất cả người dùng từ dữ liệu
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    // Trả danh sách người dùng có phân trang.
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUser(
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) Integer size) {

        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(userService.getAllUsersPagination(page, size))));
    }

    // Xóa người dùng theo ID
    @DeleteMapping("/user/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    // Xóa mềm tài khoản để bảo toàn dữ liệu liên quan.
    public ResponseEntity<ApiResponse<?>> deleteUserId(@PathVariable Long id) {
        userService.xoaNguoiDungTheoId(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa thành công người dùng", null));
    }

    // Chỉnh sửa thông tin hoặc hoạt động của người dùng
    @PutMapping("/user/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    // Cho phép admin cập nhật thông tin, quyền và trạng thái tài khoản.
    public ResponseEntity<ApiResponse<?>> updateUserForAdmin(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateUserRequest request) {
        userService.capNhatNguoiDungAdmin(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật người dùng thành công", null));
    }

}
