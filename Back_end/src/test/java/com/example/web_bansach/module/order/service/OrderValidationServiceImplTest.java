package com.example.web_bansach.module.order.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.module.order.dto.request.CreateOrderRequest;
import com.example.web_bansach.module.order.repository.OrderRepository;
import com.example.web_bansach.module.order.service.impl.OrderValidationServiceImpl;

class OrderValidationServiceImplTest {

    private final OrderRepository orderRepository = org.mockito.Mockito.mock(OrderRepository.class);
    private final OrderValidationServiceImpl validationService = new OrderValidationServiceImpl(orderRepository);

    @Test
    void validateCreateOrder_shouldAcceptValidVietnamesePhoneAndZeroShippingFee() {
        CreateOrderRequest request = validRequest();

        assertThatCode(() -> validationService.validateCreateOrder(request)).doesNotThrowAnyException();
    }

    @Test
    void validateCreateOrder_shouldRejectInvalidPhoneNumbers() {
        CreateOrderRequest request = validRequest();
        request.setReceiverPhone("12345");

        assertThatThrownBy(() -> validationService.validateCreateOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("So dien thoai");
    }

    @Test
    void validateCreateOrder_shouldRejectNegativeShippingFee() {
        CreateOrderRequest request = validRequest();
        request.setShippingFee(new BigDecimal("-1"));

        assertThatThrownBy(() -> validationService.validateCreateOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Phi van chuyen");
    }

    @Test
    void validateCreateOrder_shouldRejectOversizedReceiverNameAndAddress() {
        CreateOrderRequest longNameRequest = validRequest();
        longNameRequest.setReceiverName("A".repeat(256));

        assertThatThrownBy(() -> validationService.validateCreateOrder(longNameRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("255");

        CreateOrderRequest longAddressRequest = validRequest();
        longAddressRequest.setShippingAddress("A".repeat(501));

        assertThatThrownBy(() -> validationService.validateCreateOrder(longAddressRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("500");
    }

    private CreateOrderRequest validRequest() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setReceiverName("Nguyen Van A");
        request.setReceiverPhone("0901234567");
        request.setShippingAddress("Ha Noi");
        request.setShippingMethod("STANDARD");
        request.setShippingFee(BigDecimal.ZERO);
        return request;
    }
}
