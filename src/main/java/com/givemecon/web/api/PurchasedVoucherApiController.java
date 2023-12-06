package com.givemecon.web.api;

import com.givemecon.domain.purchasedvoucher.PurchasedVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.web.dto.PurchasedVoucherDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/purchased")
@RestController
public class PurchasedVoucherApiController {

    private final PurchasedVoucherService purchasedVoucherService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PurchasedVoucherResponse save(Authentication authentication,
                                         @RequestBody PurchasedVoucherRequest requestDto) {

        return purchasedVoucherService.save(authentication.getName(), requestDto);
    }
}
