package com.givemecon.web.api;

import com.givemecon.domain.brand.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.givemecon.web.dto.BrandDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/brands")
@RestController
public class BrandApiController {

    private final BrandService brandService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BrandResponse save(@ModelAttribute BrandSaveRequest requestDto) {
        return brandService.save(requestDto);
    }

    @GetMapping
    public List<BrandResponse> findAll(@RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return brandService.findAllByCategoryId(categoryId);
        }

        return brandService.findAll();
    }

    @PostMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BrandResponse update(@PathVariable Long id,
                                @RequestParam Long categoryId,
                                @RequestParam String name,
                                @RequestPart(required = false) MultipartFile icon) {

        BrandUpdateRequest requestDto = BrandUpdateRequest.builder()
                .categoryId(categoryId)
                .name(name)
                .icon(icon)
                .build();

        return brandService.update(id, requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        brandService.delete(id);
    }
}
