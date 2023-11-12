package com.givemecon.domain.voucher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.web.dto.VoucherDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class VoucherService {

    private final VoucherRepository voucherRepository;

    public VoucherResponse save(VoucherSaveRequest requestDto) {
        Voucher voucher = voucherRepository.save(requestDto.toEntity());

        return VoucherResponse.builder()
                .id(voucher.getId())
                .price(voucher.getPrice())
                .image(voucher.getImage())
                .build();
    }

    @Transactional(readOnly = true)
    public VoucherResponse find(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(RuntimeException::new); // TODO: 예외 처리

        return VoucherResponse.builder()
                .id(voucher.getId())
                .price(voucher.getPrice())
                .image(voucher.getImage())
                .build();
    }

    public VoucherResponse update(Long id, VoucherUpdateRequest requestDto) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(RuntimeException::new); // TODO: 예외 처리

        voucher.update(requestDto.getPrice(), requestDto.getImage());

        return VoucherResponse.builder()
                .id(voucher.getId())
                .price(voucher.getPrice())
                .image(voucher.getImage())
                .build();
    }

    public Long delete(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(RuntimeException::new); // TODO: 예외 처리

        voucherRepository.delete(voucher);

        return id;
    }
}
