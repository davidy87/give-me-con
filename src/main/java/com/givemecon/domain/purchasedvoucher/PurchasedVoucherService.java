package com.givemecon.domain.purchasedvoucher;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.givemecon.util.error.ErrorCode.*;
import static com.givemecon.web.dto.PurchasedVoucherDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class PurchasedVoucherService {

    private final MemberRepository memberRepository;

    private final VoucherRepository voucherRepository;

    private final PurchasedVoucherRepository purchasedVoucherRepository;

    public List<PurchasedVoucherResponse> saveAll(String username, List<PurchasedVoucherRequest> requestDtoList) {
        Member buyer = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        Voucher voucher = voucherRepository.findById(requestDtoList.get(0).getVoucherId())
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        List<PurchasedVoucher> savedList = purchasedVoucherRepository.saveAll(requestDtoList.stream()
                .map(PurchasedVoucherRequest::toEntity)
                .toList());

        savedList.forEach(purchasedVoucher -> {
            purchasedVoucher.setCategory(voucher.getCategory());
            purchasedVoucher.setBrand(voucher.getBrand());
            purchasedVoucher.setOwner(buyer);
        });

        return savedList.stream()
                .map(PurchasedVoucherResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PurchasedVoucherResponse> findAllByUsername(String username) {
        Member owner = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        return purchasedVoucherRepository.findAll().stream()
                .filter(entity -> entity.getOwner().equals(owner))
                .map(PurchasedVoucherResponse::new)
                .toList();
    }
}
