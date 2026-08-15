package com.example.web_bansach.module.author.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
// Chứa dữ liệu tên và tiểu sử khi admin tạo hoặc cập nhật tác giả.
public class AuthorRequest {
    @NotBlank(message = "Tên tác giả không được để trống")
    @Size(max = 255, message = "Tên tác giả không được vượt quá 255 ký tự")
    private String authorName;

    @Size(max = 2000, message = "Tiểu sử không được vượt quá 2000 ký tự")
    private String biography;
}
