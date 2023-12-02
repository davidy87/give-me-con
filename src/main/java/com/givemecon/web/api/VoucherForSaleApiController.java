package com.givemecon.web.api;

import com.givemecon.domain.voucher.VoucherForSaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.web.dto.VoucherDto.*;
import static com.givemecon.web.dto.VoucherForSaleDto.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/vouchers-for-sale")
@RestController
public class VoucherForSaleApiController {

    private final VoucherForSaleService voucherForSaleService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public VoucherForSaleResponse save(@AuthenticationPrincipal OAuth2User principal,
                                       @RequestBody VoucherForSaleRequest requestDto) {

        log.info("username = {}", principal.getName());
        return voucherForSaleService.save(principal.getName(), requestDto);
    }

    @DeleteMapping("/{id}")
    public Long delete(@PathVariable Long id) {
        return voucherForSaleService.delete(id);
    }
}
