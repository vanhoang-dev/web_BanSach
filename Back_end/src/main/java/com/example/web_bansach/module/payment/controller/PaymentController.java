package com.example.web_bansach.module.payment.controller;

import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
    private static final Pattern PAYMENT_CODE_PATTERN = Pattern.compile("(?i)SEP-?\\d+");

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
        return ResponseEntity.ok(ApiResponse.success("Khoi tao thanh toan thanh cong", response));
    }

    @PostMapping("/sepay-webhook")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sepayWebhook(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, Object> callbackData) {
        logger.info("Received SePay webhook");

        String transactionId = extractTransactionId(callbackData);
        BigDecimal amount = parseAmount(callbackData);

        if (transactionId == null || transactionId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Thieu ma giao dich"));
        }

        if (amount == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Thieu so tien giao dich"));
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
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentStatus(
            Authentication authentication,
            @PathVariable Long paymentId) {
        PaymentResponse response = paymentService.getPaymentStatus(
                authentication.getName(),
                isAdmin(authentication),
                paymentId);
        return ResponseEntity.ok(ApiResponse.success("Lay trang thai thanh toan thanh cong", response));
    }

    @GetMapping("/status/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentStatusByOrderId(
            Authentication authentication,
            @PathVariable Long orderId) {
        PaymentResponse response = paymentService.getPaymentStatusByOrderId(
                authentication.getName(),
                isAdmin(authentication),
                orderId);
        return ResponseEntity.ok(ApiResponse.success("Lay trang thai thanh toan thanh cong", response));
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch("ROLE_ADMIN"::equals);
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

    private String extractTransactionId(Map<String, Object> callbackData) {
        String direct = firstPaymentCode(callbackData, "code", "transactionId", "paymentCode");
        if (direct != null) {
            return direct;
        }

        String content = firstText(callbackData, "content", "description", "transferContent", "transactionContent",
                "referenceCode");
        if (content == null || content.isBlank()) {
            return null;
        }

        Matcher matcher = PAYMENT_CODE_PATTERN.matcher(content);
        return matcher.find() ? matcher.group() : null;
    }

    private String firstPaymentCode(Map<String, Object> data, String... keys) {
        for (String key : keys) {
            Object value = data.get(key);
            if (value != null) {
                Matcher matcher = PAYMENT_CODE_PATTERN.matcher(value.toString());
                if (matcher.find()) {
                    return matcher.group();
                }
            }
        }
        return null;
    }

    private String firstText(Map<String, Object> data, String... keys) {
        for (String key : keys) {
            Object value = data.get(key);
            if (value != null) {
                String text = value.toString().trim();
                if (!text.isEmpty() && !"-".equals(text)) {
                    return text;
                }
            }
        }
        return null;
    }
}
