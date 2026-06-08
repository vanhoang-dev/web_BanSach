package com.example.web_bansach.module.user.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
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
import com.example.web_bansach.infrastructure.external.EmailSender;
import com.example.web_bansach.module.auth.dto.request.ForgotPasswordRequest;
import com.example.web_bansach.module.auth.dto.request.LoginRequest;
import com.example.web_bansach.module.auth.dto.request.ResetPasswordRequest;
import com.example.web_bansach.module.auth.dto.request.UserRequest;
import com.example.web_bansach.module.auth.dto.response.LoginResponse;
import com.example.web_bansach.module.auth.entity.PasswordResetToken;
import com.example.web_bansach.module.auth.repository.PasswordResetTokenRepository;
import com.example.web_bansach.module.user.entity.Roles;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.RolesRepository;
import com.example.web_bansach.module.user.repository.UserRepository;
import com.example.web_bansach.module.user.service.AuthService;
import com.example.web_bansach.security.jwt.JwtTokenProvider;

@Service
public class AuthServiceImpl implements AuthService {

    private static final long PASSWORD_RESET_TOKEN_TTL_MINUTES = 30;

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailSender emailSender;
    private final RolesRepository rolesRepository;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public AuthServiceImpl(UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailSender emailSender,
            RolesRepository rolesRepository) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailSender = emailSender;
        this.rolesRepository = rolesRepository;
    }

    @Transactional
    @Override
    public void taoTaiKhoanMoi(UserRequest request) {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new BusinessException("Tên đăng nhập đã tồn tại");
        }

        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new BusinessException("Email đã tồn tại");
        }

        Roles userRole = rolesRepository.findByName("ROLE_USER");
        if (userRole == null) {
            throw new BusinessException("Chưa cấu hình quyền ROLE_USER");
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setIsActive(true);
        user.setRoles(Set.of(userRole));

        userRepository.save(user);
    }

    @Transactional
    @Override
    public LoginResponse dangNhap(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Email hoặc mật khẩu không đúng");
        }

        Users user = userRepository.findByEmail(request.getEmail());
        if (user == null || !Boolean.TRUE.equals(user.getIsActive())) {
            throw new BadCredentialsException("Email hoặc mật khẩu không đúng");
        }

        String token = buildAccessToken(user);
        return buildLoginResponse(user, token);
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
                + "<p>Nhấn vào liên kết sau để đặt lại mật khẩu: <a href=\"" + resetLink
                + "\">Đặt lại mật khẩu</a></p>"
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
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private LoginResponse buildLoginResponse(Users user, String token) {
        LoginResponse response = new LoginResponse();
        response.setJwt(token);
        response.setUserId(user.getId());
        response.setUsername(user.getEmail());
        response.setRoles(user.getRoles() == null ? Set.of()
                : user.getRoles().stream()
                        .map(Roles::getName)
                        .collect(Collectors.toSet()));
        return response;
    }

    private String buildAccessToken(Users user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("roles", user.getRoles() == null ? Set.of()
                : user.getRoles().stream()
                        .map(Roles::getName)
                        .collect(Collectors.toSet()));
        return jwtTokenProvider.generateToken(user.getEmail(), claims);
    }
}
