package com.example.web_bansach.module.payment.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.module.payment.dto.PaymentRequest;
import com.example.web_bansach.module.payment.dto.PaymentResponse;
import com.example.web_bansach.module.payment.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            Authentication authentication,
            @RequestBody PaymentRequest request) throws Exception {
        logger.info("Initiating payment for order: {}", request.getOrderId());
        PaymentResponse response = paymentService.initiatePayment(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Khởi tạo thanh toán thành công", response));
    }

    @PostMapping("/sepay-webhook")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sepayWebhook(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, Object> callbackData) {
        logger.info("Received SePay webhook");

        String transactionId = (String) callbackData.getOrDefault("code", callbackData.get("transactionId"));
        BigDecimal amount = parseAmount(callbackData);

        if (transactionId == null || transactionId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Thiếu mã giao dịch"));
        }

        if (amount == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Thiếu số tiền giao dịch"));
        }

        boolean verified = paymentService.verifyPaymentCallback(transactionId, amount, authorization);
        if (!verified) {
            logger.warn("SePay webhook verification failed for transaction {}", transactionId);
            return ResponseEntity.ok(ApiResponse.success(Map.of(
                    "status", "FAILED",
                    "message", "Webhook verification failed")));
        }

        paymentService.updatePaymentStatus(transactionId, "SUCCESS", authorization);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "status", "SUCCESS",
                "message", "Webhook processed")));
    }

    @GetMapping("/status/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentStatus(@PathVariable Long paymentId) {
        PaymentResponse response = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(ApiResponse.success("Lấy trạng thái thanh toán thành công", response));
    }

    @GetMapping("/status/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentStatusByOrderId(@PathVariable Long orderId) {
        PaymentResponse response = paymentService.getPaymentStatusByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success("Lấy trạng thái thanh toán thành công", response));
    }

    private BigDecimal parseAmount(Map<String, Object> callbackData) {
        Object amountObj = callbackData.containsKey("transferAmount")
                ? callbackData.get("transferAmount")
                : callbackData.get("amount");

        if (amountObj instanceof Number) {
            return BigDecimal.valueOf(((Number) amountObj).doubleValue());
        }

        if (amountObj instanceof String) {
            try {
                return new BigDecimal((String) amountObj);
            } catch (NumberFormatException ex) {
                return null;
            }
        }

        return null;
    }
}
