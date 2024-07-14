package com.givemecon.domain.image.controller;

import com.givemecon.domain.image.dto.ExtractedImageInfo;
import com.givemecon.domain.image.service.ImageTextExtractionService;
import com.givemecon.util.validator.ValidFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
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
