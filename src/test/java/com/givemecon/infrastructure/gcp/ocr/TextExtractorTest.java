package com.givemecon.infrastructure.gcp.ocr;

import com.google.cloud.spring.vision.CloudVisionTemplate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TextExtractorTest {

    @Mock
    ResourceLoader resourceLoader;

    @Mock
    CloudVisionTemplate cloudVisionTemplate;

    @InjectMocks
    TextExtractor textExtractor;

    @Value("${gcp.test-image.url}")
    String imagePath;

    Resource resource;

    @Test
    void extractTextFromImage() {
        // given
        log.info("imagePath = {}", imagePath);
        String textFromImage = "WAITING?\nPLEASE\nTURN OFF\nYOUR\nENGINE";

        Mockito.when(resourceLoader.getResource(eq(imagePath)))
                .thenReturn(resource);

        Mockito.when(cloudVisionTemplate.extractTextFromImage(eq(resource)))
                .thenReturn(textFromImage);

        // when
        String extractedText = textExtractor.extractTextFromImage(imagePath);

        // then
        assertThat(extractedText).isEqualTo(textFromImage);
    }
}