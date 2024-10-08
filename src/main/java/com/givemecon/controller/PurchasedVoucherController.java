package com.givemecon.controller;

import com.givemecon.application.service.PurchasedVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.application.dto.PurchasedVoucherDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/purchased-vouchers")
@RestController
public class PurchasedVoucherController {

    private final PurchasedVoucherService purchasedVoucherService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public List<PurchasedVoucherResponse> saveAll(Authentication authentication,
                                                  @Validated @RequestBody PurchasedVoucherRequestList requestDtoList) {

        return purchasedVoucherService.saveAll(authentication.getName(), requestDtoList.getRequests());
    }

    @GetMapping
    public List<PurchasedVoucherResponse> findAllByUsername(Authentication authentication) {
        return purchasedVoucherService.findAllByUsername(authentication.getName());
    }

    @GetMapping("/{id}")
    public PurchasedVoucherResponse findOne(Authentication authentication, @PathVariable Long id) {
        return purchasedVoucherService.findOne(id, authentication.getName());
    }

    @PutMapping("/{id}")
    public StatusUpdateResponse setUsedOnUsable(@PathVariable Long id) {
        return purchasedVoucherService.setUsedOnUsable(id);
    }
}
