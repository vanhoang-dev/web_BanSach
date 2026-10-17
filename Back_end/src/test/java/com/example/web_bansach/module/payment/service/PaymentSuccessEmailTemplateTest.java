package com.example.web_bansach.module.payment.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.web_bansach.module.payment.service.email.PaymentSuccessEmailModel;
import com.example.web_bansach.module.payment.service.email.PaymentSuccessEmailTemplate;

class PaymentSuccessEmailTemplateTest {

    private final PaymentSuccessEmailTemplate template = new PaymentSuccessEmailTemplate();

    @Test
    void render_shouldIncludePaymentDataAndEscapeCustomerContent() {
        PaymentSuccessEmailModel model = new PaymentSuccessEmailModel(
                "Nguyễn <script>",
                42L,
                new BigDecimal("150000"),
                "SEP42",
                LocalDateTime.of(2026, 9, 27, 14, 30),
                List.of(new PaymentSuccessEmailModel.Item("Sách & Cuộc sống", 2, new BigDecimal("75000"))));

        String html = template.render(model);

        assertThat(template.subject(42L)).isEqualTo("Xác nhận thanh toán đơn hàng #42");
        assertThat(html)
                .contains("Thanh toán thành công", "SEP42", "27/09/2026 14:30")
                .contains("Nguyễn &lt;script&gt;")
                .contains("Sách &amp; Cuộc sống")
                .doesNotContain("Nguyễn <script>");
    }
}
