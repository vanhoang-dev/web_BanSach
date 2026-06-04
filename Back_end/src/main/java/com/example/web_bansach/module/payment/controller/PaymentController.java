package com.example.web_bansach.module.payment.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.module.payment.dto.PaymentRequest;
import com.example.web_bansach.module.payment.dto.PaymentResponse;
import com.example.web_bansach.module.payment.service.PaymentService;

/**
 * Payment Controller - REST endpoints cho payment operations
 * 
 * Endpoints:
 * POST /api/payment/initiate - Khởi tạo thanh toán
 * POST /api/payment/sepay-webhook - Callback từ SePay
 * GET /api/payment/status/{id} - Lấy trạng thái thanh toán
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Khởi tạo thanh toán
     * 
     * @param request - PaymentRequest chứa: orderId, amount, returnUrl, description
     * @return ResponseEntity<ApiResponse<PaymentResponse>>
     */
    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            @RequestBody PaymentRequest request) {
        try {
            logger.info("Initiating payment for order: {}", request.getOrderId());

            PaymentResponse response = paymentService.initiatePayment(request);

            return ResponseEntity.ok(ApiResponse.success("Khởi tạo thanh toán thành công", response));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid payment request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Yêu cầu không hợp lệ: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error initiating payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi khi khởi tạo thanh toán: " + e.getMessage()));
        }
    }

    /**
     * Callback từ SePay khi thanh toán hoàn tất
     * 
     * @param callbackData - Callback data từ SePay
     * @return ResponseEntity
     */
    @PostMapping("/sepay-webhook")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> sepayWebhook(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody java.util.Map<String, Object> callbackData) {
        try {
            logger.info("Received SePay webhook");

            String transactionId = (String) callbackData.getOrDefault("code", callbackData.get("transactionId"));
            Object amountObj = callbackData.containsKey("transferAmount")
                    ? callbackData.get("transferAmount")
                    : callbackData.get("amount");

            if (transactionId == null) {
                logger.warn("SePay webhook missing transaction code");
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "Thiếu mã giao dịch"));
            }

            BigDecimal amount = null;
            if (amountObj instanceof Number) {
                amount = BigDecimal.valueOf(((Number) amountObj).doubleValue());
            } else if (amountObj instanceof String) {
                try {
                    amount = new BigDecimal((String) amountObj);
                } catch (NumberFormatException ex) {
                    amount = null;
                }
            }

            if (amount == null) {
                logger.warn("SePay webhook missing amount for transaction {}", transactionId);
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "Thiếu số tiền giao dịch"));
            }

            // Verify callback signature
            boolean isVerified = paymentService.verifyPaymentCallback(
                    transactionId,
                    amount,
                    authorization);

            if (!isVerified) {
                logger.warn("SePay webhook verification failed");
                return ResponseEntity.ok(ApiResponse.success(java.util.Map.of(
                        "status", "FAILED",
                        "message", "Webhook verification failed")));
            }

            // Update payment status
            String status = "SUCCESS";
            paymentService.updatePaymentStatus(
                    transactionId,
                    status,
                    authorization);

            logger.info("SePay payment processed: {}", status);

            return ResponseEntity.ok(ApiResponse.success(java.util.Map.of(
                    "status", "SUCCESS",
                    "message", "Webhook processed")));
        } catch (Exception e) {
            logger.error("Error processing SePay webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, e.getMessage()));
        }
    }

    /**
     * Lấy trạng thái thanh toán
     * 
     * @param paymentId - Payment ID
     * @return ResponseEntity<ApiResponse<PaymentResponse>>
     */
    @GetMapping("/status/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentStatus(
            @PathVariable Long paymentId) {
        try {
            logger.info("Getting payment status for ID: {}", paymentId);

            PaymentResponse response = paymentService.getPaymentStatus(paymentId);

            return ResponseEntity.ok(ApiResponse.success("Lấy trạng thái thanh toán thành công", response));
        } catch (Exception e) {
            logger.error("Error getting payment status", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Không tìm thấy thông tin thanh toán"));
        }
    }

    /**
     * Hoàn tiền (Refund)
     * 
     * @param paymentId     - Payment ID
     * @param refundRequest - Chứa amount
     * @return ResponseEntity
     */
    @PostMapping("/refund/{paymentId}")
    public ResponseEntity<ApiResponse<?>> refundPayment(
            @PathVariable Long paymentId,
            @RequestBody java.util.Map<String, Object> refundRequest) {
        try {
            logger.info("Processing refund for payment ID: {}", paymentId);

            Object amountObj = refundRequest.get("amount");
            if (amountObj == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "Số tiền hoàn không hợp lệ"));
            }

            BigDecimal amount = amountObj instanceof Number
                    ? BigDecimal.valueOf(((Number) amountObj).doubleValue())
                    : new BigDecimal(amountObj.toString());

            boolean success = paymentService.refundPayment(paymentId, amount);

            if (success) {
                return ResponseEntity.ok(ApiResponse.success("Hoàn tiền thành công", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "Hoàn tiền thất bại"));
            }
        } catch (Exception e) {
            logger.error("Error processing refund", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi khi xử lý hoàn tiền: " + e.getMessage()));
        }
    }
}
