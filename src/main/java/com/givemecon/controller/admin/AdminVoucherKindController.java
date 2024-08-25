package com.givemecon.controller.admin;

import com.givemecon.application.service.VoucherKindService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.application.dto.VoucherKindDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/admin/voucher-kinds")
@RestController
public class AdminVoucherKindController {

    private final VoucherKindService voucherKindService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VoucherKindDetailResponse save(@Validated @ModelAttribute VoucherKindSaveRequest requestDto) {
        return voucherKindService.save(requestDto);
    }

    @PostMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VoucherKindDetailResponse update(@PathVariable Long id,
                                            @ModelAttribute VoucherKindUpdateRequest requestDto) {

        return voucherKindService.update(id, requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        voucherKindService.delete(id);
    }
}
