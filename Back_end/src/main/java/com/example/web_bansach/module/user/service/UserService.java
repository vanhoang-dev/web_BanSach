package com.example.web_bansach.module.user.service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.common.logging.LogMaskingUtil;
import com.example.web_bansach.module.user.dto.request.ChangePasswordRequest;
import com.example.web_bansach.module.user.dto.request.AdminUpdateUserRequest;
import com.example.web_bansach.module.user.dto.request.UpdateUserRequest;
import com.example.web_bansach.module.user.dto.response.UserResponse;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Dịch vụ xử lý các thao tác liên quan đến người dùng.
 * Nhận các thành phần phụ thuộc thông qua hàm khởi tạo.
 */
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Users layNguoiDungTheoId(Long id) {
        log.info("Get user by id, userId={}", id);
        if (id == null || id <= 0) {
            throw new BusinessException("ID người dùng không hợp lệ");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsersPagination(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = 0;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }

        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        Page<Users> usersPage = userRepository.findByDeletedAtIsNull(pageable);

        return usersPage.map(this::convertToUserResponse);
    }

    private UserResponse convertToUserResponse(Users users) {
        UserResponse response = new UserResponse();
        response.setUserId(users.getId());
        response.setUsername(users.getUsername());
        response.setEmail(users.getEmail());
        response.setFullName(users.getFullName());
        response.setPhone(users.getPhone());
        response.setAddress(users.getAddress());
        response.setIsActive(users.getIsActive());
        response.setCreatedAt(users.getCreatedAt());
        response.setRoles(users.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet()));
        return response;
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile(String username) {
        log.info("Get current user profile, email={}", LogMaskingUtil.maskEmail(username));
        Users user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("Người dùng không tồn tại");
        }
        return convertToUserResponse(user);
    }

    @Transactional
    public void updateCurrentUserProfile(String username, UpdateUserRequest update) {
        Users user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("Người dùng không tồn tại");
        }
        log.info("Update current user profile, userId={}, email={}",
                user.getId(),
                LogMaskingUtil.maskEmail(user.getEmail()));
        updateUserProfileById(user.getId(), update);
    }

    @Transactional
    public void changeCurrentUserPassword(String username, ChangePasswordRequest request) {
        Users user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("Người dùng không tồn tại");
        }

        if (request == null) {
            throw new BusinessException("Thông tin đổi mật khẩu không hợp lệ");
        }

        if (request.getCurrentPassword() == null || request.getCurrentPassword().trim().isEmpty()) {
            throw new BusinessException("Mật khẩu hiện tại không được để trống");
        }

        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new BusinessException("Mật khẩu mới không được để trống");
        }

        if (request.getConfirmPassword() == null || !request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Xác nhận mật khẩu mới không khớp");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("Mật khẩu hiện tại không đúng");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException("Mật khẩu mới phải khác mật khẩu hiện tại");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void updateUserProfileById(Long userId, UpdateUserRequest update) {
        log.info("Update user profile by id, userId={}", userId);
        Users userData = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        userData.setFullName(update.getFullName());

        if (update.getEmail() != null && !update.getEmail().equals(userData.getEmail())) {
            Users usersEmail = userRepository.findByEmail(update.getEmail());
            if (usersEmail != null) {
                throw new BusinessException("Email đã được dùng, vui lòng chọn email khác");
            }
            userData.setEmail(update.getEmail());
        }

        if (update.getPhone() != null && !update.getPhone().equals(userData.getPhone())) {
            userData.setPhone(update.getPhone());
        }

        if (update.getAddress() != null && !update.getAddress().equals(userData.getAddress())) {
            userData.setAddress(update.getAddress());
        }

        userRepository.save(userData);
        log.info("Update user profile successfully, userId={}", userData.getId());
    }

    @Transactional
    public void xoaNguoiDungTheoId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID người dùng không hợp lệ");
        }

        Users user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        user.setIsActive(false);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void capNhatNguoiDungAdmin(Long userId, AdminUpdateUserRequest request) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("ID người dùng không hợp lệ");
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            user.setPhone(request.getPhone());
        }

        if (request.getAddress() != null && !request.getAddress().isEmpty()) {
            user.setAddress(request.getAddress());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            Users emailExists = userRepository.findByEmail(request.getEmail());
            if (emailExists != null) {
                throw new BusinessException("Email đã được sử dụng");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }
        userRepository.save(user);
    }
}
