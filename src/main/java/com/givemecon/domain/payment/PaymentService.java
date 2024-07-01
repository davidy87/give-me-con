package com.givemecon.domain.payment;

import com.givemecon.domain.order.*;
import com.givemecon.domain.payment.exception.InvalidPaymentException;
import com.givemecon.domain.payment.toss.PaymentConfirmation;
import com.givemecon.domain.payment.toss.TossPaymentsRestClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.givemecon.domain.order.OrderDto.*;
import static com.givemecon.domain.order.OrderStatus.*;
import static com.givemecon.domain.payment.PaymentDto.*;
import static com.givemecon.domain.payment.exception.PaymentErrorCode.*;

@RequiredArgsConstructor
@Service
@Transactional
public class PaymentService {

    private final OrderService orderService;

    private final PaymentRepository paymentRepository;

    private final TossPaymentsRestClient tossPaymentsRestClient;

    public PaymentResponse confirmPayment(PaymentRequest paymentRequest, String username) {
        verifyPaymentRequest(paymentRequest, username);

        // 토스페이먼츠로 결제 승인 API 요청
        PaymentConfirmation confirmation = tossPaymentsRestClient.requestPaymentConfirmation(paymentRequest);
        Payment payment = paymentRepository.save(confirmation.toEntity());

        // 주문 완료
        orderService.confirmOrder(paymentRequest.getOrderId(), username);

        return new PaymentResponse(payment);
    }

    private void verifyPaymentRequest(PaymentRequest paymentRequest, String username) {
        OrderSummary orderSummary = orderService.findOrder(paymentRequest.getOrderId(), username);

        if (orderSummary.getStatus() != IN_PROGRESS) {
            throw new InvalidPaymentException(ORDER_NOT_IN_PROGRESS);
        }

        if (!Objects.equals(username, orderSummary.getCustomerName())) {
            throw new InvalidPaymentException(CUSTOMER_NOT_MATCH);
        }

        if (!Objects.equals(paymentRequest.getAmount(), orderSummary.getTotalPrice())) {
            throw new InvalidPaymentException(AMOUNT_NOT_MATCH);
        }
    }
}
