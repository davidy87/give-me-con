package com.givemecon.application.service;

import com.givemecon.application.dto.ExtractedImageInfo;
import com.givemecon.domain.repository.brand.BrandRepository;
import com.givemecon.util.gcp.ocr.TextExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageTextExtractionService {

    private final BrandRepository brandRepository;

    private final TextExtractor textExtractor;

    public ExtractedImageInfo extractTextFromImage(MultipartFile imageFile) {
        String textFromImage = textExtractor.extractTextFromImage(imageFile.getResource());
        ExtractedImageInfo extractedImageInfo = new ExtractedImageInfo();

        textFromImage.lines()
                .filter(StringUtils::hasText)
                .peek(textLine -> log.info("text line = {}", textLine))
                .forEach(textLine -> loadExtractedText(textLine, extractedImageInfo));

        return extractedImageInfo;
    }

    private void loadExtractedText(String text, ExtractedImageInfo extractedImageInfo) {
        // 아직 브랜드 이름이 추출되지 않았을 경우, 추출된 단어가 브랜드 이름인지 확인 후 저장
        if (extractedImageInfo.getBrandName() == null) {
            brandRepository.findByName(text)
                    .ifPresent(brand -> extractedImageInfo.setBrandName(brand.getName()));

            if (extractedImageInfo.getBrandName() != null) {
                return;
            }
        }

        // 아직 바코드가 추출되지 않았을 경우, 추출된 단어가 바코드인지 확인 후 저장
        if (extractedImageInfo.getBarcode() == null) {
            boolean isBarcode = text.chars()
                    .filter(c -> !Character.isSpaceChar(c))
                    .allMatch(Character::isDigit);

            if (isBarcode) {
                extractedImageInfo.setBarcode(text);
                return;
            }
        }

        // 아직 유효기간이 추출되지 않았을 경우, 추출된 단어가 유효기간인지 확인 후 저장
        if (extractedImageInfo.getExpDate() == null) {
            extractedImageInfo.setExpDate(parseLocalDate(text));
        }
    }

    private LocalDate parseLocalDate(String text) {
        DateTimeFormatter[] dateTimeFormatters = {
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("yyyy.MM.dd"),
                DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        };

        for (DateTimeFormatter dateTimeFormatter : dateTimeFormatters) {
            try {
                return LocalDate.parse(text, dateTimeFormatter);
            } catch (DateTimeParseException e) {
                log.info("Date Parsing Failed. {}", e.getParsedString());
            }
        }

        return null;
    }
}
