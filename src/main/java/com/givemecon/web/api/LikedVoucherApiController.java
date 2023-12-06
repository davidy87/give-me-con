package com.givemecon.web.api;

import com.givemecon.domain.likedvoucher.LikedVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.web.dto.VoucherDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/liked-vouchers")
@RestController
public class LikedVoucherApiController {

    private final LikedVoucherService likedVoucherService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Long save(Authentication authentication, @RequestBody Long voucherId) {
        return likedVoucherService.save(authentication.getName(), voucherId);
    }

    @GetMapping
    public List<VoucherResponse> findAllByUsername(Authentication authentication) {
        return likedVoucherService.findAllByUsername(authentication.getName());
    }

    @DeleteMapping
    public void delete(Authentication authentication, @RequestBody Long voucherId) {
        likedVoucherService.delete(authentication.getName(), voucherId);
    }
}
