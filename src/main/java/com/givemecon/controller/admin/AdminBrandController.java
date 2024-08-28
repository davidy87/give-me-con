package com.givemecon.controller.admin;

import com.givemecon.application.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.application.dto.BrandDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/admin/brands")
@RestController
public class AdminBrandController {

    private final BrandService brandService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BrandResponse save(@Validated @ModelAttribute BrandSaveRequest requestDto) {
        return brandService.save(requestDto);
    }

    @PostMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BrandResponse update(@PathVariable Long id,
                                @ModelAttribute BrandUpdateRequest requestDto) {

        return brandService.update(id, requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        brandService.delete(id);
    }
}
