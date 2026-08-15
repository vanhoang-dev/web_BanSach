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
import com.example.web_bansach.common.logging.LogMaskingUtil;
import com.example.web_bansach.infrastructure.messaging.email.EmailMessage;
import com.example.web_bansach.infrastructure.messaging.email.EmailQueuePublisher;
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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
// Hiện thực toàn bộ luồng đăng ký, đăng nhập, JWT và khôi phục mật khẩu.
public class AuthServiceImpl implements AuthService {

    private static final long PASSWORD_RESET_TOKEN_TTL_MINUTES = 30;
    private static final String PASSWORD_RESET_EMAIL = "PASSWORD_RESET";

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailQueuePublisher emailQueuePublisher;
    private final RolesRepository rolesRepository;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${spring.mail.username:noreply@webbansach.local}")
    private String mailFrom;

    // Khởi tạo service với repository, bộ mã hóa, JWT, email và trình xác thực.
    public AuthServiceImpl(UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailQueuePublisher emailQueuePublisher,
            RolesRepository rolesRepository) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailQueuePublisher = emailQueuePublisher;
        this.rolesRepository = rolesRepository;
    }

    @Transactional
    @Override
    // Kiểm tra dữ liệu trùng, mã hóa mật khẩu và lưu tài khoản ROLE_USER mới.
    public void taoTaiKhoanMoi(UserRequest request) {
        log.info("Start register user, email={}", LogMaskingUtil.maskEmail(request.getEmail()));

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
        user.setPhone(normalizeOptional(request.getPhone()));
        user.setAddress(normalizeOptional(request.getAddress()));
        user.setIsActive(true);
        user.setRoles(Set.of(userRole));

        userRepository.save(user);
        log.info("Register user successfully, userId={}, email={}",
                user.getId(),
                LogMaskingUtil.maskEmail(user.getEmail()));
    }

    // Chuẩn hóa trường tùy chọn: bỏ khoảng trắng và đổi chuỗi rỗng thành null.
    private String normalizeOptional(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    @Transactional
    @Override
    // Xác thực tài khoản đang hoạt động rồi phát JWT cho phiên đăng nhập.
    public LoginResponse dangNhap(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            log.warn("Login failed, email={}", LogMaskingUtil.maskEmail(request.getEmail()));
            throw new BadCredentialsException("Email hoặc mật khẩu không đúng");
        }

        Users user = userRepository.findByEmail(request.getEmail());
        if (user == null || !Boolean.TRUE.equals(user.getIsActive())) {
            log.warn("Login failed, email={}", LogMaskingUtil.maskEmail(request.getEmail()));
            throw new BadCredentialsException("Email hoặc mật khẩu không đúng");
        }

        String token = buildAccessToken(user);
        log.info("Login successfully, userId={}, email={}", user.getId(), LogMaskingUtil.maskEmail(user.getEmail()));
        return buildLoginResponse(user, token);
    }

    @Transactional
    @Override
    // Tạo reset token có hạn dùng và gửi đường dẫn khôi phục qua email.
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

        EmailMessage emailMessage = EmailMessage.create(
                PASSWORD_RESET_EMAIL,
                mailFrom,
                user.getEmail(),
                "Đặt lại mật khẩu",
                emailBody,
                user.getId());
        emailQueuePublisher.publishAfterCommit(emailMessage);
    }

    @Transactional
    @Override
    // Xác minh reset token, cập nhật mật khẩu và đánh dấu token đã sử dụng.
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
    // Kiểm tra mật khẩu người dùng nhập bằng PasswordEncoder của Spring Security.
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // Đóng gói JWT, định danh và danh sách quyền thành response đăng nhập.
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

    // Tạo JWT chứa userId và roles để backend phân quyền cho các request sau.
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
