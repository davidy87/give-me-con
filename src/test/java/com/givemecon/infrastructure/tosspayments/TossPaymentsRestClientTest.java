package com.givemecon.infrastructure.tosspayments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.application.exception.payment.InvalidPaymentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.Map;

import static com.givemecon.application.dto.PaymentDto.PaymentRequest;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(TossPaymentsRestClient.class)
class TossPaymentsRestClientTest {

    @Autowired
    TossPaymentsRestClient client;

    @Autowired
    MockRestServiceServer server;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${toss.confirmation-url}")
    String confirmationUrl;

    @Test
    void paymentConfirmationSuccessTest() throws JsonProcessingException {
        // given
        PaymentRequest paymentRequest = new PaymentRequest("PAYMENT-KEY", "ORDER-ID", 4_000L);
        PaymentConfirmation expectedResponse = new PaymentConfirmation(
                paymentRequest.getPaymentKey(),
                "CONFIRMED",
                paymentRequest.getOrderId(),
                "ORDER_NAME",
                paymentRequest.getAmount(),
                Map.of("url", "https://receipt-url.com")
        );

        String successResponse = objectMapper.writeValueAsString(expectedResponse);

        server.expect(requestTo(confirmationUrl))
                .andRespond(withSuccess(successResponse, MediaType.APPLICATION_JSON));

        // when
        PaymentConfirmation actualResponse = client.requestPaymentConfirmation(paymentRequest);

        // then
        assertThat(actualResponse.getPaymentKey()).isEqualTo(expectedResponse.getPaymentKey());
        assertThat(actualResponse.getStatus()).isEqualTo(expectedResponse.getStatus());
        assertThat(actualResponse.getOrderId()).isEqualTo(expectedResponse.getOrderId());
        assertThat(actualResponse.getOrderName()).isEqualTo(expectedResponse.getOrderName());
        assertThat(actualResponse.getTotalAmount()).isEqualTo(expectedResponse.getTotalAmount());
        assertThat(actualResponse.getReceipt()).isEqualTo(expectedResponse.getReceipt());
    }

    @Test
    @DisplayName("토스페이먼츠 결제 승인 API 요청 예외 - 결제 정보가 올바르지 않기 때문에 실패해야 한다.")
    void invalidPaymentRequest() throws JsonProcessingException {
        // given
        PaymentRequest paymentRequest =
                new PaymentRequest("INVALID-PAYMENT-KEY", "ORDER-ID", 4_000L);

        TossPaymentsErrorCode errorCode =
                new TossPaymentsErrorCode(400, "INVALID_PAYMENT", "존재하지 않는 결제 정보 입니다.");

        String responseBody = objectMapper.writeValueAsString(errorCode);

        server.expect(requestTo(confirmationUrl))
                .andRespond(withBadRequest().body(responseBody).contentType(MediaType.APPLICATION_JSON));

        // when & then
        assertThatThrownBy(() -> client.requestPaymentConfirmation(paymentRequest))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessage(errorCode.getMessage());
    }
}