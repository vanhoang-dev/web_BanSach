package com.example.web_bansach.module.user.service;

import com.example.web_bansach.module.user.dto.request.UpdateUserProfileRequest;
import com.example.web_bansach.module.user.dto.response.UserResponse;

/**
 * Service xử lý user profile - tách riêng từ UserService
 * Quản lý: User profile CRUD (read, update) cho user của họ
 */
public interface UserProfileService {

    /**
     * Lấy user by ID
     * 
     * @param id - User ID
     * @return UserResponse
     * @throws ResourceNotFoundException nếu không tìm thấy
     */
    UserResponse layNguoiDungTheoId(Long id);

    /**
     * Cập nhật profile cho user hiện tại (self-service)
     * 
     * @param id      - User ID (current user)
     * @param request - UpdateUserProfileRequest
     * @throws ResourceNotFoundException nếu user không tồn tại
     */
    void capNhatThongTinCaNhan(Long id, UpdateUserProfileRequest request);

    /**
     * Lấy profile của user hiện tại
     * 
     * @param userId - User ID hiện tại
     * @return UserResponse
     */
    UserResponse layThongTinCaNhan(Long userId);
}
