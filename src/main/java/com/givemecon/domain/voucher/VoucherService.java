package com.givemecon.domain.voucher;

import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.util.error.ErrorCode.*;
import static com.givemecon.web.dto.VoucherDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class VoucherService {

    private final VoucherRepository voucherRepository;

    public VoucherResponse save(VoucherSaveRequest requestDto) {
        Voucher voucher = voucherRepository.save(requestDto.toEntity());

        return VoucherResponse.builder()
                .price(voucher.getPrice())
                .image(voucher.getImage())
                .build();
    }

    @Transactional(readOnly = true)
    public VoucherResponse find(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        return VoucherResponse.builder()
                .price(voucher.getPrice())
                .image(voucher.getImage())
                .build();
    }

    public VoucherResponse update(Long id, VoucherUpdateRequest requestDto) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        voucher.update(requestDto.getPrice(), requestDto.getImage());

        return VoucherResponse.builder()
                .price(voucher.getPrice())
                .image(voucher.getImage())
                .build();
    }

    public Long delete(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        voucherRepository.delete(voucher);

        return id;
    }
}
