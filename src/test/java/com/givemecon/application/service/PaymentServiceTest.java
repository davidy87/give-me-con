package com.givemecon.application.service;

import com.givemecon.application.exception.payment.InvalidPaymentException;
import com.givemecon.domain.entity.payment.OrderInfo;
import com.givemecon.domain.entity.payment.Payment;
import com.givemecon.domain.repository.PaymentRepository;
import com.givemecon.infrastructure.tosspayments.PaymentConfirmation;
import com.givemecon.infrastructure.tosspayments.TossPaymentsRestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.givemecon.application.dto.OrderDto.OrderConfirmation;
import static com.givemecon.application.dto.PaymentDto.PaymentRequest;
import static com.givemecon.application.dto.PaymentDto.PaymentResponse;
import static com.givemecon.application.exception.payment.PaymentErrorCode.AMOUNT_NOT_MATCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    OrderService orderService;

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    TossPaymentsRestClient tossPaymentsRestClient;

    @InjectMocks
    PaymentService paymentService;

    @Mock
    OrderConfirmation orderConfirmation;

    @BeforeEach
    void setup() {
        Mockito.when(orderService.confirmOrder(any(String.class), any(String.class)))
                .thenReturn(orderConfirmation);
    }

    @Test
    @DisplayName("정상적인 결제 승인 요청")
    void confirmPayment(@Mock PaymentConfirmation paymentConfirmation) {
        // given
        String customerName = "customer";
        String orderId = "ORDER-ID";
        String orderName = "orderName";
        Long amount = 4_000L;
        String receiptUrl = "receiptUrl";
        OrderInfo orderInfo = new OrderInfo(orderId, orderName, amount);

        Payment payment = Payment.builder()
                .receiptUrl(receiptUrl)
                .orderInfo(orderInfo)
                .build();

        Mockito.when(orderConfirmation.getAmount()).thenReturn(amount);
        Mockito.when(tossPaymentsRestClient.requestPaymentConfirmation(any(PaymentRequest.class)))
                .thenReturn(paymentConfirmation);

        Mockito.when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        Mockito.when(paymentConfirmation.toEntity()).thenReturn(payment);

        PaymentRequest paymentRequest = new PaymentRequest(orderId, orderName, amount);

        // when
        PaymentResponse paymentResponse = paymentService.confirmPayment(paymentRequest, customerName);

        // then
        assertThat(paymentResponse.getAmount()).isEqualTo(amount);
        assertThat(paymentResponse.getOrderId()).isEqualTo(orderId);
        assertThat(paymentResponse.getOrderName()).isEqualTo(orderName);
        assertThat(paymentResponse.getReceiptUrl()).isEqualTo(receiptUrl);
    }


    @Test
    @DisplayName("결제 승인 요청 예외 - 결제금액이 주문금액과 일치하지 않을 경우, 예외를 던진다.")
    void amountNotMatch() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest("ORDER-ID", "orderName", 4_000L);
        String customerName = "customer";

        // when
        Mockito.when(orderConfirmation.getAmount()).thenReturn(paymentRequest.getAmount() + 1_000L);

        // then
        assertThatThrownBy(() -> paymentService.confirmPayment(paymentRequest, customerName))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessage(AMOUNT_NOT_MATCH.getMessage());
    }
}
