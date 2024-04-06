package com.givemecon.util.gcp.ocr;

import com.google.cloud.spring.vision.CloudVisionTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TextExtractor {

    private final ResourceLoader resourceLoader;

    private final CloudVisionTemplate cloudVisionTemplate;

    public String extractTextFromImage(String imageUrl) {
        return cloudVisionTemplate.extractTextFromImage(resourceLoader.getResource(imageUrl));
    }

    public String extractTextFromImage(Resource resource) {
        return cloudVisionTemplate.extractTextFromImage(resource);
    }
}
