package com.givemecon.domain.payment;

import com.givemecon.domain.order.*;
import com.givemecon.domain.payment.exception.InvalidPaymentException;
import com.givemecon.domain.payment.toss.PaymentConfirmation;
import com.givemecon.domain.payment.toss.TossPaymentsRestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.givemecon.domain.order.OrderDto.*;
import static com.givemecon.domain.payment.PaymentDto.*;
import static com.givemecon.domain.payment.exception.PaymentErrorCode.*;
import static org.assertj.core.api.Assertions.*;
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
    OrderSummary orderSummary;

    @BeforeEach
    void setup() {
        Mockito.when(orderService.findOrder(any(String.class), any(String.class)))
                .thenReturn(orderSummary);
    }

    @Test
    @DisplayName("정상적인 결제 승인 요청")
    void confirmPayment(@Mock PaymentConfirmation paymentConfirmation) {
        // given
        String customerName = "customer";
        Long amount = 4_000L;
        String orderId = "ORDER-ID";
        String orderName = "orderName";
        String receiptUrl = "receiptUrl";

        Payment payment = Payment.builder()
                .orderId(orderId)
                .orderName(orderName)
                .amount(amount)
                .receiptUrl(receiptUrl)
                .build();

        Mockito.when(orderSummary.getStatus()).thenReturn(OrderStatus.IN_PROGRESS);
        Mockito.when(orderSummary.getCustomerName()).thenReturn(customerName);
        Mockito.when(orderSummary.getTotalPrice()).thenReturn(amount);
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
    @DisplayName("결제 승인 요청 예외 1 - 주문의 상태가 IN_PROGRESS가 아닐 경우, 예외를 던진다.")
    void orderNotInProgress() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest("ORDER-ID", "orderName", 4_000L);
        String buyerName = "buyer";

        Mockito.when(orderSummary.getStatus()).thenReturn(OrderStatus.CONFIRMED);

        // when & then
        assertThatThrownBy(() -> paymentService.confirmPayment(paymentRequest, buyerName))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessage(ORDER_NOT_IN_PROGRESS.getMessage());
    }

    @Test
    @DisplayName("결제 승인 요청 예외 2  - 주문자와 결제 요청자가 일치하지 않을 경우, 예외를 던진다.")
    void buyerNotMatch() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest("ORDER-ID", "orderName", 4_000L);
        String validCustomerName = "customer";
        String invalidCustomerName = "invalidCustomer";

        Mockito.when(orderSummary.getStatus()).thenReturn(OrderStatus.IN_PROGRESS);
        Mockito.when(orderSummary.getCustomerName()).thenReturn(validCustomerName);

        // when & then
        assertThatThrownBy(() -> paymentService.confirmPayment(paymentRequest, invalidCustomerName))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessage(CUSTOMER_NOT_MATCH.getMessage());
    }

    @Test
    @DisplayName("결제 승인 요청 예외 3 - 결제할 금액이 주문금액과 일치하지 않을 경우, 예외를 던진다.")
    void amountNotMatch() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest("ORDER-ID", "orderName", 4_000L);
        String customerName = "customer";

        Mockito.when(orderSummary.getStatus()).thenReturn(OrderStatus.IN_PROGRESS);
        Mockito.when(orderSummary.getCustomerName()).thenReturn(customerName);
        Mockito.when(orderSummary.getTotalPrice()).thenReturn(3_000L);

        // when & then
        assertThatThrownBy(() -> paymentService.confirmPayment(paymentRequest, customerName))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessage(AMOUNT_NOT_MATCH.getMessage());
    }
}
