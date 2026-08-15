package com.example.web_bansach.module.user.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Ánh xạ tài khoản người dùng, hồ sơ, trạng thái và danh sách quyền.
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id; // ID người dùng

    @Column(nullable = false, unique = true)
    private String username; // Tên đăng nhập (duy nhất)

    @Column(nullable = false)
    private String password; // Mật khẩu (đã mã hóa BCrypt)

    @Column(nullable = false, unique = true)
    private String email; // Email (duy nhất)

    private String fullName; // Họ và tên
    private String phone; // Số điện thoại
    private String address; // Địa chỉ

    private Boolean isActive = true; // Trạng thái kích hoạt tài khoản

    private LocalDateTime createdAt; // Thời gian tạo tài khoản
    private LocalDateTime deletedAt; // Thời gian xóa (soft delete)

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "roles_id"))
    private Set<Roles> roles = new HashSet<>(); // Danh sách vai trò của người dùng

}
