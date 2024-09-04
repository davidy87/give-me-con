package com.givemecon.util;

import com.givemecon.infrastructure.gcp.ocr.TextExtractor;
import com.givemecon.infrastructure.tosspayments.TossPaymentsRestClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class MockBeanConfig {

    @Bean
    @Primary
    TossPaymentsRestClient tossPaymentsRestClient() {
        return Mockito.mock(TossPaymentsRestClient.class);
    }

    @Bean
    @Primary
    TextExtractor textExtractor() {
        return Mockito.mock(TextExtractor.class);
    }
}
