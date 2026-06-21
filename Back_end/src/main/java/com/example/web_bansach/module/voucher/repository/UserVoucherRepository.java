package com.example.web_bansach.module.voucher.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.web_bansach.module.voucher.entity.UserVoucher;
import com.example.web_bansach.module.voucher.entity.Voucher;

import jakarta.persistence.LockModeType;

public interface UserVoucherRepository extends JpaRepository<UserVoucher, Long> {

    boolean existsByUser_IdAndVoucher_Id(Long userId, Long voucherId);

    Optional<UserVoucher> findByUser_IdAndVoucher_CodeIgnoreCaseAndUsedFalse(Long userId, String code);

    @Query("SELECT uv.voucher FROM UserVoucher uv WHERE uv.user.id = :userId "
            + "AND uv.used = false AND uv.voucher.expiredAt >= :today ORDER BY uv.voucher.expiredAt ASC")
    Page<Voucher> findAvailableByUserId(
            @Param("userId") Long userId,
            @Param("today") LocalDate today,
            Pageable pageable);

    @Query("SELECT uv.voucher FROM UserVoucher uv WHERE uv.user.id = :userId ORDER BY uv.claimedAt DESC")
    Page<Voucher> findClaimedByUserId(@Param("userId") Long userId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT uv FROM UserVoucher uv JOIN FETCH uv.voucher v WHERE uv.user.id = :userId "
            + "AND UPPER(v.code) = :code AND uv.used = false")
    Optional<UserVoucher> findUnusedForUpdate(
            @Param("userId") Long userId,
            @Param("code") String code);
}
