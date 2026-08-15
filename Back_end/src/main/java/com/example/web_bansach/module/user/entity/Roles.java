package com.example.web_bansach.module.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
// Ánh xạ quyền ROLE_USER hoặc ROLE_ADMIN dùng bởi Spring Security.
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roles_id")
    private Long id; // ID vai trò

    @Column(name = "roles_name", unique = true, nullable = false)
    private String name; // Tên vai trò (ROLE_USER, ROLE_ADMIN)

}
