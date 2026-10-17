package com.example.web_bansach.module.auth.service.email;

import org.springframework.stereotype.Component;

@Component
public class AccountWelcomeEmailTemplate {

    public String subject() {
        return "Chào mừng bạn đến với Nhà Sách Tri Thức";
    }

    public String render(String customerName) {
        return """
                <!doctype html>
                <html lang="vi">
                <body style="margin:0;background:#f7f8fa;font-family:Arial,sans-serif;color:#1d2939">
                  <div style="max-width:600px;margin:0 auto;padding:32px 16px">
                    <div style="background:#ffffff;border:1px solid #e4e7ec;border-radius:10px;padding:32px">
                      <p style="margin:0 0 8px;color:#16a34a;font-size:12px;font-weight:700;letter-spacing:.08em;text-transform:uppercase">Tài khoản đã xác nhận</p>
                      <h1 style="margin:0 0 16px;color:#183b4e;font-size:24px">Cảm ơn %s đã tạo tài khoản!</h1>
                      <p style="margin:0;line-height:1.7">Tài khoản Nhà Sách Tri Thức của bạn đã sẵn sàng. Bạn có thể đăng nhập để mua sách, lưu sách yêu thích và theo dõi đơn hàng.</p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(escape(customerName));
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
