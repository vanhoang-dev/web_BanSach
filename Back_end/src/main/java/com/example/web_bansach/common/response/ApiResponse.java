package com.example.web_bansach.common.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cấu trúc phản hồi API tiêu chuẩn được các controller sử dụng.
 *
 * Mục đích là giữ phản hồi của mọi endpoint nhất quán và dễ đọc:
 * - statusCode: mã trạng thái HTTP.
 * - message: thông báo ngắn gọn cho client.
 * - data: dữ liệu trả về nếu có.
 * - timestamp: thời điểm phản hồi được tạo.
 * - path: đường dẫn request, thường do bộ xử lý lỗi điền vào.
 * - errors: chi tiết lỗi validation hoặc lỗi nghiệp vụ nếu có.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private int statusCode;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String path;
    private Object errors;

    // Tạo response có status code, message, data và tự gán thời gian hiện tại.
    public ApiResponse(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // Hàm tạo response chung, dùng lại cho cả trường hợp thành công và thất bại.
    public static <T> ApiResponse<T> of(int statusCode, String message, T data) {
        return new ApiResponse<>(statusCode, message, data);
    }

    // Dùng khi API thành công với message mặc định và có dữ liệu trả về.
    public static <T> ApiResponse<T> success(T data) {
        return of(200, "Thành công", data);
    }

    // Dùng khi API thành công nhưng muốn tự đặt message riêng.
    public static <T> ApiResponse<T> success(String message, T data) {
        return of(200, message, data);
    }

    // Dùng khi tạo mới dữ liệu thành công, thường ứng với HTTP 201.
    public static <T> ApiResponse<T> created(T data) {
        return of(201, "Tạo mới thành công", data);
    }

    // Cách gọi khác của failure, giúp nơi gọi đọc là đang trả về lỗi.
    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return failure(statusCode, message);
    }

    // Trả về lỗi kèm chi tiết, ví dụ danh sách field validation bị sai.
    public static <T> ApiResponse<T> error(int statusCode, String message, Object errors) {
        return failure(statusCode, message, errors);
    }

    // Dùng khi API thất bại và không cần trả thêm chi tiết lỗi.
    public static <T> ApiResponse<T> failure(int statusCode, String message) {
        return of(statusCode, message, null);
    }

    // Dùng khi API thất bại và cần trả thêm chi tiết lỗi trong field errors.
    public static <T> ApiResponse<T> failure(int statusCode, String message, Object errors) {
        ApiResponse<T> response = new ApiResponse<>(statusCode, message, null);
        response.setErrors(errors);
        return response;
    }
}
