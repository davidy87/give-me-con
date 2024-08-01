package com.givemecon.common.configuration;

import com.givemecon.common.util.PortUtils;
import io.findify.s3mock.S3Mock;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Profile("test")
@Configuration
public class EmbeddedS3Config {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${spring.cloud.aws.s3.mock.port}")
    private int port;

    @Bean
    public S3Mock s3Mock() throws IOException {
        port = PortUtils.isPortRunning(port) ? PortUtils.findAvailablePort() : port;

        S3Mock s3Mock = new S3Mock.Builder()
                .withPort(port)
                .withInMemoryBackend()
                .build();

        s3Mock.start();

        return s3Mock;
    }

    @Bean
    @Primary
    @DependsOn("s3Mock")
    public S3Client s3Client() throws URISyntaxException {
        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .endpointOverride(new URI("http://127.0.0.1:" + port))
                .build();

        s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(bucketName)
                .build());

        return s3Client;
    }

    @PreDestroy
    public void shutdown() throws IOException {
        s3Mock().shutdown();
    }
}
