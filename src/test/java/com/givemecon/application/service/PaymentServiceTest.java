package com.givemecon.application.service;

import com.givemecon.application.exception.InvalidOrderException;
import com.givemecon.application.exception.InvalidPaymentException;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.order.Order;
import com.givemecon.domain.entity.payment.OrderInfo;
import com.givemecon.domain.entity.payment.Payment;
import com.givemecon.domain.entity.payment.PaymentMethod;
import com.givemecon.domain.repository.PaymentRepository;
import com.givemecon.infrastructure.tosspayments.PaymentConfirmation;
import com.givemecon.infrastructure.tosspayments.TossPaymentsRestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.givemecon.application.dto.OrderDto.OrderConfirmation;
import static com.givemecon.application.dto.PaymentDto.PaymentRequest;
import static com.givemecon.application.dto.PaymentDto.PaymentResponse;
import static com.givemecon.application.exception.errorcode.OrderErrorCode.BUYER_NOT_MATCH;
import static com.givemecon.application.exception.errorcode.OrderErrorCode.INVALID_ORDER_NUMBER;
import static com.givemecon.application.exception.errorcode.PaymentErrorCode.AMOUNT_NOT_MATCH;
import static com.givemecon.application.exception.errorcode.PaymentErrorCode.INVALID_PAYMENT_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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

    @Nested
    @DisplayName("결제 승인 테스트")
    class PaymentConfirmationTest {

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

    @Nested
    @DisplayName("결제 내역 조회 테스트")
    class PaymentHistoryTest {

        @Test
        @DisplayName("정상적인 결제 내역 조회")
        void findPaymentHistory() {
            // given
            Member buyer = Member.builder()
                    .username("buyer")
                    .build();

            Order order = new Order("ORDER-NUMBER", buyer);
            OrderInfo orderInfo = new OrderInfo(order.getOrderNumber(), "Americano T", 4_000L);

            Payment payment = Payment.builder()
                    .paymentKey("paymentKey")
                    .method(PaymentMethod.CARD)
                    .receiptUrl("receiptUrl")
                    .orderInfo(orderInfo)
                    .build();

            Mockito.when(paymentRepository.findByPaymentKey(eq(payment.getPaymentKey())))
                    .thenReturn(Optional.of(payment));

            Mockito.when(orderService.findOrder(eq(orderInfo.getOrderNumber())))
                    .thenReturn(order);

            // when
            PaymentResponse paymentRecord = paymentService.findPaymentHistory(payment.getPaymentKey(), buyer.getUsername());

            // then
            assertThat(paymentRecord.getAmount()).isEqualTo(orderInfo.getAmount());
            assertThat(paymentRecord.getOrderId()).isEqualTo(orderInfo.getOrderNumber());
            assertThat(paymentRecord.getOrderName()).isEqualTo(orderInfo.getOrderName());
            assertThat(paymentRecord.getReceiptUrl()).isEqualTo(payment.getReceiptUrl());
        }

        @Test
        @DisplayName("결제 내역 조회 예외 1 - 올바르지 않은 결제 키")
        void invalidPaymentKey() {
            // given
            String invalidPaymentKey = "INVALID-PAYMENT-KEY";

            Mockito.when(paymentRepository.findByPaymentKey(eq(invalidPaymentKey)))
                    .thenThrow(new InvalidPaymentException(INVALID_PAYMENT_KEY));

            // when & then
            assertThatThrownBy(() -> paymentService.findPaymentHistory(invalidPaymentKey, "buyer"))
                    .isInstanceOf(InvalidPaymentException.class)
                    .hasMessage(INVALID_PAYMENT_KEY.getMessage());
        }

        @Test
        @DisplayName("결제 내역 조회 예외 2 - 올바르지 않은 주문번호")
        void invalidOrderNumber() {
            // given
            String invalidOrderNumber = "INVALID-ORDER-NUMBER";
            OrderInfo orderInfo = new OrderInfo(invalidOrderNumber, "Americano T", 4_000L);
            Payment payment = Payment.builder()
                    .paymentKey("paymentKey")
                    .method(PaymentMethod.CARD)
                    .receiptUrl("receiptUrl")
                    .orderInfo(orderInfo)
                    .build();

            Mockito.when(paymentRepository.findByPaymentKey(eq(payment.getPaymentKey())))
                    .thenReturn(Optional.of(payment));

            Mockito.when(orderService.findOrder(eq(invalidOrderNumber)))
                    .thenThrow(new InvalidOrderException(INVALID_ORDER_NUMBER));

            // when & then
            assertThatThrownBy(() -> paymentService.findPaymentHistory(payment.getPaymentKey(), "buyer"))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(INVALID_ORDER_NUMBER.getMessage());
        }

        @Test
        @DisplayName("결제 내역 조회 예외 3 - 일치하지 않은 결제 내역 요청자와 구매자")
        void buyerNotMatch() {
            // given
            String invalidUsername = "notBuyer";
            Member buyer = Member.builder()
                    .username("buyer")
                    .build();

            Order order = new Order("ORDER-NUMBER", buyer);
            OrderInfo orderInfo = new OrderInfo(order.getOrderNumber(), "Americano T", 4_000L);

            Payment payment = Payment.builder()
                    .paymentKey("paymentKey")
                    .method(PaymentMethod.CARD)
                    .receiptUrl("receiptUrl")
                    .orderInfo(orderInfo)
                    .build();

            Mockito.when(paymentRepository.findByPaymentKey(eq(payment.getPaymentKey())))
                    .thenReturn(Optional.of(payment));

            Mockito.when(orderService.findOrder(eq(orderInfo.getOrderNumber())))
                    .thenReturn(order);

            Mockito.doThrow(new InvalidOrderException(BUYER_NOT_MATCH))
                    .when(orderService)
                    .verifyBuyer(buyer, invalidUsername);

            // when & then
            assertThatThrownBy(() -> paymentService.findPaymentHistory(payment.getPaymentKey(), invalidUsername))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessage(BUYER_NOT_MATCH.getMessage());
        }
    }
}
