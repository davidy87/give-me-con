package com.givemecon.domain;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
@Component
public class AwsS3Service {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final S3Template s3Template;

    public String upload(String imageKey, InputStream imageInputStream) {
        S3Resource imageResource = s3Template.upload(bucketName, imageKey, imageInputStream);

        try {
            return imageResource.getURL().toString();
        } catch (IOException e) {
            throw new RuntimeException(); // TODO: 예외 처리
        }
    }

    public String download(String imageKey) {
        try {
            return s3Template.download(bucketName, imageKey).getURL().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String imageKey) {
        S3Resource imageResource = s3Template.download(bucketName, imageKey);

        if (imageResource.exists()) {
            s3Template.deleteObject(bucketName, imageKey);
        }
    }
}
