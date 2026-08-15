package com.example.web_bansach.module.cart.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.web_bansach.common.exception.GlobalExceptionHandler;
import com.example.web_bansach.module.cart.dto.response.CartResponse;
import com.example.web_bansach.module.cart.service.CartCommandService;
import com.example.web_bansach.module.cart.service.CartQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;

class CartControllerTest {

    private final CartCommandService cartCommandService = org.mockito.Mockito.mock(CartCommandService.class);
    private final CartQueryService cartQueryService = org.mockito.Mockito.mock(CartQueryService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CartController(cartCommandService, cartQueryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getMyCart_shouldUseAuthenticatedEmail() throws Exception {
        CartResponse response = new CartResponse();
        response.setCartId(1L);
        response.setTotalItems(0);
        response.setItems(List.of());
        response.setTotalAmount(BigDecimal.ZERO);

        when(cartQueryService.getCart("user@test.com")).thenReturn(response);
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("user@test.com", null));

        mockMvc.perform(get("/user/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.cartId").value(1))
                .andExpect(jsonPath("$.data.totalItems").value(0));
    }

    @Test
    void addToCart_shouldRejectNegativeBookIdBeforeServiceCall() throws Exception {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("user@test.com", null));

        mockMvc.perform(post("/user/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "bookId", -1,
                                "quantity", 1))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errors.bookId").exists());

        org.mockito.Mockito.verifyNoInteractions(cartCommandService);
    }

    @Test
    void addToCart_shouldRejectZeroQuantityBeforeServiceCall() throws Exception {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("user@test.com", null));

        mockMvc.perform(post("/user/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "bookId", 1,
                                "quantity", 0))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errors.quantity").exists());

        org.mockito.Mockito.verifyNoInteractions(cartCommandService);
    }
}
