package com.givemecon.web.api;

import com.givemecon.domain.brand.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.web.dto.BrandDto.*;
import static com.givemecon.web.dto.VoucherDto.*;

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
    public List<BrandResponse> findAll() {
        return brandService.findAll();
    }

    @GetMapping("/{id}")
    public BrandResponse find(@PathVariable Long id) {
        return brandService.find(id);
    }

    @GetMapping("/{brandName}/vouchers")
    public List<VoucherResponse> findAllVouchersByBrandName(@PathVariable String brandName) {
        return brandService.findAllVouchersByBrandName(brandName);
    }

    @PutMapping("/{id}")
    public BrandResponse update(@PathVariable Long id,
                                @RequestBody BrandUpdateRequest requestDto) {

        return brandService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    public Long delete(@PathVariable Long id) {
        return brandService.delete(id);
    }
}
