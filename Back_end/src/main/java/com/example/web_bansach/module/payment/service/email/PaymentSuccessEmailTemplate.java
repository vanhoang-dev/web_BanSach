package com.example.web_bansach.module.payment.service.email;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class PaymentSuccessEmailTemplate {
    private static final Locale VIETNAM_LOCALE = Locale.forLanguageTag("vi-VN");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public String subject(Long orderId) {
        return "Xác nhận thanh toán đơn hàng #" + orderId;
    }

    public String render(PaymentSuccessEmailModel model) {
        StringBuilder body = new StringBuilder();
        body.append("""
                <!doctype html>
                <html lang="vi">
                <body style="margin:0;background:#f7f8fa;font-family:Arial,sans-serif;color:#1d2939;line-height:1.6">
                  <div style="max-width:640px;margin:0 auto;padding:32px 20px">
                    <div style="background:#ffffff;border:1px solid #e4e7ec;border-radius:10px;padding:32px">
                      <p style="margin:0 0 8px;color:#e58f29;font-size:12px;font-weight:700;text-transform:uppercase">Nhà Sách Tri Thức</p>
                      <h1 style="margin:0 0 20px;color:#183b4e;font-size:26px;line-height:1.3">Thanh toán thành công</h1>
                """)
                .append("<p>Xin chào ").append(escapeHtml(displayName(model.customerName()))).append(",</p>")
                .append("<p>Đơn hàng <strong>#").append(model.orderId())
                .append("</strong> của bạn đã được thanh toán thành công.</p>")
                .append("<table style=\"border-collapse:collapse;width:100%;margin:24px 0\">")
                .append(row("Mã giao dịch", model.transactionCode()))
                .append(row("Số tiền", formatCurrency(model.amount())))
                .append(row("Thời gian thanh toán", model.paidAt() == null ? "" : model.paidAt().format(DATE_TIME_FORMATTER)))
                .append("</table>");

        if (!model.items().isEmpty()) {
            body.append("<h2 style=\"color:#183b4e;font-size:18px\">Sản phẩm trong đơn hàng</h2>")
                    .append("<table style=\"border-collapse:collapse;width:100%\">")
                    .append("<tr><th style=\"border:1px solid #e4e7ec;padding:10px;text-align:left\">Sách</th>")
                    .append("<th style=\"border:1px solid #e4e7ec;padding:10px;text-align:right\">SL</th>")
                    .append("<th style=\"border:1px solid #e4e7ec;padding:10px;text-align:right\">Đơn giá</th></tr>");
            for (PaymentSuccessEmailModel.Item item : model.items()) {
                body.append("<tr><td style=\"border:1px solid #e4e7ec;padding:10px\">")
                        .append(escapeHtml(item.title()))
                        .append("</td><td style=\"border:1px solid #e4e7ec;padding:10px;text-align:right\">")
                        .append(item.quantity() == null ? 0 : item.quantity())
                        .append("</td><td style=\"border:1px solid #e4e7ec;padding:10px;text-align:right\">")
                        .append(escapeHtml(formatCurrency(item.unitPrice())))
                        .append("</td></tr>");
            }
            body.append("</table>");
        }

        body.append("""
                      <p style="margin:24px 0 0">Cảm ơn bạn đã mua hàng tại Nhà Sách Tri Thức.</p>
                    </div>
                  </div>
                </body>
                </html>
                """);
        return body.toString();
    }

    private String row(String label, String value) {
        return "<tr><td style=\"border:1px solid #e4e7ec;padding:10px;font-weight:bold\">"
                + escapeHtml(label)
                + "</td><td style=\"border:1px solid #e4e7ec;padding:10px\">"
                + escapeHtml(value)
                + "</td></tr>";
    }

    private String displayName(String value) {
        return value == null || value.isBlank() ? "Quý khách" : value;
    }

    private String formatCurrency(BigDecimal amount) {
        return amount == null ? "" : NumberFormat.getCurrencyInstance(VIETNAM_LOCALE).format(amount);
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
