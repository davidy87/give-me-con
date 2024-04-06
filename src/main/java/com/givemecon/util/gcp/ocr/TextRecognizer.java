package com.givemecon.util.gcp.ocr;

import com.google.cloud.spring.vision.CloudVisionTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TextRecognizer {

    private final ResourceLoader resourceLoader;

    private final CloudVisionTemplate cloudVisionTemplate;

    public String getTextFromImage(String imageUrl) {
        return cloudVisionTemplate.extractTextFromImage(resourceLoader.getResource(imageUrl));
    }
}
