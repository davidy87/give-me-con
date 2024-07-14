package com.givemecon.domain.payment.service;

import com.givemecon.domain.order.service.OrderService;
import com.givemecon.domain.payment.entity.Payment;
import com.givemecon.domain.payment.exception.InvalidPaymentException;
import com.givemecon.domain.payment.repository.PaymentRepository;
import com.givemecon.domain.payment.infrastructure.tosspayments.PaymentConfirmation;
import com.givemecon.domain.payment.infrastructure.tosspayments.TossPaymentsRestClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.givemecon.domain.order.dto.OrderDto.*;
import static com.givemecon.domain.payment.dto.PaymentDto.*;
import static com.givemecon.domain.payment.exception.PaymentErrorCode.*;

@RequiredArgsConstructor
@Service
@Transactional
public class PaymentService {

    private final OrderService orderService;

    private final PaymentRepository paymentRepository;

    private final TossPaymentsRestClient tossPaymentsRestClient;

    public PaymentResponse confirmPayment(PaymentRequest paymentRequest, String username) {
        // 주문 체결 처리
        confirmOrder(paymentRequest, username);

        // 토스페이먼츠로 결제 승인 API 요청
        PaymentConfirmation confirmation = tossPaymentsRestClient.requestPaymentConfirmation(paymentRequest);
        Payment payment = paymentRepository.save(confirmation.toEntity());

        return new PaymentResponse(payment);
    }

    private void confirmOrder(PaymentRequest paymentRequest, String username) {
        OrderConfirmation orderConfirmation = orderService.confirmOrder(paymentRequest.getOrderId(), username);

        if (!Objects.equals(paymentRequest.getAmount(), orderConfirmation.getAmount())) {
            throw new InvalidPaymentException(AMOUNT_NOT_MATCH);
        }
    }
}