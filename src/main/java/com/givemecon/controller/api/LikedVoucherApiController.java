package com.givemecon.controller.api;

import com.givemecon.domain.likedvoucher.LikedVoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.domain.voucher.VoucherDto.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/liked-vouchers")
@RestController
public class LikedVoucherApiController {

    private final LikedVoucherService likedVoucherService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public VoucherResponse save(Authentication authentication, @RequestBody Long voucherId) {
        return likedVoucherService.save(authentication.getName(), voucherId);
    }

    @GetMapping
    public PagedVoucherResponse findAllByUsername(Authentication authentication,
                                                  @PageableDefault(sort = "id") Pageable pageable) {

        return likedVoucherService.findPageByUsername(authentication.getName(), pageable);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{voucherId}")
    public void deleteByUsernameAndVoucherId(Authentication authentication, @PathVariable Long voucherId) {
        likedVoucherService.deleteByUsernameAndVoucherId(authentication.getName(), voucherId);
    }
}
