package com.givemecon.util.gcp.ocr;

import com.google.cloud.spring.vision.CloudVisionTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TextExtractor {

    private final ResourceLoader resourceLoader;

    private final CloudVisionTemplate cloudVisionTemplate;

    public String extractTextFromImage(String imageUrl) {
        return cloudVisionTemplate.extractTextFromImage(resourceLoader.getResource(imageUrl));
    }

    public List<String[]> extractTextLinesFromImage(String imageUrl) {
        String textFromImage = cloudVisionTemplate.extractTextFromImage(resourceLoader.getResource(imageUrl));
        log.info("Extracted text = {}", textFromImage);

        return textFromImage.lines()
                .filter(StringUtils::hasText)
                .map(textLine -> textLine.split(" "))
                .toList();
    }
}
