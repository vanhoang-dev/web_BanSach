package com.example.web_bansach.module.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.infrastructure.email.config.MailProperties;
import com.example.web_bansach.infrastructure.email.messaging.EmailMessage;
import com.example.web_bansach.infrastructure.email.messaging.EmailPublisher;
import com.example.web_bansach.module.auth.config.AccountVerificationProperties;
import com.example.web_bansach.module.auth.entity.AccountVerificationCode;
import com.example.web_bansach.module.auth.repository.AccountVerificationCodeRepository;
import com.example.web_bansach.module.auth.service.email.AccountVerificationEmailTemplate;
import com.example.web_bansach.module.auth.service.email.AccountWelcomeEmailTemplate;
import com.example.web_bansach.module.auth.service.impl.AccountVerificationServiceImpl;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AccountVerificationServiceImplTest {

    @Mock private AccountVerificationCodeRepository codeRepository;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailPublisher emailPublisher;

    private AccountVerificationServiceImpl service;
    private AccountVerificationProperties properties;
    private MailProperties mailProperties;

    @BeforeEach
    void setUp() {
        properties = new AccountVerificationProperties();
        mailProperties = new MailProperties();
        mailProperties.setFrom("noreply@test.local");
        service = new AccountVerificationServiceImpl(
                codeRepository,
                userRepository,
                passwordEncoder,
                emailPublisher,
                mailProperties,
                properties,
                new AccountVerificationEmailTemplate(),
                new AccountWelcomeEmailTemplate());
    }

    @Test
    void issueVerificationCode_shouldStoreHashAndPublishOtpEmail() {
        Users user = pendingUser();
        when(passwordEncoder.encode(any(String.class))).thenReturn("hashed-code");

        service.issueVerificationCode(user);

        ArgumentCaptor<AccountVerificationCode> codeCaptor = ArgumentCaptor.forClass(AccountVerificationCode.class);
        verify(codeRepository).save(codeCaptor.capture());
        assertThat(codeCaptor.getValue().getCodeHash()).isEqualTo("hashed-code");
        assertThat(codeCaptor.getValue().getExpiresAt()).isAfter(LocalDateTime.now().plusMinutes(9));

        ArgumentCaptor<EmailMessage> messageCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailPublisher).publishAfterCommit(messageCaptor.capture());
        assertThat(messageCaptor.getValue().getEmailType()).isEqualTo("ACCOUNT_VERIFICATION");
        assertThat(messageCaptor.getValue().getTo()).isEqualTo("reader@test.com");
    }

    @Test
    void verifyAccount_shouldActivateUserAndPublishWelcomeEmail() {
        Users user = pendingUser();
        AccountVerificationCode code = validCode(user);
        when(userRepository.findByEmail("reader@test.com")).thenReturn(user);
        when(codeRepository.findFirstByUserAndUsedFalseOrderByCreatedAtDesc(user)).thenReturn(Optional.of(code));
        when(passwordEncoder.matches("123456", "hashed-code")).thenReturn(true);

        service.verifyAccount("reader@test.com", "123456");

        assertThat(user.getIsActive()).isTrue();
        assertThat(code.getUsed()).isTrue();
        verify(userRepository).save(user);
        ArgumentCaptor<EmailMessage> messageCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailPublisher).publishAfterCommit(messageCaptor.capture());
        assertThat(messageCaptor.getValue().getEmailType()).isEqualTo("ACCOUNT_WELCOME");
    }

    @Test
    void verifyAccount_shouldCountInvalidAttempts() {
        Users user = pendingUser();
        AccountVerificationCode code = validCode(user);
        when(userRepository.findByEmail("reader@test.com")).thenReturn(user);
        when(codeRepository.findFirstByUserAndUsedFalseOrderByCreatedAtDesc(user)).thenReturn(Optional.of(code));
        when(passwordEncoder.matches("000000", "hashed-code")).thenReturn(false);

        assertThatThrownBy(() -> service.verifyAccount("reader@test.com", "000000"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("không đúng");

        assertThat(code.getFailedAttempts()).isEqualTo(1);
        assertThat(user.getIsActive()).isFalse();
        verify(codeRepository).save(code);
    }

    private Users pendingUser() {
        Users user = new Users();
        user.setId(10L);
        user.setEmail("reader@test.com");
        user.setFullName("Bạn đọc");
        user.setIsActive(false);
        return user;
    }

    private AccountVerificationCode validCode(Users user) {
        AccountVerificationCode code = new AccountVerificationCode();
        code.setUser(user);
        code.setCodeHash("hashed-code");
        code.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        code.setUsed(false);
        code.setFailedAttempts(0);
        return code;
    }
}
