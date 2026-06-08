package com.example.web_bansach.module.voucher.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.web_bansach.module.voucher.entity.Voucher;

import jakarta.persistence.LockModeType;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    // Find voucher by code
    Optional<Voucher> findByCode(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Voucher v WHERE v.code = :code")
    Optional<Voucher> findByCodeForUpdate(@Param("code") String code);

    // Find all valid vouchers (not expired and quantity > 0)
    @Query("SELECT v FROM Voucher v WHERE v.expiredAt >= :today AND v.quantity > 0 ORDER BY v.expiredAt ASC")
    Page<Voucher> findValidVouchers(@Param("today") LocalDate today, Pageable pageable);

    // Check if voucher code exists
    boolean existsByCode(String code);

    // Find all vouchers (admin)
    Page<Voucher> findAll(Pageable pageable);

    // Find expired vouchers
    @Query("SELECT v FROM Voucher v WHERE v.expiredAt < :today")
    Page<Voucher> findExpiredVouchers(@Param("today") LocalDate today, Pageable pageable);
}



