package com.givemecon.controller.api;

import com.givemecon.domain.voucherforsale.VoucherForSaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.domain.voucherforsale.VoucherForSaleDto.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/vouchers-for-sale")
@RestController
public class VoucherForSaleApiController {

    private final VoucherForSaleService voucherForSaleService;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VoucherForSaleResponse save(Authentication authentication,
                                       @Validated @ModelAttribute VoucherForSaleRequest requestDto) {

        return voucherForSaleService.save(authentication.getName(), requestDto);
    }

    @GetMapping
    public List<VoucherForSaleResponse> findAll(Authentication authentication,
                                                @Validated @ModelAttribute StatusCodeParameter paramDto) {

        if (paramDto.getStatusCode() == null) {
            return voucherForSaleService.findAllByUsername(authentication.getName());
        }

        return voucherForSaleService.findAllByStatus(paramDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public VoucherForSaleResponse updateStatus(@PathVariable Long id,
                                               @Validated @RequestBody StatusCodeBody bodyDto) {

        return voucherForSaleService.updateStatus(id, bodyDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        voucherForSaleService.delete(id);
    }
}
