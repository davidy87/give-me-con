package com.givemecon.web.api;

import com.givemecon.domain.voucher.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.web.dto.VoucherDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/vouchers")
@RestController
public class VoucherApiController {

    private final VoucherService voucherService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public VoucherResponse save(@RequestBody VoucherSaveRequest requestDto) {
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

    @PutMapping("/{id}")
    public VoucherResponse update(@PathVariable Long id,
                                  @RequestBody VoucherUpdateRequest requestDto) {

        return voucherService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    public Long delete(@PathVariable Long id) {
        return voucherService.delete(id);
    }
}
