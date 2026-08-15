package com.example.web_bansach.infrastructure.cloudinary;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.web_bansach.common.exception.BusinessException;

@Service
// Upload, xóa và trích publicId ảnh trên Cloudinary.
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String folder) throws Exception {

        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder // thư mục động
                ));

        return uploadResult.get("secure_url").toString(); // HTTPS URL
    }

    public void deleteImage(String fileUrl) throws Exception {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            throw new BusinessException("URL file không hợp lệ");
        }

        String publicId = extractPublicId(fileUrl.trim());
        if (publicId == null || publicId.isEmpty()) {
            throw new BusinessException("Không thể xác định file cần xóa");
        }

        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    private String extractPublicId(String fileUrl) {
        int uploadIndex = fileUrl.indexOf("/upload/");
        if (uploadIndex == -1) {
            return null;
        }

        String path = fileUrl.substring(uploadIndex + "/upload/".length());
        int versionSeparatorIndex = path.indexOf('/');
        if (versionSeparatorIndex == -1) {
            return null;
        }

        String withoutVersion = path.substring(versionSeparatorIndex + 1);
        int extensionIndex = withoutVersion.lastIndexOf('.');
        if (extensionIndex > 0) {
            return withoutVersion.substring(0, extensionIndex);
        }

        return withoutVersion;
    }
}
