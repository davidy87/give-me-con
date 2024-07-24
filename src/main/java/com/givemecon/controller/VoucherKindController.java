package com.givemecon.controller;

import com.givemecon.application.service.VoucherKindService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.application.dto.VoucherKindDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/voucher-kinds")
@RestController
public class VoucherKindController {

    private final VoucherKindService voucherKindService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VoucherKindDetailResponse save(@Validated @ModelAttribute VoucherKindSaveRequest requestDto) {
        return voucherKindService.save(requestDto);
    }

    @GetMapping
    public List<VoucherKindResponse> findAll(Authentication authentication,
                                             @RequestParam(required = false) Long brandId) {

        if (brandId != null) {
            if (authentication != null) {
                return voucherKindService.findAllWithMinPriceByBrandId(brandId, authentication.getName());
            }

            return voucherKindService.findAllWithMinPriceByBrandId(brandId);
        }

        return voucherKindService.findAll();
    }

    @GetMapping("/{id}")
    public VoucherKindDetailResponse find(Authentication authentication, @PathVariable Long id) {
        if (authentication != null) {
            return voucherKindService.findOne(id, authentication.getName());
        }

        return voucherKindService.findOne(id);
    }

    @PostMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VoucherKindDetailResponse update(@PathVariable Long id,
                                            @ModelAttribute VoucherKindUpdateRequest requestDto) {

        return voucherKindService.update(id, requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        voucherKindService.delete(id);
    }
}
