package com.givemecon.controller;

import com.givemecon.domain.likedvoucher.LikedVoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.domain.voucherkind.VoucherKindDto.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/liked-vouchers")
@RestController
public class LikedVoucherController {

    private final LikedVoucherService likedVoucherService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public VoucherKindResponse save(Authentication authentication, @RequestBody Long voucherId) {
        return likedVoucherService.save(authentication.getName(), voucherId);
    }

    @GetMapping
    public List<VoucherKindResponse> findAllByUsername(Authentication authentication) {
        return likedVoucherService.findAllByUsername(authentication.getName());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{voucherKindId}")
    public void deleteByUsernameAndVoucherId(Authentication authentication, @PathVariable Long voucherKindId) {
        likedVoucherService.deleteByUsernameAndVoucherId(authentication.getName(), voucherKindId);
    }
}
