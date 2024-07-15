package com.givemecon.infrastructure.tosspayments;

import com.givemecon.application.exception.payment.InvalidPaymentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static com.givemecon.application.dto.PaymentDto.PaymentRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ContextConfiguration(classes = TossPaymentsRestClient.class)
@SpringBootTest
class TossPaymentsRestClientTest {

    @Autowired
    TossPaymentsRestClient tossPaymentsRestClient;

    @Test
    @DisplayName("토스페이먼츠 결제 승인 API 요청 예외 - 결제 정보가 올바르지 않기 때문에 실패해야 한다.")
    void restClientTest() {
        // given
        PaymentRequest paymentRequest = new PaymentRequest("PAYMENT-KEY", "ORDER-ID", 4_000L);

        // when & then
        assertThatThrownBy(() -> tossPaymentsRestClient.requestPaymentConfirmation(paymentRequest))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessage("존재하지 않는 결제 정보 입니다.");
    }
}