package com.givemecon.util.gcp.ocr;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

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
        assertThat(textFromImage).isEqualTo("Unit testing with\nmockito");
    }

    @Test
    void extractTextLinesFromImage() {
        List<String[]> textLines = textExtractor.extractTextLinesFromImage(imagePath);
        for (String[] textLine : textLines) {
            log.info("words per line = {}", Arrays.toString(textLine));
        }

        assertThat(textLines).hasSize(2);
    }
}