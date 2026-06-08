package com.example.web_bansach.module.payment.service;

import java.math.BigDecimal;

import com.example.web_bansach.module.payment.dto.PaymentRequest;
import com.example.web_bansach.module.payment.dto.PaymentResponse;

/**
 * Service xử lý payment operations
 */
public interface PaymentService {

    /**
     * Khởi tạo thanh toán
     * 
     * @param request - PaymentRequest
     * @return PaymentResponse với URL thanh toán
     * @throws Exception nếu có lỗi
     */
    PaymentResponse initiatePayment(String userEmail, PaymentRequest request) throws Exception;

    /**
     * Verify callback từ payment gateway
     * 
     * @param transactionId - ID giao dịch
     * @param amount        - Số tiền
     * @param signature     - Chữ ký từ gateway
     * @return true nếu xác minh thành công
     */
    boolean verifyPaymentCallback(String transactionId, BigDecimal amount, String signature);

    /**
     * Lấy trạng thái thanh toán
     * 
     * @param paymentId - ID payment
     * @return PaymentResponse chứa thông tin thanh toán
     */
    PaymentResponse getPaymentStatus(Long paymentId);

    /**
     * Hoàn tiền
     * 
     * @param paymentId - ID payment cần hoàn tiền
     * @param amount    - Số tiền hoàn (có thể partial refund)
     * @return true nếu hoàn tiền thành công
     * @throws Exception nếu có lỗi
     */
    boolean refundPayment(Long paymentId, BigDecimal amount) throws Exception;

    /**
     * Update trạng thái thanh toán (được gọi từ callback)
     * 
     * @param transactionId - Mã giao dịch từ SePay
     * @param status        - Trạng thái: SUCCESS, FAILED
     * @param signature     - Header xác thực callback từ gateway (lưu để verify)
     */
    void updatePaymentStatus(String transactionId, String status, String signature);
}
