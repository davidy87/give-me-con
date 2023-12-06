package com.givemecon.domain.likedvoucher;

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
import static com.givemecon.web.dto.VoucherDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class LikedVoucherService {

    private final MemberRepository memberRepository;

    private final VoucherRepository voucherRepository;

    private final LikedVoucherRepository likedVoucherRepository;

    public Long save(String username, Long voucherId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        LikedVoucher likedVoucher = likedVoucherRepository.save(LikedVoucher.builder()
                .voucher(voucher)
                .member(member)
                .build());

        return likedVoucher.getId();
    }

    @Transactional(readOnly = true)
    public List<VoucherResponse> findAllByUsername(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        return likedVoucherRepository.findAll().stream()
                .filter(entity -> entity.getMember().equals(member))
                .map(entity -> new VoucherResponse(entity.getVoucher()))
                .toList();
    }

    public void delete(String username, Long voucherId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        LikedVoucher likedVoucher = likedVoucherRepository.findAll().stream()
                .filter(entity -> entity.getMember().equals(member) && entity.getVoucher().equals(voucher))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));// TODO: 예외 처리 변경 필요

        likedVoucherRepository.delete(likedVoucher);
    }
}
