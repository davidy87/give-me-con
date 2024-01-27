package com.givemecon.web.api;

import com.givemecon.domain.purchasedvoucher.PurchasedVoucherService;
import jakarta.validation.Valid;
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
                                                  @Valid @RequestBody PurchasedVoucherRequestList requestDto) {

        return purchasedVoucherService.saveAll(authentication.getName(), requestDto.getRequestList());
    }

    @GetMapping
    public List<PurchasedVoucherResponse> findAllByUsername(Authentication authentication) {
        return purchasedVoucherService.findAllByUsername(authentication.getName());
    }

    @GetMapping("/{id}")
    public PurchasedVoucherResponse find(Authentication authentication, @PathVariable Long id) {
        return purchasedVoucherService.find(id, authentication.getName());
    }

    @PutMapping("/{id}")
    public PurchasedVoucherResponse updateValidity(@PathVariable Long id) {
        return purchasedVoucherService.updateValidity(id);
    }
}
