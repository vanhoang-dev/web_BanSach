package com.example.web_bansach.common.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for file operations
 */
public class FileUtils {

    private FileUtils() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif",
            "image/webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public static boolean isImageValid(String contentType, long fileSize) {
        return isValidContentType(contentType) && fileSize <= MAX_FILE_SIZE && fileSize > 0;
    }

    public static boolean isValidContentType(String contentType) {
        return contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
    }

    public static boolean isValidFileSize(long fileSize) {
        return fileSize > 0 && fileSize <= MAX_FILE_SIZE;
    }

    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1).toLowerCase() : "";
    }

    public static void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        } catch (Exception e) {
            // Log but don't throw
        }
    }
}
