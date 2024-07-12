package com.givemecon.controller.api;

import com.givemecon.domain.voucherkind.VoucherKindService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.domain.voucherkind.VoucherKindDto.*;
import static com.givemecon.domain.voucher.VoucherDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/voucher-kinds")
@RestController
public class VoucherKindApiController {

    private final VoucherKindService voucherKindService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VoucherKindResponse save(@Validated @ModelAttribute VoucherKindSaveRequest requestDto) {
        return voucherKindService.save(requestDto);
    }

    @GetMapping
    public List<VoucherKindResponse> findAll(@RequestParam(required = false) Long brandId) {
        if (brandId != null) {
            return voucherKindService.findAllByBrandId(brandId);
        }

        return voucherKindService.findAll();
    }

    @GetMapping("/{id}")
    public VoucherKindResponse find(@PathVariable Long id) {
        return voucherKindService.find(id);
    }

    @GetMapping("/{id}/selling-list")
    public List<VoucherResponse> findSellingListByVoucherId(@PathVariable Long id) {
        return voucherKindService.findSellingListByVoucherId(id);
    }

    @PostMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VoucherKindResponse update(@PathVariable Long id,
                                      @ModelAttribute VoucherKindUpdateRequest requestDto) {

        return voucherKindService.update(id, requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        voucherKindService.delete(id);
    }
}
