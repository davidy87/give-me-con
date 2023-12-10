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
            buyer.addPurchasedVoucher(purchasedVoucher);
        });

        return savedList.stream()
                .map(PurchasedVoucherResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PurchasedVoucherResponse> findAllByUsername(String username) {
        Member owner = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        return owner.getPurchasedVoucherList().stream()
                .map(PurchasedVoucherResponse::new)
                .toList();
    }

    public PurchasedVoucherResponse find(String username, Long id) {
        Member owner = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        return owner.getPurchasedVoucherList().stream()
                .filter(purchasedVoucher -> purchasedVoucher.getId().equals(id))
                .map(PurchasedVoucherResponse::new)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));
    }
}
