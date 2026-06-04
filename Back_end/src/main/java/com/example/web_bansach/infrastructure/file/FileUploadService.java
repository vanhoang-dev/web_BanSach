package com.example.web_bansach.infrastructure.file;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interface xử lý upload file
 * Abstraction để có thể thay đổi storage strategy (Cloudinary, S3, etc.)
 */
public interface FileUploadService {

    /**
     * Upload file
     * 
     * @param file   - MultipartFile cần upload
     * @param folder - folder lưu trữ (vd: "books", "users")
     * @return URL của file đã upload
     * @throws Exception nếu upload thất bại
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
     * @param file - MultipartFile cần check
     * @return true nếu hợp lệ
     */
    boolean isValidFile(MultipartFile file);
}
