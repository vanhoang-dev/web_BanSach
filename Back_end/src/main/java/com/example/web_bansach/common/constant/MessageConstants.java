package com.example.web_bansach.common.constant;

// Tập trung các thông báo dùng chung để tránh lặp chuỗi trong backend.
public class MessageConstants {

    private MessageConstants() {
    }

    public static final String RESOURCE_NOT_FOUND = "Không tìm thấy dữ liệu";
    public static final String UNAUTHORIZED = "Bạn chưa đăng nhập hoặc token không hợp lệ";
    public static final String FORBIDDEN = "Bạn không có quyền truy cập";
    public static final String INVALID_REQUEST = "Dữ liệu gửi lên không hợp lệ";
    public static final String INTERNAL_SERVER_ERROR = "Hệ thống đang gặp lỗi, vui lòng thử lại sau";
    public static final String INVALID_CREDENTIALS = "Email hoặc mật khẩu không đúng";
    public static final String USER_ALREADY_EXISTS = "Người dùng đã tồn tại";
    public static final String EMAIL_ALREADY_EXISTS = "Email đã tồn tại";
    public static final String USERNAME_ALREADY_EXISTS = "Tên đăng nhập đã tồn tại";
    public static final String INVALID_EMAIL = "Email không đúng định dạng";
    public static final String PASSWORD_TOO_SHORT = "Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt";
    public static final String OUT_OF_STOCK = "Sản phẩm đã hết hàng";
    public static final String INSUFFICIENT_STOCK = "Số lượng tồn kho không đủ";
    public static final String INVALID_QUANTITY = "Số lượng không hợp lệ";
    public static final String INVALID_PAGINATION = "Thông tin phân trang không hợp lệ";

    public static final String CREATED_SUCCESS = "Tạo mới thành công";
    public static final String UPDATED_SUCCESS = "Cập nhật thành công";
    public static final String DELETED_SUCCESS = "Xóa thành công";
    public static final String OPERATION_SUCCESS = "Thao tác thành công";
}
