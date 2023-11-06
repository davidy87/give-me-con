package com.givemecon.web.api;

import com.givemecon.domain.voucher.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public VoucherResponse find(@PathVariable Long id) {
        return voucherService.find(id);
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
