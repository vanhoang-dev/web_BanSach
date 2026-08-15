package com.example.web_bansach.module.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.user.entity.Users;

@Repository
// Truy cập tài khoản theo ID, username, email và trạng thái xóa mềm.
public interface UserRepository extends JpaRepository<Users, Long> {
    public Users findByUsername(String username);

    public Users findByEmail(String email);

    Page<Users> findByDeletedAtIsNull(Pageable pageable);

    long countByDeletedAtIsNull();
}



