package com.example.web_bansach.module.payment.service;

import com.example.web_bansach.module.payment.entity.Payment;

/** Notification port invoked when a payment changes to SUCCESS. */
public interface PaymentNotificationService {

    void notifyPaymentSucceededAfterCommit(Payment payment);
}
