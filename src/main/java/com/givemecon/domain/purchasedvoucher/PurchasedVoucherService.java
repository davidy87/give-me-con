package com.givemecon.domain.purchasedvoucher;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.util.error.ErrorCode.*;
import static com.givemecon.web.dto.PurchasedVoucherDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class PurchasedVoucherService {

    private final MemberRepository memberRepository;

    private final VoucherRepository voucherRepository;

    private final PurchasedVoucherRepository purchasedVoucherRepository;

    public PurchasedVoucherResponse save(String username, PurchasedVoucherRequest requestDto) {
        Member buyer = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        Voucher voucher = voucherRepository.findById(requestDto.getVoucherId())
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        PurchasedVoucher purchasedVoucher = purchasedVoucherRepository.save(requestDto.toEntity());
        purchasedVoucher.setCategory(voucher.getCategory());
        purchasedVoucher.setBrand(voucher.getBrand());
        purchasedVoucher.setOwner(buyer);

        return new PurchasedVoucherResponse(purchasedVoucher);
    }
}
