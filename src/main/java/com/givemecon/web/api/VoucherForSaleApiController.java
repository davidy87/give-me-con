package com.givemecon.web.api;

import com.givemecon.domain.voucher.VoucherForSaleService;
import com.givemecon.web.dto.VoucherForSaleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.web.dto.VoucherDto.*;
import static com.givemecon.web.dto.VoucherForSaleDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/vouchers-for-sale")
@RestController
public class VoucherForSaleApiController {

    private final VoucherForSaleService voucherForSaleService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public VoucherForSaleResponse save(Authentication authentication,
                                       @RequestBody VoucherForSaleRequest requestDto) {

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return voucherForSaleService.save(principal.getUsername(), requestDto);
    }

    @DeleteMapping("/{id}")
    public Long delete(@PathVariable Long id) {
        return voucherForSaleService.delete(id);
    }
}
