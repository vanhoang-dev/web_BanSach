package com.example.web_bansach.module.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.web_bansach.common.exception.GlobalExceptionHandler;
import com.example.web_bansach.module.auth.dto.request.LoginRequest;
import com.example.web_bansach.module.auth.dto.response.LoginResponse;
import com.example.web_bansach.module.user.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

class AuthControllerTest {

    private final AuthService authService = org.mockito.Mockito.mock(AuthService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void dangNhap_shouldReturnTokenAndRoles() throws Exception {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setJwt("access-token");
        loginResponse.setUserId(1L);
        loginResponse.setUsername("user@test.com");
        loginResponse.setRoles(Set.of("ROLE_USER"));

        when(authService.dangNhap(any(LoginRequest.class))).thenReturn(loginResponse);

        LoginRequest request = new LoginRequest("user@test.com", "secret123");

        mockMvc.perform(post("/tai-khoan/dang-nhap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.jwt").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").doesNotExist())
                .andExpect(jsonPath("$.data.roles[0]").value("ROLE_USER"));
    }
}
