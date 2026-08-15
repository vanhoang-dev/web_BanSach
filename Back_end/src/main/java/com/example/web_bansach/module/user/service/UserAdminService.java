package com.example.web_bansach.module.user.service;

import java.util.List;

import com.example.web_bansach.module.user.dto.request.AdminUpdateUserRequest;
import com.example.web_bansach.module.user.dto.response.UserResponse;

/**
 * Dịch vụ quản trị tài khoản người dùng, được tách riêng từ dịch vụ người dùng.
 * Quản lý việc tạo, đọc, cập nhật và xóa người dùng dành cho quản trị viên.
 */
public interface UserAdminService {

    /**
     * Lấy toàn bộ người dùng cho màn hình quản trị.
     * 
     * @param page - Trang
     * @param size - Kích cỡ
     * @return danh sách thông tin người dùng
     */
    List<UserResponse> layDanhSachNguoiDung(int page, int size);

    /**
     * Cập nhật thông tin người dùng; quản trị viên có thể thay đổi mọi trường được cho phép.
     * 
     * @param id      - User ID
     * @param request dữ liệu cập nhật từ quản trị viên
     * @throws ResourceNotFoundException nếu người dùng không tồn tại
     */
    void capNhatNguoiDungAdmin(Long id, AdminUpdateUserRequest request);

    /**
     * Xóa mềm tài khoản người dùng.
     * 
     * @param id - User ID
     * @throws ResourceNotFoundException nếu người dùng không tồn tại
     */
    void xoaNguoiDungTheoId(Long id);

    /**
     * Bật hoặc tắt trạng thái hoạt động của người dùng.
     * 
     * @param id       - User ID
     * @param isActive - Active flag
     */
    void capNhatTrangThaiUser(Long id, Boolean isActive);
}
