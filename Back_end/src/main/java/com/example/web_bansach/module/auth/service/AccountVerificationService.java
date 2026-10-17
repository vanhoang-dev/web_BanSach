package com.example.web_bansach.module.auth.service;

import com.example.web_bansach.module.user.entity.Users;

public interface AccountVerificationService {

    void issueVerificationCode(Users user);

    void resendVerificationCode(String email);

    void verifyAccount(String email, String rawCode);
}
