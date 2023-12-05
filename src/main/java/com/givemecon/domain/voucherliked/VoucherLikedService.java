package com.givemecon.domain.voucherliked;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.util.error.ErrorCode.*;

@RequiredArgsConstructor
@Service
@Transactional
public class VoucherLikedService {

    private final MemberRepository memberRepository;

    private final VoucherRepository voucherRepository;

    private final VoucherLikedRepository voucherLikedRepository;

    public Long save(String username, Long voucherId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        VoucherLiked voucherLiked = voucherLikedRepository.save(VoucherLiked.builder()
                .voucher(voucher)
                .member(member)
                .build());

        return voucherLiked.getId();
    }

    public void delete(String username, Long voucherId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        VoucherLiked voucherLiked = voucherLikedRepository.findAll().stream()
                .filter(entity -> entity.getMember().equals(member) && entity.getVoucher().equals(voucher))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));// TODO: 예외 처리 변경 필요

        voucherLikedRepository.delete(voucherLiked);
    }
}
