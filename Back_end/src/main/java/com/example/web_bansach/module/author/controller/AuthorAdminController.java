package com.example.web_bansach.module.author.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.common.response.PageResponse;
import com.example.web_bansach.module.author.dto.request.AuthorRequest;
import com.example.web_bansach.module.author.dto.response.AuthorResponse;
import com.example.web_bansach.module.author.entity.Author;
import com.example.web_bansach.module.author.service.AuthorCommandService;
import com.example.web_bansach.module.author.service.AuthorQueryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/authors")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
// Cung cấp API quản trị để thêm, sửa, xóa và xem danh sách tác giả.
public class AuthorAdminController {

    private final AuthorCommandService authorCommandService;

    private final AuthorQueryService authorQueryService;

    // Khởi tạo controller với service ghi và service đọc dữ liệu tác giả.
    public AuthorAdminController(AuthorCommandService authorCommandService, AuthorQueryService authorQueryService) {
        this.authorCommandService = authorCommandService;
        this.authorQueryService = authorQueryService;
    }

    @PostMapping
    // Tạo một tác giả mới từ dữ liệu quản trị viên gửi lên.
    public ResponseEntity<ApiResponse<Author>> createAuthor(@Valid @RequestBody AuthorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(authorCommandService.addAuthorService(request)));
    }

    @PutMapping("/{id}")
    // Cập nhật thông tin tác giả đã tồn tại.
    public ResponseEntity<ApiResponse<Author>> updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody AuthorRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authorCommandService.updateAuthorService(id, request)));
    }

    @DeleteMapping("/{id}")
    // Xóa tác giả theo ID sau khi kiểm tra ràng buộc nghiệp vụ.
    public ResponseEntity<ApiResponse<?>> deleteAuthor(@PathVariable Long id) {
        authorCommandService.deleAuthorService(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa tác giả thành công", null));
    }

    @GetMapping
    // Trả danh sách tác giả có phân trang cho màn hình admin.
    public ResponseEntity<ApiResponse<PageResponse<AuthorResponse>>> getAllAuthors(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(authorQueryService.getAllAuthorPagination(page, size))));
    }
}
