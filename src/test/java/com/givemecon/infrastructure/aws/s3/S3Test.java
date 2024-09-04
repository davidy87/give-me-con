package com.givemecon.infrastructure.aws.s3;

import com.givemecon.IntegrationTestEnvironment;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class S3Test extends IntegrationTestEnvironment {

    @Autowired
    S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    String bucketName;

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
