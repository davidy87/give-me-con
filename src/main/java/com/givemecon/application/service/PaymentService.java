package com.givemecon.application.service;

import com.givemecon.application.exception.payment.InvalidPaymentException;
import com.givemecon.domain.entity.payment.Payment;
import com.givemecon.domain.repository.PaymentRepository;
import com.givemecon.infrastructure.tosspayments.PaymentConfirmation;
import com.givemecon.infrastructure.tosspayments.TossPaymentsRestClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.givemecon.application.dto.OrderDto.OrderConfirmation;
import static com.givemecon.application.dto.PaymentDto.PaymentRequest;
import static com.givemecon.application.dto.PaymentDto.PaymentResponse;
import static com.givemecon.application.exception.payment.PaymentErrorCode.AMOUNT_NOT_MATCH;

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