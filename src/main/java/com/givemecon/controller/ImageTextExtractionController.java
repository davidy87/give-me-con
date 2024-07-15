package com.givemecon.controller;

import com.givemecon.application.dto.ExtractedImageInfo;
import com.givemecon.application.service.ImageTextExtractionService;
import com.givemecon.util.validator.ValidFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/images")
@RestController
public class ImageTextExtractionController {

    private final ImageTextExtractionService imageTextExtractionService;

    @PostMapping(
            path = "/extracted-texts",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ExtractedImageInfo extractTextFromImage(@ValidFile @RequestPart MultipartFile imageFile) {
        return imageTextExtractionService.extractTextFromImage(imageFile);
    }
}
