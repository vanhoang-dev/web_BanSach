package com.example.web_bansach.module.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.web_bansach.common.exception.GlobalExceptionHandler;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.payment.dto.PaymentRequest;
import com.example.web_bansach.module.payment.dto.PaymentResponse;
import com.example.web_bansach.module.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;

class PaymentControllerTest {

    private final PaymentService paymentService = org.mockito.Mockito.mock(PaymentService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PaymentController(paymentService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void initiatePayment_shouldUseAuthenticatedEmailAndReturnPaymentUrl() throws Exception {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(1L);
        response.setPaymentUrl("http://pay");
        response.setTransactionId("SEP-1");
        response.setAmount(new BigDecimal("100000"));
        response.setStatus("PENDING");

        when(paymentService.initiatePayment(eq("user@test.com"), any(PaymentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/payment/initiate")
                        .principal(new UsernamePasswordAuthenticationToken("user@test.com", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.paymentUrl").value("http://pay"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        verify(paymentService).initiatePayment(eq("user@test.com"), any(PaymentRequest.class));
    }

    @Test
    void getPaymentStatus_shouldReturn404WhenPaymentMissing() throws Exception {
        when(paymentService.getPaymentStatus(999L))
                .thenThrow(new ResourceNotFoundException("Không tìm thấy thông tin thanh toán"));

        mockMvc.perform(get("/api/payment/status/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404));
    }

    @Test
    void sepayWebhook_shouldReturn400WhenTransactionCodeMissing() throws Exception {
        mockMvc.perform(post("/api/payment/sepay-webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("amount", 100000))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400));
    }

    @Test
    void sepayWebhook_shouldExtractPaymentCodeFromRealTransferContent() throws Exception {
        Map<String, Object> payload = new java.util.LinkedHashMap<>();
        payload.put("gateway", "MBBank");
        payload.put("transactionDate", "2026-06-12 15:25:00");
        payload.put("accountNumber", "82000444213546");
        payload.put("subAccount", null);
        payload.put("code", null);
        payload.put("content", "133153544486-SEP4-CHUYEN TIEN-OQCH000DN0Z4-MOMO133153544486MOMO");
        payload.put("transferType", "in");
        payload.put("description", "BankAPINotify 133153544486-SEP4-CHUYEN TIEN-OQCH000DN0Z4-MOMO133153544486MOMO");
        payload.put("transferAmount", 5000);
        payload.put("referenceCode", "FT26163080353409");
        payload.put("accumulated", 0);
        payload.put("id", 62985170);

        when(paymentService.verifyPaymentCallback(eq("SEP4"), any(BigDecimal.class), eq("Apikey test-key")))
                .thenReturn(true);

        mockMvc.perform(post("/api/payment/sepay-webhook")
                        .header("Authorization", "Apikey test-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SUCCESS"));

        verify(paymentService).updatePaymentStatus("SEP4", "SUCCESS", "Apikey test-key");
    }

    private PaymentRequest paymentRequest() {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(1L);
        request.setAmount(new BigDecimal("100000"));
        request.setReturnUrl("http://return");
        request.setDescription("order 1");
        return request;
    }
}
