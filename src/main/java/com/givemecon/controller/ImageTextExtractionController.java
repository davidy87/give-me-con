package com.givemecon.controller;

import com.givemecon.domain.image.ExtractedImageInfo;
import com.givemecon.domain.image.ImageTextExtractionService;
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
