package com.example.web_bansach.module.user.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.web_bansach.module.user.dto.response.UserResponse;
import com.example.web_bansach.module.user.entity.Users;

/**
 * Chuyển thực thể người dùng thành dữ liệu phản hồi.
 */
@Component
public class UserMapper {

    /**
     * Chuyển một thực thể người dùng thành dữ liệu phản hồi.
     */
    public UserResponse mapToResponse(Users user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setIsActive(user.getIsActive());
        response.setCreatedAt(user.getCreatedAt());

        // Chuyển danh sách quyền của người dùng.
        if (user.getRoles() != null) {
            response.setRoles(user.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toSet()));
        }

        return response;
    }
}
