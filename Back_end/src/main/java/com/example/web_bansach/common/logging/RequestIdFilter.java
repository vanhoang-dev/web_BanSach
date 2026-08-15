package com.example.web_bansach.common.logging;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Gan requestId vao MDC de moi log trong cung request co the truy vet duoc.
 */
@Component
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String REQUEST_ID_KEY = "requestId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        try {
            MDC.put(REQUEST_ID_KEY, requestId);
            response.setHeader(REQUEST_ID_HEADER, requestId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(REQUEST_ID_KEY);
        }
    }
}
