package com.example.web_bansach.module.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.account-verification")
public class AccountVerificationProperties {

    private int codeTtlMinutes = 10;
    private int resendCooldownSeconds = 60;
    private int maxAttempts = 5;

    public int getCodeTtlMinutes() {
        return codeTtlMinutes;
    }

    public void setCodeTtlMinutes(int codeTtlMinutes) {
        this.codeTtlMinutes = codeTtlMinutes;
    }

    public int getResendCooldownSeconds() {
        return resendCooldownSeconds;
    }

    public void setResendCooldownSeconds(int resendCooldownSeconds) {
        this.resendCooldownSeconds = resendCooldownSeconds;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}
