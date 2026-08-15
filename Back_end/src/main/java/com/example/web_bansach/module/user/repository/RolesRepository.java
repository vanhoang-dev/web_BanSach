package com.example.web_bansach.module.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.user.entity.Roles;

@Repository
// Truy cập quyền theo tên để đăng ký và phân quyền tài khoản.
public interface RolesRepository extends JpaRepository<Roles, Long> {

    Roles findByName(String name);
}



