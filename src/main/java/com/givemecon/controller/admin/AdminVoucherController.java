package com.givemecon.controller.admin;

import com.givemecon.application.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
