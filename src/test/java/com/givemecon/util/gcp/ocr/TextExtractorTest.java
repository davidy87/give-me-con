package com.givemecon.util.gcp.ocr;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class TextExtractorTest {

    @Autowired
    TextExtractor textExtractor;

    @Value("${gcp.test-image.path}")
    String imagePath;

    @Test
    void extractTextFromImage() {
        log.info("imagePath = {}", imagePath);
        String textFromImage = textExtractor.extractTextFromImage(imagePath);
        log.info("textFromImage = {}", textFromImage);
        assertThat(textFromImage).isEqualTo("WAITING?\nPLEASE\nTURN OFF\nYOUR\nENGINE");
    }
}