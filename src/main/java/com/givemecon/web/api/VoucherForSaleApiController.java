package com.givemecon.web.api;

import com.givemecon.domain.voucherforsale.VoucherForSaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.web.dto.VoucherForSaleDto.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/vouchers-for-sale")
@RestController
public class VoucherForSaleApiController {

    private final VoucherForSaleService voucherForSaleService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VoucherForSaleResponse save(Authentication authentication,
                                       @Validated @ModelAttribute VoucherForSaleRequest requestDto) {

        return voucherForSaleService.save(authentication.getName(), requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        voucherForSaleService.delete(id);
    }
}
