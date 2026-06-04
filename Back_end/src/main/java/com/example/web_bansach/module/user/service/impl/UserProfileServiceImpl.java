package com.example.web_bansach.module.user.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.user.dto.request.UpdateUserProfileRequest;
import com.example.web_bansach.module.user.dto.response.UserResponse;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.mapper.UserMapper;
import com.example.web_bansach.module.user.repository.UserRepository;
import com.example.web_bansach.module.user.service.UserProfileService;

/**
 * Xử lý user profile - lấy thông tin, cập nhật profile cá nhân
 */
@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserProfileServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse layNguoiDungTheoId(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));

        return userMapper.mapToResponse(user);
    }

    @Transactional
    @Override
    public void capNhatThongTinCaNhan(Long id, UpdateUserProfileRequest request) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));

        // Update fields (self-service - không được thay đổi username, email, roles)
        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            user.setPhone(request.getPhone());
        }

        if (request.getAddress() != null && !request.getAddress().trim().isEmpty()) {
            user.setAddress(request.getAddress());
        }

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse layThongTinCaNhan(Long userId) {
        return layNguoiDungTheoId(userId);
    }
}
