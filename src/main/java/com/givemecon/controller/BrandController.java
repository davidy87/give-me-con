package com.givemecon.controller;

import com.givemecon.application.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.application.dto.BrandDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/brands")
@RestController
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public List<BrandResponse> findAll(@RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return brandService.findAllByCategory(categoryId);
        }

        return brandService.findAll();
    }
}
