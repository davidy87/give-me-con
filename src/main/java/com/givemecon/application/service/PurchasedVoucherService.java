package com.givemecon.application.service;

import com.givemecon.application.exception.InvalidRequestFieldException;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.purchasedvoucher.PurchasedVoucher;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.repository.MemberRepository;
import com.givemecon.domain.repository.PurchasedVoucherRepository;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import com.givemecon.common.event.voucher.VoucherStatusUpdateEvent;
import com.givemecon.common.event.voucher.VoucherStatusUpdateEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.givemecon.application.dto.PurchasedVoucherDto.*;
import static com.givemecon.application.exception.errorcode.MemberErrorCode.*;
import static com.givemecon.application.exception.errorcode.PurchasedVoucherErrorCode.*;
import static com.givemecon.application.exception.errorcode.VoucherErrorCode.*;
import static com.givemecon.domain.entity.purchasedvoucher.PurchasedVoucherStatus.*;
import static com.givemecon.domain.entity.voucher.VoucherStatus.FOR_SALE;
import static com.givemecon.domain.entity.voucher.VoucherStatus.SOLD;

@RequiredArgsConstructor
@Service
@Transactional
public class PurchasedVoucherService {

    private final MemberRepository memberRepository;

    private final VoucherRepository voucherRepository;

    private final PurchasedVoucherRepository purchasedVoucherRepository;

    private final VoucherStatusUpdateEventPublisher eventPublisher;

    public List<PurchasedVoucherResponse> saveAll(String username, List<PurchasedVoucherRequest> requestDtoList) {
        Member buyer = memberRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_USERNAME));

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
        Voucher voucher = voucherRepository.findById(requestDto.getVoucherId())
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_VOUCHER_ID));

        if (voucher.getStatus() != FOR_SALE) {
            throw new InvalidRequestFieldException(VOUCHER_NOT_FOR_SALE);
        }

        voucher.updateStatus(SOLD);
        eventPublisher.publishEvent(new VoucherStatusUpdateEvent(voucher)); // 기프티콘 상태 변경 이벤트 발행

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
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_USERNAME));

        Page<PurchasedVoucherResponse> pageResult = purchasedVoucherRepository.findPageByOwner(owner, pageable)
                .map(PurchasedVoucherResponse::new);

        return new PagedPurchasedVoucherResponse(pageResult);
    }

    @Transactional(readOnly = true)
    public PurchasedVoucherResponse findOne(Long id, String username) {
        return purchasedVoucherRepository.findOneFetchedByIdAndUsername(id, username)
                .map(PurchasedVoucherResponse::new)
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_PURCHASED_VOUCHER_ID));
    }

    public StatusUpdateResponse setUsedOnUsable(Long id) {
        PurchasedVoucher purchasedVoucher = purchasedVoucherRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_PURCHASED_VOUCHER_ID));

        // purchasedVoucher의 현재 상태가 EXPIRED일 수도 있으므로, USABLE일 경우에만 변경
        if (purchasedVoucher.getStatus() != USABLE) {
            throw new InvalidRequestFieldException(PURCHASED_VOUCHER_NOT_USABLE);
        }

        purchasedVoucher.updateStatus(USED);

        return new StatusUpdateResponse(purchasedVoucher);
    }
}
