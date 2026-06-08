package com.example.web_bansach.module.voucher.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.module.voucher.dto.request.CreateVoucherRequest;
import com.example.web_bansach.module.voucher.entity.Voucher;
import com.example.web_bansach.module.voucher.mapper.VoucherMapper;
import com.example.web_bansach.module.voucher.repository.VoucherRepository;

@ExtendWith(MockitoExtension.class)
class VoucherServiceTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private VoucherMapper voucherMapper;

    @InjectMocks
    private VoucherService voucherService;

    @Test
    void createVoucher_shouldNormalizeCodeBeforeSaving() {
        CreateVoucherRequest request = new CreateVoucherRequest();
        request.setCode(" welcome10 ");
        request.setDiscountPercent(10);
        request.setMaxDiscount(new BigDecimal("50000"));
        request.setQuantity(5);
        request.setExpiredAt(LocalDate.now().plusDays(10));

        when(voucherRepository.existsByCode("WELCOME10")).thenReturn(false);
        when(voucherRepository.save(any(Voucher.class))).thenAnswer(invocation -> invocation.getArgument(0));

        voucherService.createVoucher(request);

        ArgumentCaptor<Voucher> captor = ArgumentCaptor.forClass(Voucher.class);
        verify(voucherRepository).save(captor.capture());
        assertThat(captor.getValue().getCode()).isEqualTo("WELCOME10");
    }

    @Test
    void useVoucher_shouldDecreaseQuantityWithLockedVoucher() {
        Voucher voucher = new Voucher();
        voucher.setCode("WELCOME10");
        voucher.setQuantity(2);
        voucher.setExpiredAt(LocalDate.now().plusDays(1));

        when(voucherRepository.findByCodeForUpdate("WELCOME10")).thenReturn(Optional.of(voucher));

        voucherService.useVoucher(" welcome10 ");

        assertThat(voucher.getQuantity()).isEqualTo(1);
        verify(voucherRepository).save(voucher);
    }

    @Test
    void useVoucher_shouldRejectExpiredVoucher() {
        Voucher voucher = new Voucher();
        voucher.setCode("OLD");
        voucher.setQuantity(2);
        voucher.setExpiredAt(LocalDate.now().minusDays(1));

        when(voucherRepository.findByCodeForUpdate("OLD")).thenReturn(Optional.of(voucher));

        assertThatThrownBy(() -> voucherService.useVoucher("old"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("hết hạn");
    }
}
