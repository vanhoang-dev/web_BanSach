package com.example.web_bansach.module.author.dto.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// Trả thông tin tác giả cho frontend mà không lộ cấu trúc entity.
public class AuthorResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String authorName;
    private String biography;
}
