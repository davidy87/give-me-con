package com.givemecon.controller;

import com.givemecon.application.service.VoucherKindService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.application.dto.VoucherKindDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/voucher-kinds")
@RestController
public class VoucherKindController {

    private final VoucherKindService voucherKindService;

    @GetMapping
    public List<VoucherKindResponse> findAll(Authentication authentication,
                                             @RequestParam(required = false) Long brandId) {

        if (brandId == null) {
            return voucherKindService.findAll();
        }

        if (authentication == null) {
            return voucherKindService.findAllWithMinPriceByBrandId(brandId);
        }

        return voucherKindService.findAllWithMinPriceByBrandId(brandId, authentication.getName());
    }

    @GetMapping("/{id}")
    public VoucherKindDetailResponse find(Authentication authentication, @PathVariable Long id) {
        if (authentication != null) {
            return voucherKindService.findOne(id, authentication.getName());
        }

        return voucherKindService.findOne(id);
    }
}
