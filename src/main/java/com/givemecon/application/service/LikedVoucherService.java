package com.givemecon.application.service;

import com.givemecon.common.exception.concrete.EntityNotFoundException;
import com.givemecon.domain.entity.likedvoucher.LikedVoucher;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.repository.MemberRepository;
import com.givemecon.domain.repository.likedvoucher.LikedVoucherRepository;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import com.givemecon.domain.repository.voucherkind.VoucherKindRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.givemecon.application.dto.VoucherKindDto.*;
import static com.givemecon.application.dto.VoucherKindDto.PagedVoucherKindResponse;
import static com.givemecon.application.dto.VoucherKindDto.VoucherKindDetailResponse;
import static com.givemecon.domain.entity.voucher.VoucherStatus.FOR_SALE;

@RequiredArgsConstructor
@Service
@Transactional
public class LikedVoucherService {

    private final MemberRepository memberRepository;

    private final VoucherKindRepository voucherKindRepository;

    private final VoucherRepository voucherRepository;

    private final LikedVoucherRepository likedVoucherRepository;

    public VoucherKindDetailResponse save(String username, Long voucherId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        VoucherKind voucherKind = voucherKindRepository.findById(voucherId)
                .orElseThrow(() -> new EntityNotFoundException(VoucherKind.class));

        likedVoucherRepository.save(LikedVoucher.builder()
                .member(member)
                .voucherKind(voucherKind)
                .build());

        return new VoucherKindDetailResponse(voucherKind);
    }

    @Transactional(readOnly = true)
    public List<VoucherKindResponse> findAllByUsername(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        return likedVoucherRepository.findAllFetchedByMember(member).stream()
                .map(likedVoucher -> getMinPriceResponse(likedVoucher.getVoucherKind(), member))
                .toList();
    }

    // 최소 가격을 구해 VoucherKindDetailResponse DTO 반환 (최소 가격 조회 시, 사용자가 판매 중인 기프티콘은 제외)
    private VoucherKindResponse getMinPriceResponse(VoucherKind voucherKind, Member member) {
        Pageable limit = PageRequest.of(0, 1);
        Long minPrice = voucherRepository.findOneWithMinPrice(member, voucherKind, FOR_SALE, limit).stream()
                .findFirst()
                .map(Voucher::getPrice)
                .orElse(0L);

        return new VoucherKindResponse(voucherKind, minPrice);
    }

    @Transactional(readOnly = true)
    public PagedVoucherKindResponse findPageByUsername(String username, Pageable pageable) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        Page<VoucherKindResponse> pageResult = likedVoucherRepository.findPageByMember(member, pageable)
                .map(likedVoucher -> new VoucherKindDetailResponse(likedVoucher.getVoucherKind()));

        return new PagedVoucherKindResponse(pageResult);
    }

    public void deleteByUsernameAndVoucherId(String username, Long voucherKindId) {
        likedVoucherRepository.deleteByUsernameAndVoucherKindId(username, voucherKindId);
    }
}
