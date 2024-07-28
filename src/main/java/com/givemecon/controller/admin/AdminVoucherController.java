package com.givemecon.controller.admin;

import com.givemecon.application.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.application.dto.VoucherDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/admin/vouchers")
@RestController
public class AdminVoucherController {

    private final VoucherService voucherService;

    @GetMapping
    public List<VoucherResponse> findAllByStatus(@Validated @ModelAttribute QueryParameter paramDto) {
        return voucherService.findAllByStatus(paramDto);
    }

    @PutMapping("/{id}")
    public VoucherResponse updateStatus(@PathVariable Long id,
                                        @Validated @RequestBody StatusUpdateRequest requestDto) {

        return voucherService.updateStatus(id, requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        voucherService.delete(id);
    }
}
