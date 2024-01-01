package com.givemecon.web.api;

import com.givemecon.domain.brand.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.web.dto.BrandDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/brands")
@RestController
public class BrandApiController {

    private final BrandService brandService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BrandResponse save(@RequestBody BrandSaveRequest requestDto) {
        return brandService.save(requestDto);
    }

    @GetMapping
    public List<BrandResponse> findAll(@RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return brandService.findAllByCategoryId(categoryId);
        }

        return brandService.findAll();
    }

    @PutMapping("/{id}")
    public BrandResponse update(@PathVariable Long id,
                                @RequestBody BrandUpdateRequest requestDto) {

        return brandService.update(id, requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        brandService.delete(id);
    }
}
