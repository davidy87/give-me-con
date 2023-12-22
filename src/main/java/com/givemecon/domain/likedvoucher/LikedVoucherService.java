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
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        LikedVoucher likedVoucher = likedVoucherRepository.save(LikedVoucher.builder()
                .voucher(voucher)
                .build());

        member.addLikedVoucher(likedVoucher);

        return likedVoucher.getId();
    }

    @Transactional(readOnly = true)
    public List<VoucherResponse> findAllByUsername(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        return member.getLikedVoucherList().stream()
                .map(likedVoucher -> new VoucherResponse(likedVoucher.getVoucher()))
                .toList();
    }

//    public void delete(String username, Long voucherId) {
//        Member member = memberRepository.findByUsername(username)
//                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
//
//        Voucher voucher = voucherRepository.findById(voucherId)
//                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
//
//        LikedVoucher likedVoucher = member.getLikedVoucherList().stream()
//                .filter(entity -> entity.getVoucher().equals(voucher))
//                .findFirst()
//                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND)); // TODO: 예외 처리 변경 필요
//
//        likedVoucherRepository.delete(likedVoucher);
//    }

    public void deleteByUsernameAndVoucherId(String username, Long voucherId) {
        likedVoucherRepository.deleteByUsernameAndVoucherId(voucherId, username);
    }
}
