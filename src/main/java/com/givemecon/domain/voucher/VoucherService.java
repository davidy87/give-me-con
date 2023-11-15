package com.givemecon.domain.voucher;

import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.givemecon.util.error.ErrorCode.*;
import static com.givemecon.web.dto.VoucherDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class VoucherService {

    private final VoucherRepository voucherRepository;

    public VoucherResponse save(VoucherSaveRequest requestDto) {
        Voucher voucher = voucherRepository.save(requestDto.toEntity());
        return new VoucherResponse(voucher);
    }

    @Transactional(readOnly = true)
    public VoucherResponse find(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        return new VoucherResponse(voucher);
    }

    public List<VoucherSellingResponse> findSellingListByVoucherId(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        return voucher.getVoucherSellingList().stream()
                .map(VoucherSellingResponse::new)
                .toList();
    }

    public VoucherResponse update(Long id, VoucherUpdateRequest requestDto) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        voucher.update(requestDto.getPrice(), requestDto.getImage());

        return new VoucherResponse(voucher);
    }

    public Long delete(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        voucherRepository.delete(voucher);

        return id;
    }
}
