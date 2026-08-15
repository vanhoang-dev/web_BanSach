package com.example.web_bansach.infrastructure.file.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.infrastructure.cloudinary.CloudinaryService;
import com.example.web_bansach.infrastructure.file.FileUploadService;

/**
 * Xử lý việc tải tệp lên dịch vụ lưu trữ Cloudinary.
 */
@Service
public class CloudinaryFileUploadService implements FileUploadService {

    private final CloudinaryService cloudinaryService;

    // Cấu hình file hợp lệ
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_TYPES = { "image/jpeg", "image/png", "image/webp" };

    public CloudinaryFileUploadService(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) throws Exception {
        if (!isValidFile(file)) {
            throw new BusinessException("File không hợp lệ hoặc quá lớn");
        }

        return cloudinaryService.uploadImage(file, folder);
    }

    @Override
    public void deleteFile(String fileUrl) throws Exception {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            throw new BusinessException("URL file không hợp lệ");
        }

        cloudinaryService.deleteImage(fileUrl);
    }

    @Override
    public boolean isValidFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        // Check kích thước file
        if (file.getSize() > MAX_FILE_SIZE) {
            return false;
        }

        // Kiểm tra loại nội dung của tệp.
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        for (String allowedType : ALLOWED_TYPES) {
            if (contentType.equals(allowedType)) {
                return true;
            }
        }

        return false;
    }
}
