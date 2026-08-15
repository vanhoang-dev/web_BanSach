package com.example.web_bansach.security.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.web_bansach.common.constant.MessageConstants;
import com.example.web_bansach.common.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
// Trả phản hồi JSON 401 khi yêu cầu chưa đăng nhập truy cập đường dẫn được bảo vệ.
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    // Ghi mã trạng thái, loại nội dung và lỗi xác thực vào phản hồi HTTP.
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        ErrorResponse errorResponse = ErrorResponse.authenticationError(
                MessageConstants.UNAUTHORIZED,
                request.getRequestURI());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
