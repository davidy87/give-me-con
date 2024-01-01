package com.givemecon.web.api;

import com.givemecon.config.auth.jwt.JwtTokenProvider;
import com.givemecon.domain.voucher.VoucherForSaleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.web.dto.VoucherForSaleDto.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/vouchers-for-sale")
@RestController
public class VoucherForSaleApiController {

    private final VoucherForSaleService voucherForSaleService;

    private final JwtTokenProvider jwtTokenProvider;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public VoucherForSaleResponse save(HttpServletRequest request,
                                       @RequestBody VoucherForSaleRequest requestDto) {

        String accessToken = jwtTokenProvider.retrieveToken(request);
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        return voucherForSaleService.save(authentication.getName(), requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        voucherForSaleService.delete(id);
    }
}
