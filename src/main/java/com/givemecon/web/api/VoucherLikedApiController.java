package com.givemecon.web.api;

import com.givemecon.domain.voucherliked.VoucherLikedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/like")
@RestController
public class VoucherLikedApiController {

    private final VoucherLikedService voucherLikedService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Long save(Authentication authentication, @RequestBody Long voucherId) {
        return voucherLikedService.save(authentication.getName(), voucherId);
    }

    @DeleteMapping
    public void delete(Authentication authentication, @RequestBody Long voucherId) {
        voucherLikedService.delete(authentication.getName(), voucherId);
    }
}
