package com.givemecon.domain.voucherpurchased;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.util.error.ErrorCode.*;
import static com.givemecon.web.dto.VoucherPurchasedDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class VoucherPurchasedService {

    private final MemberRepository memberRepository;

    private final VoucherRepository voucherRepository;

    private final VoucherPurchasedRepository voucherPurchasedRepository;

    public VoucherPurchasedResponse save(String username, VoucherPurchasedRequest requestDto) {
        Member buyer = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        Voucher voucher = voucherRepository.findById(requestDto.getVoucherId())
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        VoucherPurchased voucherPurchased = voucherPurchasedRepository.save(requestDto.toEntity());
        voucherPurchased.setCategory(voucher.getCategory());
        voucherPurchased.setBrand(voucher.getBrand());
        voucherPurchased.setOwner(buyer);

        return new VoucherPurchasedResponse(voucherPurchased);
    }
}
