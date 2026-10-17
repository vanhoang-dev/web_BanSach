package com.example.web_bansach.module.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.auth.entity.AccountVerificationCode;
import com.example.web_bansach.module.user.entity.Users;

import jakarta.persistence.LockModeType;

@Repository
public interface AccountVerificationCodeRepository extends JpaRepository<AccountVerificationCode, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AccountVerificationCode> findFirstByUserAndUsedFalseOrderByCreatedAtDesc(Users user);

    List<AccountVerificationCode> findAllByUserAndUsedFalse(Users user);
}
