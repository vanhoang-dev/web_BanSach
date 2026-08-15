package com.example.web_bansach.module.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.infrastructure.messaging.email.EmailQueuePublisher;
import com.example.web_bansach.module.auth.dto.request.UserRequest;
import com.example.web_bansach.module.auth.repository.PasswordResetTokenRepository;
import com.example.web_bansach.module.user.entity.Roles;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.RolesRepository;
import com.example.web_bansach.module.user.repository.UserRepository;
import com.example.web_bansach.module.user.service.impl.AuthServiceImpl;
import com.example.web_bansach.security.jwt.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private EmailQueuePublisher emailQueuePublisher;
    @Mock private RolesRepository rolesRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void taoTaiKhoanMoi_shouldAssignRoleUser() {
        Roles roleUser = new Roles();
        roleUser.setId(2L);
        roleUser.setName("ROLE_USER");

        when(userRepository.findByUsername("newuser")).thenReturn(null);
        when(userRepository.findByEmail("new@test.com")).thenReturn(null);
        when(passwordEncoder.encode("Secret123!")).thenReturn("encoded");
        when(rolesRepository.findByName("ROLE_USER")).thenReturn(roleUser);

        authService.taoTaiKhoanMoi(userRequest());

        ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);
        verify(userRepository).save(captor.capture());
        Users savedUser = captor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("new@test.com");
        assertThat(savedUser.getPassword()).isEqualTo("encoded");
        assertThat(savedUser.getRoles()).extracting(Roles::getName).containsExactly("ROLE_USER");
    }

    @Test
    void taoTaiKhoanMoi_shouldFailWhenRoleUserMissing() {
        when(userRepository.findByUsername("newuser")).thenReturn(null);
        when(userRepository.findByEmail("new@test.com")).thenReturn(null);
        when(rolesRepository.findByName("ROLE_USER")).thenReturn(null);

        assertThatThrownBy(() -> authService.taoTaiKhoanMoi(userRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ROLE_USER");
    }

    private UserRequest userRequest() {
        UserRequest request = new UserRequest();
        request.setUsername("newuser");
        request.setEmail("new@test.com");
        request.setPassword("Secret123!");
        request.setFullName("New User");
        return request;
    }
}
