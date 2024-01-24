package com.givemecon.web.api;

import com.givemecon.domain.voucher.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.web.dto.VoucherDto.*;
import static com.givemecon.web.dto.VoucherForSaleDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/vouchers")
@RestController
public class VoucherApiController {

    private final VoucherService voucherService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VoucherResponse save(@ModelAttribute VoucherSaveRequest requestDto) {

        return voucherService.save(requestDto);
    }

    @GetMapping
    public List<VoucherResponse> findAll(@RequestParam(required = false) String brandName) {
        if (brandName != null) {
            return voucherService.findAllByBrandName(brandName);
        }

        return voucherService.findAll();
    }

    @GetMapping("/{id}")
    public VoucherResponse find(@PathVariable Long id) {
        return voucherService.find(id);
    }

    @GetMapping("/{id}/selling-list")
    public List<VoucherForSaleResponse> findSellingListByVoucherId(@PathVariable Long id) {
        return voucherService.findSellingListByVoucherId(id);
    }

    @PostMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VoucherResponse update(@PathVariable Long id,
                                  @ModelAttribute VoucherUpdateRequest requestDto) {

        return voucherService.update(id, requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        voucherService.delete(id);
    }
}
