package com.example.web_bansach.module.auth.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.infrastructure.email.config.MailProperties;
import com.example.web_bansach.infrastructure.email.messaging.EmailMessage;
import com.example.web_bansach.infrastructure.email.messaging.EmailPublisher;
import com.example.web_bansach.module.auth.config.AccountVerificationProperties;
import com.example.web_bansach.module.auth.entity.AccountVerificationCode;
import com.example.web_bansach.module.auth.repository.AccountVerificationCodeRepository;
import com.example.web_bansach.module.auth.service.AccountVerificationService;
import com.example.web_bansach.module.auth.service.email.AccountVerificationEmailTemplate;
import com.example.web_bansach.module.auth.service.email.AccountWelcomeEmailTemplate;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;

@Service
public class AccountVerificationServiceImpl implements AccountVerificationService {

    private static final String VERIFICATION_EMAIL_TYPE = "ACCOUNT_VERIFICATION";
    private static final String WELCOME_EMAIL_TYPE = "ACCOUNT_WELCOME";

    private final AccountVerificationCodeRepository codeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailPublisher emailPublisher;
    private final MailProperties mailProperties;
    private final AccountVerificationProperties properties;
    private final AccountVerificationEmailTemplate verificationTemplate;
    private final AccountWelcomeEmailTemplate welcomeTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    public AccountVerificationServiceImpl(
            AccountVerificationCodeRepository codeRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailPublisher emailPublisher,
            MailProperties mailProperties,
            AccountVerificationProperties properties,
            AccountVerificationEmailTemplate verificationTemplate,
            AccountWelcomeEmailTemplate welcomeTemplate) {
        this.codeRepository = codeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailPublisher = emailPublisher;
        this.mailProperties = mailProperties;
        this.properties = properties;
        this.verificationTemplate = verificationTemplate;
        this.welcomeTemplate = welcomeTemplate;
    }

    @Override
    @Transactional
    public void issueVerificationCode(Users user) {
        invalidateExistingCodes(user);

        String rawCode = "%06d".formatted(secureRandom.nextInt(1_000_000));
        AccountVerificationCode verificationCode = new AccountVerificationCode();
        verificationCode.setUser(user);
        verificationCode.setCodeHash(passwordEncoder.encode(rawCode));
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(properties.getCodeTtlMinutes()));
        codeRepository.save(verificationCode);

        emailPublisher.publishAfterCommit(EmailMessage.create(
                VERIFICATION_EMAIL_TYPE,
                mailProperties.getFrom(),
                user.getEmail(),
                verificationTemplate.subject(),
                verificationTemplate.render(user.getFullName(), rawCode, properties.getCodeTtlMinutes()),
                user.getId()));
    }

    @Override
    @Transactional
    public void resendVerificationCode(String email) {
        Users user = findUser(email);
        if (Boolean.TRUE.equals(user.getIsActive())) {
            throw new BusinessException("Tài khoản đã được xác nhận");
        }

        codeRepository.findFirstByUserAndUsedFalseOrderByCreatedAtDesc(user).ifPresent(latestCode -> {
            LocalDateTime resendAt = latestCode.getCreatedAt()
                    .plusSeconds(properties.getResendCooldownSeconds());
            if (resendAt.isAfter(LocalDateTime.now())) {
                long waitSeconds = Math.max(1, java.time.Duration.between(LocalDateTime.now(), resendAt).toSeconds());
                throw new BusinessException("Vui lòng chờ " + waitSeconds + " giây trước khi gửi lại mã");
            }
        });

        issueVerificationCode(user);
    }

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public void verifyAccount(String email, String rawCode) {
        Users user = findUser(email);
        if (Boolean.TRUE.equals(user.getIsActive())) {
            throw new BusinessException("Tài khoản đã được xác nhận");
        }

        AccountVerificationCode verificationCode = codeRepository
                .findFirstByUserAndUsedFalseOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new BusinessException("Không tìm thấy mã xác nhận còn hiệu lực"));

        if (verificationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            verificationCode.setUsed(true);
            codeRepository.save(verificationCode);
            throw new BusinessException("Mã xác nhận đã hết hạn. Vui lòng yêu cầu mã mới");
        }

        int failedAttempts = verificationCode.getFailedAttempts() == null ? 0 : verificationCode.getFailedAttempts();
        if (failedAttempts >= properties.getMaxAttempts()) {
            verificationCode.setUsed(true);
            codeRepository.save(verificationCode);
            throw new BusinessException("Mã xác nhận đã bị khóa do nhập sai quá nhiều lần");
        }

        if (!passwordEncoder.matches(rawCode, verificationCode.getCodeHash())) {
            int updatedAttempts = failedAttempts + 1;
            verificationCode.setFailedAttempts(updatedAttempts);
            if (updatedAttempts >= properties.getMaxAttempts()) {
                verificationCode.setUsed(true);
            }
            codeRepository.save(verificationCode);
            throw new BusinessException(updatedAttempts >= properties.getMaxAttempts()
                    ? "Mã xác nhận đã bị khóa. Vui lòng yêu cầu mã mới"
                    : "Mã xác nhận không đúng");
        }

        verificationCode.setUsed(true);
        codeRepository.save(verificationCode);
        user.setIsActive(true);
        userRepository.save(user);

        emailPublisher.publishAfterCommit(EmailMessage.create(
                WELCOME_EMAIL_TYPE,
                mailProperties.getFrom(),
                user.getEmail(),
                welcomeTemplate.subject(),
                welcomeTemplate.render(user.getFullName()),
                user.getId()));
    }

    private Users findUser(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessException("Email không hợp lệ");
        }
        Users user = userRepository.findByEmail(email.trim());
        if (user == null) {
            throw new BusinessException("Không tìm thấy tài khoản cần xác nhận");
        }
        return user;
    }

    private void invalidateExistingCodes(Users user) {
        List<AccountVerificationCode> activeCodes = codeRepository.findAllByUserAndUsedFalse(user);
        activeCodes.forEach(code -> code.setUsed(true));
        if (!activeCodes.isEmpty()) {
            codeRepository.saveAll(activeCodes);
        }
    }
}
