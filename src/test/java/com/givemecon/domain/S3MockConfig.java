package com.givemecon.domain;

import io.findify.s3mock.S3Mock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@TestConfiguration
public class S3MockConfig {

    @Bean
    public S3Mock s3Mock() {
        return new S3Mock.Builder().build();
    }

    @Bean
    @Primary
    @DependsOn("s3Mock")
    public S3Client s3Client() throws URISyntaxException {
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .endpointOverride(new URI("http://127.0.0.1:8001"))
                .build();
    }
}
