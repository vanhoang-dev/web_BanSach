package com.example.web_bansach.module.user.service;

import com.example.web_bansach.module.user.dto.request.UpdateUserProfileRequest;
import com.example.web_bansach.module.user.dto.response.UserResponse;

/**
 * Dịch vụ xử lý hồ sơ người dùng, được tách riêng từ dịch vụ người dùng.
 * Quản lý việc đọc và cập nhật hồ sơ cá nhân của chính người dùng.
 */
public interface UserProfileService {

    /**
     * Lấy người dùng theo mã định danh.
     * 
     * @param id - User ID
     * @return thông tin phản hồi của người dùng
     * @throws ResourceNotFoundException nếu không tìm thấy
     */
    UserResponse layNguoiDungTheoId(Long id);

    /**
     * Cập nhật hồ sơ cho người dùng hiện tại.
     * 
     * @param id mã định danh của người dùng hiện tại
     * @param request dữ liệu cập nhật hồ sơ
     * @throws ResourceNotFoundException nếu người dùng không tồn tại
     */
    void capNhatThongTinCaNhan(Long id, UpdateUserProfileRequest request);

    /**
     * Lấy hồ sơ của người dùng hiện tại.
     * 
     * @param userId - User ID hiện tại
     * @return thông tin phản hồi của người dùng
     */
    UserResponse layThongTinCaNhan(Long userId);
}
