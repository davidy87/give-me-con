package com.givemecon.domain;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class AwsS3Service {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final S3Template s3Template;

    public String upload(String imageKey, MultipartFile imageFile) {
        try {
            S3Resource imageResource = s3Template.upload(bucketName, imageKey, imageFile.getInputStream());
            return imageResource.getURL().toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
