package com.givemecon.domain.purchasedvoucher.service;

import com.givemecon.domain.member.entity.Member;
import com.givemecon.domain.member.repository.MemberRepository;
import com.givemecon.domain.purchasedvoucher.entity.PurchasedVoucher;
import com.givemecon.domain.purchasedvoucher.repository.PurchasedVoucherRepository;
import com.givemecon.domain.voucher.entity.Voucher;
import com.givemecon.domain.voucher.repository.VoucherRepository;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.givemecon.domain.purchasedvoucher.dto.PurchasedVoucherDto.*;
import static com.givemecon.domain.purchasedvoucher.dto.PurchasedVoucherStatus.*;
import static com.givemecon.domain.voucher.dto.VoucherStatus.*;

@RequiredArgsConstructor
@Service
@Transactional
public class PurchasedVoucherService {

    private final MemberRepository memberRepository;

    private final VoucherRepository voucherRepository;

    private final PurchasedVoucherRepository purchasedVoucherRepository;

    public List<PurchasedVoucherResponse> saveAll(String username, List<PurchasedVoucherRequest> requestDtoList) {
        Member buyer = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        List<PurchasedVoucher> purchasedVouchers =
                purchasedVoucherRepository.saveAll(requestDtoList.stream()
                        .map(requestDto -> saveOne(buyer, requestDto))
                        .toList());

        return purchasedVouchers.stream()
                .map(PurchasedVoucherResponse::new)
                .toList();
    }

    /**
     * saveAll() 의 helper method
     * @param buyer 기프티콘 구매 요청한 회원
     * @param requestDto 요청 DTO
     * @return PurchasedVoucher Entity
     */
    private PurchasedVoucher saveOne(Member buyer, PurchasedVoucherRequest requestDto) {
        Voucher voucher = voucherRepository.findById(requestDto.getVoucherForSaleId())
                .filter(forSale -> forSale.getStatus() == FOR_SALE)
                .orElseThrow(() -> new EntityNotFoundException(Voucher.class));

        voucher.updateStatus(SOLD);

        return purchasedVoucherRepository.save(new PurchasedVoucher(voucher, buyer));
    }

    @Transactional(readOnly = true)
    public List<PurchasedVoucherResponse> findAllByUsername(String username) {
        return purchasedVoucherRepository.findAllFetchedByUsername(username).stream()
                .map(PurchasedVoucherResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public PagedPurchasedVoucherResponse findPageByUsername(String username, Pageable pageable) {
        Member owner = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        Page<PurchasedVoucherResponse> pageResult = purchasedVoucherRepository.findPageByOwner(owner, pageable)
                .map(PurchasedVoucherResponse::new);

        return new PagedPurchasedVoucherResponse(pageResult);
    }

    @Transactional(readOnly = true)
    public PurchasedVoucherResponse findOne(Long id, String username) {
        return purchasedVoucherRepository.findOneFetchedByIdAndUsername(id, username)
                .map(PurchasedVoucherResponse::new)
                .orElseThrow(() -> new EntityNotFoundException(PurchasedVoucher.class));
    }

    public StatusUpdateResponse setUsed(Long id) {
        PurchasedVoucher purchasedVoucher = purchasedVoucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(PurchasedVoucher.class));

        // purchasedVoucher의 현재 상태가 EXPIRED일 수도 있으므로, USABLE일 경우에만 변경
        if (purchasedVoucher.getStatus() == USABLE) {
            purchasedVoucher.updateStatus(USED);
        }

        return new StatusUpdateResponse(purchasedVoucher);
    }
}