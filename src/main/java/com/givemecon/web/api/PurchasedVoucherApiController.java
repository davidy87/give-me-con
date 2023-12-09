package com.givemecon.web.api;

import com.givemecon.domain.purchasedvoucher.PurchasedVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.web.dto.PurchasedVoucherDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/purchased-vouchers")
@RestController
public class PurchasedVoucherApiController {

    private final PurchasedVoucherService purchasedVoucherService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public List<PurchasedVoucherResponse> saveAll(Authentication authentication,
                                                  @RequestBody List<PurchasedVoucherRequest> requestDto) {

        return purchasedVoucherService.saveAll(authentication.getName(), requestDto);
    }

    @GetMapping
    public List<PurchasedVoucherResponse> findAllByUsername(Authentication authentication) {
        return purchasedVoucherService.findAllByUsername(authentication.getName());
    }
}
