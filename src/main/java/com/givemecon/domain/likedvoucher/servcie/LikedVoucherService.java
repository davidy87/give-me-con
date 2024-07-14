package com.givemecon.domain.likedvoucher.servcie;

import com.givemecon.domain.likedvoucher.entity.LikedVoucher;
import com.givemecon.domain.likedvoucher.repository.LikedVoucherRepository;
import com.givemecon.domain.member.entity.Member;
import com.givemecon.domain.member.repository.MemberRepository;
import com.givemecon.domain.voucher.entity.Voucher;
import com.givemecon.domain.voucherkind.entity.VoucherKind;
import com.givemecon.domain.voucherkind.repository.VoucherKindRepository;
import com.givemecon.domain.voucher.repository.VoucherRepository;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.givemecon.domain.voucherkind.dto.VoucherKindDto.*;
import static com.givemecon.domain.voucher.dto.VoucherStatus.FOR_SALE;

@RequiredArgsConstructor
@Service
@Transactional
public class LikedVoucherService {

    private final MemberRepository memberRepository;

    private final VoucherKindRepository voucherKindRepository;

    private final VoucherRepository voucherRepository;

    private final LikedVoucherRepository likedVoucherRepository;

    public VoucherKindResponse save(String username, Long voucherId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        VoucherKind voucherKind = voucherKindRepository.findById(voucherId)
                .orElseThrow(() -> new EntityNotFoundException(VoucherKind.class));

        likedVoucherRepository.save(LikedVoucher.builder()
                .member(member)
                .voucherKind(voucherKind)
                .build());

        return new VoucherKindResponse(voucherKind);
    }

    @Transactional(readOnly = true)
    public List<VoucherKindResponse> findAllByUsername(String username) {
        return likedVoucherRepository.findAllFetchedByUsername(username).stream()
                .map(likedVoucher -> getMinPriceResponse(likedVoucher.getVoucherKind()))
                .toList();
    }

    // 최소 가격을 구해 VoucherKindResponse DTO 반환
    private VoucherKindResponse getMinPriceResponse(VoucherKind voucherKind) {
        Pageable limit = PageRequest.of(0, 1);
        Long minPrice = voucherRepository.findOneWithMinPrice(voucherKind, FOR_SALE, limit).stream()
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
                .map(likedVoucher -> new VoucherKindResponse(likedVoucher.getVoucherKind()));

        return new PagedVoucherKindResponse(pageResult);
    }

    public void deleteByUsernameAndVoucherId(String username, Long voucherKindId) {
        likedVoucherRepository.deleteByUsernameAndVoucherKindId(username, voucherKindId);
    }
}
