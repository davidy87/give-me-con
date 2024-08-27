package com.givemecon.infrastructure.tosspayments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.application.exception.InvalidPaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.givemecon.application.dto.PaymentDto.PaymentRequest;

@Slf4j
@RequiredArgsConstructor
@Component
public class TossPaymentsRestClient {

    @Value("${toss.confirmation-url}")
    private String confirmationUrl;

    @Value("${toss.secret-key}")
    private String secretKey;

    private final RestClient.Builder restClientBuilder;

    private final ObjectMapper objectMapper;

    public PaymentConfirmation requestPaymentConfirmation(PaymentRequest paymentRequest) {
        log.info("confirmationUrl = {}", confirmationUrl);
        log.info("secretKey = {}", secretKey);
        RestClient restClient = restClientBuilder.build();

        return restClient.post()
                .uri(confirmationUrl)
                .headers(httpHeaders -> httpHeaders.setBasicAuth(encodeSecretKey()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new InvalidPaymentException(getErrorCode(response));
                })
                .body(PaymentConfirmation.class);
    }

    private TossPaymentsErrorCode getErrorCode(ClientHttpResponse response) throws IOException {
        int status = response.getStatusCode().value();
        TossPaymentsErrorCode errorCode = objectMapper.readValue(response.getBody(), TossPaymentsErrorCode.class);
        errorCode.setStatus(status);
        log.info("payment confirmation failed, error = {}", errorCode);

        return errorCode;
    }

    private String encodeSecretKey() {
        byte[] secretKeyBytes = Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return new String(secretKeyBytes);
    }
}
