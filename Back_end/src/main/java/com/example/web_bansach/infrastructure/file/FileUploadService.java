package com.example.web_bansach.infrastructure.file;

import org.springframework.web.multipart.MultipartFile;

/**
 * Giao diện định nghĩa các thao tác tải tệp lên hệ thống.
 * Abstraction để có thể thay đổi storage strategy (Cloudinary, S3, etc.)
 */
public interface FileUploadService {

    /**
     * Upload file
     * 
     * @param file   tệp cần tải lên
     * @param folder thư mục lưu trữ, ví dụ "books" hoặc "users"
     * @return đường dẫn của tệp sau khi tải lên thành công
     * @throws Exception nếu quá trình tải lên thất bại
     */
    String uploadFile(MultipartFile file, String folder) throws Exception;

    /**
     * Xóa file
     * 
     * @param fileUrl - URL của file cần xóa
     * @throws Exception nếu xóa thất bại
     */
    void deleteFile(String fileUrl) throws Exception;

    /**
     * Check file có hợp lệ không
     * 
     * @param file tệp cần kiểm tra
     * @return {@code true} nếu tệp hợp lệ
     */
    boolean isValidFile(MultipartFile file);
}
