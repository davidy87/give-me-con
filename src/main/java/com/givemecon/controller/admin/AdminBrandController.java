package com.givemecon.controller.admin;

import com.givemecon.application.dto.BrandDto;
import com.givemecon.application.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/admin/brands")
@RestController
public class AdminBrandController {

    private final BrandService brandService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BrandDto.BrandResponse save(@Validated @ModelAttribute BrandDto.BrandSaveRequest requestDto) {
        return brandService.save(requestDto);
    }

    @PostMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BrandDto.BrandResponse update(@PathVariable Long id,
                                         @ModelAttribute BrandDto.BrandUpdateRequest requestDto) {

        return brandService.update(id, requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        brandService.delete(id);
    }
}
