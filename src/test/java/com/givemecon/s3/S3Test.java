package com.givemecon.s3;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Import(S3MockConfig.class)
@SpringBootTest
public class S3Test {

    @Autowired
    S3Mock s3Mock;

    @Autowired
    S3Template s3Template;

    @Autowired
    S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @BeforeEach
    void setup() {
        s3Mock.start();
        s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(bucketName)
                .build());
    }

    @AfterEach
    void shutdown() {
        s3Mock.stop();
    }

    @Test
    void putObject() throws IOException {
        // given
        InputStream inputStream = new ByteArrayInputStream("testImage.png".getBytes());
        String imageKey = UUID.randomUUID() + ".png";

        // when
        String imageUrl = s3Template.upload(bucketName, imageKey, inputStream)
                .getURL()
                .toString();

        // then
        String downloadUrl = s3Template.download(bucketName, imageKey)
                .getURL()
                .toString();

        assertThat(downloadUrl).isEqualTo(imageUrl);
    }

    @Test
    void deleteObject() {
        // given
        InputStream inputStream = new ByteArrayInputStream("testImage.png".getBytes());
        String imageKey = UUID.randomUUID() + ".png";
        s3Template.upload(bucketName, imageKey, inputStream);

        // when
        s3Template.deleteObject(bucketName, imageKey);

        // then
        S3Resource found = s3Template.download(bucketName, imageKey);
        assertThat(found.exists()).isFalse();
    }
}
