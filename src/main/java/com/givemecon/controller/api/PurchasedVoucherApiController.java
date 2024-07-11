package com.givemecon.controller.api;

import com.givemecon.domain.purchasedvoucher.PurchasedVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.domain.purchasedvoucher.PurchasedVoucherDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/purchased-vouchers")
@RestController
public class PurchasedVoucherApiController {

    private final PurchasedVoucherService purchasedVoucherService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public List<PurchasedVoucherResponse> saveAll(Authentication authentication,
                                                  @Validated @RequestBody PurchasedVoucherRequestList requestDtoList) {

        return purchasedVoucherService.saveAll(authentication.getName(), requestDtoList.getRequests());
    }

//    @GetMapping
//    public PagedPurchasedVoucherResponse findAllByUsername(Authentication authentication,
//                                                           @PageableDefault(sort = "id") Pageable pageable) {
//
//        return purchasedVoucherService.findPageByUsername(authentication.getName(), pageable);
//    }
    @GetMapping
    public List<PurchasedVoucherResponse> findAllByUsername(Authentication authentication) {
        return purchasedVoucherService.findAllByUsername(authentication.getName());
    }

    @GetMapping("/{id}")
    public PurchasedVoucherResponse findOne(Authentication authentication, @PathVariable Long id) {
        return purchasedVoucherService.findOne(id, authentication.getName());
    }

    @PutMapping("/{id}")
    public StatusUpdateResponse setUsed(@PathVariable Long id) {
        return purchasedVoucherService.setUsed(id);
    }
}
