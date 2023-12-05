package com.givemecon.web.api;

import com.givemecon.domain.voucherpurchased.VoucherPurchasedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.web.dto.VoucherPurchasedDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/purchased")
@RestController
public class VoucherPurchasedApiController {

    private final VoucherPurchasedService voucherPurchasedService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public VoucherPurchasedResponse save(Authentication authentication,
                                         @RequestBody VoucherPurchasedRequest requestDto) {

        return voucherPurchasedService.save(authentication.getName(), requestDto);
    }
}
