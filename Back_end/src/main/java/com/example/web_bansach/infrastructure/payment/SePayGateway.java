package com.example.web_bansach.infrastructure.payment;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.web_bansach.common.exception.BusinessException;

@Component
// Hiện thực tạo mã QR và xác minh giao dịch thông qua SePay.
public class SePayGateway implements PaymentGateway {
    private static final Logger logger = LoggerFactory.getLogger(SePayGateway.class);

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

    private String buildPaymentCode(Long orderId) {
        return "SEP" + orderId;
    }

    private String encode(String value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
