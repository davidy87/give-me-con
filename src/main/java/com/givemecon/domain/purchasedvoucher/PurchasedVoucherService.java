package com.givemecon.domain.purchasedvoucher;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.givemecon.util.error.ErrorCode.*;
import static com.givemecon.domain.purchasedvoucher.PurchasedVoucherDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class PurchasedVoucherService {

    private final MemberRepository memberRepository;

    private final VoucherForSaleRepository voucherForSaleRepository;

    private final PurchasedVoucherRepository purchasedVoucherRepository;

    public List<PurchasedVoucherResponse> saveAll(String username, List<PurchasedVoucherRequest> requestDtoList) {
        Member buyer = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND, Member.class));

        List<PurchasedVoucher> purchasedVouchers = purchasedVoucherRepository.saveAll(
                requestDtoList.stream()
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
        VoucherForSale voucherForSale = voucherForSaleRepository.findById(requestDto.getVoucherForSaleId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND, VoucherForSale.class));

        PurchasedVoucher purchasedVoucher = purchasedVoucherRepository.save(new PurchasedVoucher());
        purchasedVoucher.updateVoucherForSale(voucherForSale);
        buyer.addPurchasedVoucher(purchasedVoucher);

        return purchasedVoucher;
    }

    @Transactional(readOnly = true)
    public List<PurchasedVoucherResponse> findAllByUsername(String username) {
        Member owner = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND, Member.class));

        return owner.getPurchasedVoucherList().stream()
                .map(PurchasedVoucherResponse::new)
                .toList();
    }

    public PurchasedVoucherResponse find(Long id, String username) {
        return purchasedVoucherRepository.findByIdAndUsername(id, username)
                .map(PurchasedVoucherResponse::new)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND, PurchasedVoucher.class));
    }

    public PurchasedVoucherResponse updateValidity(Long id) {
        PurchasedVoucher purchasedVoucher = purchasedVoucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND, PurchasedVoucher.class));

        purchasedVoucher.updateValidity();

        return new PurchasedVoucherResponse(purchasedVoucher);
    }
}
