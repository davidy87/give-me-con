package com.givemecon.domain.payment.toss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.givemecon.domain.payment.PaymentDto.*;

@Slf4j
@Component
public class TossPaymentsRestClient {

    @Value("${toss.confirmation-url}")
    private String confirmationUrl;

    @Value("${toss.secret-key}")
    private String secretKey;

    public PaymentConfirmation requestPaymentConfirmation(PaymentRequest paymentRequest) {
        log.info("confirmationUrl = {}", confirmationUrl);
        log.info("secretKey = {}", secretKey);
        RestClient restClient = RestClient.create();

        return restClient.post()
                .uri(confirmationUrl)
                .headers(httpHeaders -> httpHeaders.setBasicAuth(getEncodedKey()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentRequest)
                .retrieve()
                .body(PaymentConfirmation.class);
    }

    private String getEncodedKey() {
        byte[] secretKeyBytes = Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return new String(secretKeyBytes);
    }
}
