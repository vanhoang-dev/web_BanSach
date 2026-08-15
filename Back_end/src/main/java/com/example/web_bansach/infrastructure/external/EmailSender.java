package com.example.web_bansach.infrastructure.external;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.web_bansach.common.exception.BusinessException;

import jakarta.mail.internet.MimeMessage;

@Service
// Gửi email HTML, hiện được dùng cho luồng khôi phục mật khẩu.
public class EmailSender {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    public EmailSender(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    public void sendMessage(String from, String to, String subject, String text) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new BusinessException("Chưa cấu hình mail server nên không thể gửi email");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new BusinessException("Lỗi gửi email: " + e.getMessage());
        }
    }
}
