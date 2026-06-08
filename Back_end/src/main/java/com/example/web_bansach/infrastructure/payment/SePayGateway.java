package com.example.web_bansach.infrastructure.payment;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.web_bansach.common.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * SePay payment gateway implementation.
 *
 * Luồng hiện tại dùng SePay QR payment + webhook xác nhận giao dịch.
 */
@Component
public class SePayGateway implements PaymentGateway {
    private static final Logger logger = LoggerFactory.getLogger(SePayGateway.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("${sepay.bank-code:MB}")
    private String bankCode;

    @Value("${sepay.account-number:}")
    private String accountNumber;

    @Value("${sepay.account-name:SEPAY JSC}")
    private String accountName;

    @Value("${sepay.payment-template:compact}")
    private String paymentTemplate;

    @Value("${sepay.qr-base-url:https://qr.sepay.vn/img}")
    private String qrBaseUrl;

    @Value("${sepay.webhook-api-key:}")
    private String webhookApiKey;

    @Value("${sepay.status-api-url:}")
    private String statusApiUrl;

    @Value("${sepay.refund-api-url:}")
    private String refundApiUrl;

    @Value("${sepay.api-key:}")
    private String apiKey;

    @Value("${sepay.request-timeout-ms:5000}")
    private long requestTimeoutMs;

    @Value("${sepay.return-url}")
    private String defaultReturnUrl;

    @Override
    public String initiatePayment(Long orderId, BigDecimal amount, String returnUrl, String description)
            throws Exception {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new BusinessException("Chưa cấu hình tài khoản SePay để tạo QR thanh toán");
        }

        String paymentCode = buildPaymentCode(orderId);
        String qrDescription = description != null && !description.trim().isEmpty()
                ? description.trim()
                : paymentCode;
        String finalReturnUrl = returnUrl != null && !returnUrl.trim().isEmpty()
                ? returnUrl.trim()
                : defaultReturnUrl;

        String paymentUrl = qrBaseUrl
                + "?bank=" + encode(bankCode)
                + "&acc=" + encode(accountNumber)
                + "&template=" + encode(paymentTemplate)
                + "&amount=" + amount.longValue()
                + "&des=" + encode(paymentCode)
                + "&name=" + encode(accountName)
                + "&returnUrl=" + encode(finalReturnUrl)
                + "&note=" + encode(qrDescription);

        logger.info("Generated SePay QR payment URL for order: {}, code: {}", orderId, paymentCode);
        return paymentUrl;
    }

    @Override
    public boolean verifyPayment(String transactionId, BigDecimal amount, String signature) {
        if (transactionId == null || transactionId.trim().isEmpty() || amount == null) {
            return false;
        }

        if (webhookApiKey == null || webhookApiKey.trim().isEmpty()) {
            logger.warn("SePay webhook API key is not configured");
            return false;
        }

        if (signature == null || signature.trim().isEmpty()) {
            return false;
        }

        String expectedHeader = "Apikey " + webhookApiKey.trim();
        boolean verified = expectedHeader.equals(signature.trim());
        if (!verified) {
            logger.warn("SePay webhook authorization failed for transactionId={}", transactionId);
        }
        return verified;
    }

    @Override
    public String getPaymentStatus(String transactionId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            return "PENDING";
        }

        if (statusApiUrl == null || statusApiUrl.trim().isEmpty()) {
            return "PENDING";
        }

        try {
            String responseBody = sendRequest("GET", buildUrl(statusApiUrl.trim(), transactionId), null);
            String status = extractStatus(responseBody);
            return status != null ? status : "PENDING";
        } catch (Exception ex) {
            logger.warn("Cannot resolve SePay payment status for transactionId={}: {}", transactionId, ex.getMessage());
            return "PENDING";
        }
    }

    @Override
    public boolean refund(String transactionId, BigDecimal amount) throws Exception {
        if (transactionId == null || transactionId.trim().isEmpty() || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Thông tin hoàn tiền không hợp lệ");
        }

        if (refundApiUrl == null || refundApiUrl.trim().isEmpty()) {
            logger.warn("SePay refund API is not configured for transactionId={}", transactionId);
            return false;
        }

        String payload = OBJECT_MAPPER.writeValueAsString(java.util.Map.of(
                "transactionId", transactionId,
                "amount", amount,
                "bankCode", bankCode,
                "accountNumber", accountNumber,
                "accountName", accountName));

        String responseBody = sendRequest("POST", refundApiUrl.trim(), payload);
        boolean success = isSuccessResponse(responseBody);
        if (!success) {
            logger.warn("SePay refund API returned non-success response for transactionId={}", transactionId);
        }
        return success;
    }

    private String sendRequest(String method, String url, String payload) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(requestTimeoutMs))
                .build();

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(requestTimeoutMs))
                .header("Accept", "application/json");

        if (apiKey != null && !apiKey.trim().isEmpty()) {
            builder.header("Authorization", "Apikey " + apiKey.trim());
            builder.header("X-API-KEY", apiKey.trim());
        }

        if ("POST".equalsIgnoreCase(method)) {
            builder.header("Content-Type", "application/json");
            builder.POST(HttpRequest.BodyPublishers.ofString(payload != null ? payload : "{}"));
        } else {
            builder.GET();
        }

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new BusinessException("SePay request failed with HTTP status " + response.statusCode());
        }
        return response.body();
    }

    private String buildUrl(String baseUrl, String transactionId) {
        if (baseUrl.contains("?")) {
            return baseUrl + "&transactionId=" + encode(transactionId);
        }
        return baseUrl + "?transactionId=" + encode(transactionId);
    }

    private String extractStatus(String responseBody) {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return null;
        }

        try {
            JsonNode root = OBJECT_MAPPER.readTree(responseBody);
            JsonNode statusNode = findField(root, "status");
            if (statusNode != null && statusNode.isTextual()) {
                return normalizeStatus(statusNode.asText());
            }

            JsonNode dataNode = root.get("data");
            if (dataNode != null) {
                JsonNode nestedStatus = findField(dataNode, "status");
                if (nestedStatus != null && nestedStatus.isTextual()) {
                    return normalizeStatus(nestedStatus.asText());
                }
            }
        } catch (Exception ex) {
            String plainText = responseBody.trim();
            if (!plainText.isEmpty()) {
                return normalizeStatus(plainText);
            }
        }

        return null;
    }

    private boolean isSuccessResponse(String responseBody) {
        String status = extractStatus(responseBody);
        if (status == null) {
            return false;
        }
        return "SUCCESS".equals(status) || "OK".equals(status) || "REFUNDED".equals(status);
    }

    private JsonNode findField(JsonNode node, String fieldName) {
        if (node == null) {
            return null;
        }
        JsonNode direct = node.get(fieldName);
        if (direct != null) {
            return direct;
        }
        if (node.isObject()) {
            var elements = node.elements();
            while (elements.hasNext()) {
                JsonNode found = findField(elements.next(), fieldName);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private String normalizeStatus(String status) {
        if (status == null) {
            return null;
        }
        return status.trim().toUpperCase();
    }

    private String buildPaymentCode(Long orderId) {
        return "SEP-" + orderId;
    }

    private String encode(String value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
