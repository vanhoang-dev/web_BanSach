package com.example.web_bansach.module.user.service;

import java.util.List;

import com.example.web_bansach.module.user.dto.request.AdminUpdateUserRequest;
import com.example.web_bansach.module.user.dto.response.UserResponse;

/**
 * Service xử lý admin user management - tách riêng từ UserService
 * Quản lý: User CRUD operations dành cho admin
 */
public interface UserAdminService {

    /**
     * Lấy tất cả users (admin view)
     * 
     * @param page - Trang
     * @param size - Kích cỡ
     * @return List<UserResponse>
     */
    List<UserResponse> layDanhSachNguoiDung(int page, int size);

    /**
     * Cập nhật thông tin user (admin - có thể thay đổi bất kỳ field nào)
     * 
     * @param id      - User ID
     * @param request - AdminUpdateUserRequest
     * @throws ResourceNotFoundException nếu user không tồn tại
     */
    void capNhatNguoiDungAdmin(Long id, AdminUpdateUserRequest request);

    /**
     * Xoá user (soft delete)
     * 
     * @param id - User ID
     * @throws ResourceNotFoundException nếu user không tồn tại
     */
    void xoaNguoiDungTheoId(Long id);

    /**
     * Bật/tắt user
     * 
     * @param id       - User ID
     * @param isActive - Active flag
     */
    void capNhatTrangThaiUser(Long id, Boolean isActive);
}
