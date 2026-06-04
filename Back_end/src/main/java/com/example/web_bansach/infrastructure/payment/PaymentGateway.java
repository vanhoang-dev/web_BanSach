package com.example.web_bansach.infrastructure.payment;

import java.math.BigDecimal;

/**
 * Interface định nghĩa contract cho các payment gateway
 * Sử dụng Strategy pattern để support nhiều payment method
 */
public interface PaymentGateway {

    /**
     * Khởi tạo thanh toán
     * 
     * @param orderId     - ID đơn hàng
     * @param amount      - Số tiền cần thanh toán
     * @param returnUrl   - URL trả về sau khi thanh toán
     * @param description - Mô tả giao dịch
     * @return URL để redirect người dùng đến trang thanh toán
     * @throws Exception nếu có lỗi
     */
    String initiatePayment(Long orderId, BigDecimal amount, String returnUrl, String description) throws Exception;

    /**
     * Verify callback từ payment gateway
     * 
     * @param transactionId - ID giao dịch
     * @param amount        - Số tiền
     * @param signature     - Chữ ký từ gateway để xác minh
     * @return true nếu xác minh thành công
     */
    boolean verifyPayment(String transactionId, BigDecimal amount, String signature);

    /**
     * Lấy trạng thái thanh toán
     * 
     * @param transactionId - ID giao dịch
     * @return Trạng thái: SUCCESS, FAILED, PENDING
     */
    String getPaymentStatus(String transactionId);

    /**
     * Hoàn tiền
     * 
     * @param transactionId - ID giao dịch cần hoàn tiền
     * @param amount        - Số tiền hoàn
     * @return true nếu hoàn tiền thành công
     * @throws Exception nếu có lỗi
     */
    boolean refund(String transactionId, BigDecimal amount) throws Exception;
}
