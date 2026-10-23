package com.example.web_bansach.module.auth.service.email;

import org.springframework.stereotype.Component;

@Component
public class AccountVerificationEmailTemplate {

    public String subject() {
        return "Mã xác nhận tài khoản Nhà Sách Tri Thức";
    }

    public String render(String customerName, String code, int ttlMinutes) {
        return """
                <!doctype html>
                <html lang="vi">
                <body style="margin:0;background:#f7f8fa;font-family:Arial,sans-serif;color:#1d2939">
                  <div style="max-width:600px;margin:0 auto;padding:32px 16px">
                    <div style="background:#ffffff;border:1px solid #e4e7ec;border-radius:10px;padding:32px">
                      <p style="margin:0 0 8px;color:#f08a1c;font-size:12px;font-weight:700;letter-spacing:.08em;text-transform:uppercase">Xác nhận email</p>
                      <h1 style="margin:0 0 16px;color:#183b4e;font-size:24px">Chào %s,</h1>
                      <p style="margin:0 0 20px;line-height:1.6">Dùng mã bên dưới để hoàn tất đăng ký tài khoản Nhà Sách Tri Thức.</p>
                      <div style="padding:18px;background:#e8f0f3;border-radius:8px;text-align:center;font-size:32px;font-weight:700;letter-spacing:8px;color:#183b4e">%s</div>
                      <p style="margin:20px 0 0;color:#667085;font-size:14px;line-height:1.6">Mã có hiệu lực trong %d phút. Không chia sẻ mã này với bất kỳ ai.</p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(escape(customerName), escape(code), ttlMinutes);
    }

    private String escape(String value) {
        if (value == null) {
            return "bạn";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
