package com.givemecon.controller.api;

import com.givemecon.domain.brand.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.domain.brand.BrandDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/brands")
@RestController
public class BrandApiController {

    private final BrandService brandService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BrandResponse save(@Validated @ModelAttribute BrandSaveRequest requestDto) {
        return brandService.save(requestDto);
    }

    @GetMapping
    public PagedBrandResponse findAll(@RequestParam(required = false) Long categoryId,
                                      @PageableDefault(sort = "id") Pageable pageable) {

        if (categoryId != null) {
            return brandService.findPageByCategoryId(categoryId, pageable);
        }

        return brandService.findPage(pageable);
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
