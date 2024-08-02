package com.givemecon.controller;

import com.givemecon.application.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.application.dto.VoucherDto.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/vouchers")
@RestController
public class VoucherController {

    private final VoucherService voucherService;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VoucherResponse save(Authentication authentication,
                                @Validated @ModelAttribute VoucherRequest requestDto) {

        return voucherService.save(authentication.getName(), requestDto);
    }

    @GetMapping
    public List<VoucherResponse> findAll(Authentication authentication,
                                         @Validated @ModelAttribute QueryParameter paramDto) {

        String username = authentication.getName();

        if (paramDto.getStatusCode() != null) {
            return voucherService.findAllByStatusAndUsername(paramDto, username);
        }

        if (paramDto.getVoucherKindId() != null) {
            return voucherService.findAllForSaleByVoucherKindId(paramDto.getVoucherKindId(), username);
        }

        return voucherService.findAllByUsername(username);
    }

    @GetMapping("/{id}/image")
    public ImageResponse findImageUrl(@PathVariable Long id) {
        return voucherService.findImageUrl(id);
    }
}
