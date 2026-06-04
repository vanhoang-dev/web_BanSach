package com.example.web_bansach.module.user.service.impl;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.infrastructure.external.EmailSender;
import com.example.web_bansach.module.auth.dto.request.ForgotPasswordRequest;
import com.example.web_bansach.module.auth.dto.request.LoginRequest;
import com.example.web_bansach.module.auth.dto.request.RefreshTokenRequest;
import com.example.web_bansach.module.auth.dto.request.ResetPasswordRequest;
import com.example.web_bansach.module.auth.dto.request.UserRequest;
import com.example.web_bansach.module.auth.dto.response.LoginResponse;
import com.example.web_bansach.module.auth.entity.PasswordResetToken;
import com.example.web_bansach.module.auth.entity.RefreshToken;
import com.example.web_bansach.module.auth.repository.PasswordResetTokenRepository;
import com.example.web_bansach.module.auth.repository.RefreshTokenRepository;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;
import com.example.web_bansach.module.user.service.AuthService;
import com.example.web_bansach.security.jwt.JwtTokenProvider;

/**
 * Xử lý authentication - đăng ký, đăng nhập
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final long PASSWORD_RESET_TOKEN_TTL_MINUTES = 30;

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailSender emailSender;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    public AuthServiceImpl(UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder,
            RefreshTokenRepository refreshTokenRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailSender emailSender) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailSender = emailSender;
    }

    @Transactional
    @Override
    public void taoTaiKhoanMoi(UserRequest request) {
        // Kiểm tra username có tồn tại
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new BusinessException("Username đã tồn tại");
        }

        // Kiểm tra email có tồn tại
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new BusinessException("Email đã tồn tại");
        }

        // Tạo user mới
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setIsActive(true);

        userRepository.save(user);
    }

    @Transactional
    @Override
    public LoginResponse dangNhap(LoginRequest request) {
        try {
            // Authenticate
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            // Find user by email
            Users user = userRepository.findByEmail(request.getEmail());
            if (user == null) {
                throw new BadCredentialsException("User không tồn tại");
            }

            String accessToken = buildAccessToken(user);
            String refreshToken = persistRefreshToken(user);

            return buildLoginResponse(user, accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Username hoặc password sai");
        }
    }

    @Transactional
    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = normalizeToken(request != null ? request.getRefreshToken() : null, "Refresh token không hợp lệ");

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new BusinessException("Refresh token không hợp lệ hoặc đã hết hạn");
        }

        String email = jwtTokenProvider.extractRefreshUsername(refreshToken);
        Users user = userRepository.findByEmail(email);
        if (user == null || !Boolean.TRUE.equals(user.getIsActive())) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException("Refresh token không tồn tại"));

        if (Boolean.TRUE.equals(storedToken.getRevoked())) {
            throw new BusinessException("Refresh token đã bị thu hồi");
        }

        if (storedToken.getExpiresAt() != null && storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Refresh token đã hết hạn");
        }

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        String newAccessToken = buildAccessToken(user);
        String newRefreshToken = persistRefreshToken(user);
        return buildLoginResponse(user, newAccessToken, newRefreshToken);
    }

    @Transactional
    @Override
    public void dangXuat(RefreshTokenRequest request) {
        String refreshToken = normalizeToken(request != null ? request.getRefreshToken() : null, "Refresh token không hợp lệ");

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException("Refresh token không tồn tại"));

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);
    }

    @Transactional
    @Override
    public void quenMatKhau(ForgotPasswordRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BusinessException("Email không hợp lệ");
        }

        Users user = userRepository.findByEmail(request.getEmail().trim());
        if (user == null) {
            return;
        }

        String resetToken = UUID.randomUUID().toString();
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(resetToken);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(PASSWORD_RESET_TOKEN_TTL_MINUTES));
        token.setUsed(false);
        passwordResetTokenRepository.save(token);

        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
        String emailBody = "<p>Bạn đã yêu cầu đặt lại mật khẩu.</p>"
                + "<p>Nhấn vào liên kết sau để đặt lại mật khẩu: <a href=\"" + resetLink + "\">Đặt lại mật khẩu</a></p>"
                + "<p>Liên kết sẽ hết hạn sau " + PASSWORD_RESET_TOKEN_TTL_MINUTES + " phút.</p>";
        emailSender.sendMessage("noreply@webbansach.local", user.getEmail(), "Đặt lại mật khẩu", emailBody);
    }

    @Transactional
    @Override
    public void datLaiMatKhau(ResetPasswordRequest request) {
        if (request == null) {
            throw new BusinessException("Thông tin đặt lại mật khẩu không hợp lệ");
        }

        if (request.getResetToken() == null || request.getResetToken().trim().isEmpty()) {
            throw new BusinessException("Token đặt lại mật khẩu không được để trống");
        }

        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new BusinessException("Mật khẩu mới không được để trống");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Xác nhận mật khẩu mới không khớp");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getResetToken().trim())
                .orElseThrow(() -> new BusinessException("Token đặt lại mật khẩu không hợp lệ"));

        if (Boolean.TRUE.equals(resetToken.getUsed())) {
            throw new BusinessException("Token đặt lại mật khẩu đã được sử dụng");
        }

        if (resetToken.getExpiresAt() != null && resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Token đặt lại mật khẩu đã hết hạn");
        }

        Users user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUserId(user.getId());
        for (RefreshToken token : refreshTokens) {
            token.setRevoked(true);
        }
        refreshTokenRepository.saveAll(refreshTokens);
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private LoginResponse buildLoginResponse(Users user, String accessToken, String refreshToken) {
        LoginResponse response = new LoginResponse();
        response.setJwt(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUserId(user.getId());
        response.setUsername(user.getEmail());
        response.setRoles(user.getRoles() == null ? Set.of()
                : user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet()));
        return response;
    }

    private String buildAccessToken(Users user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("roles", user.getRoles() == null ? Set.of()
                : user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet()));
        return jwtTokenProvider.generateToken(user.getEmail(), claims);
    }

    private String persistRefreshToken(Users user) {
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(user.getEmail());
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setExpiresAt(LocalDateTime.now().plusNanos(jwtTokenProvider.getRefreshTokenExpirationInMillis() * 1_000_000L));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
        return refreshTokenValue;
    }

    private String normalizeToken(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException(errorMessage);
        }
        return value.trim();
    }
}
