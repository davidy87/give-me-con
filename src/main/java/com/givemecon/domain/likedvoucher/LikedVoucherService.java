package com.givemecon.domain.likedvoucher;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.domain.voucher.VoucherDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class LikedVoucherService {

    private final MemberRepository memberRepository;

    private final VoucherRepository voucherRepository;

    private final LikedVoucherRepository likedVoucherRepository;

    public VoucherResponse save(String username, Long voucherId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new EntityNotFoundException(Voucher.class));

        LikedVoucher likedVoucher = likedVoucherRepository.save(LikedVoucher.builder()
                .voucher(voucher)
                .build());

        likedVoucher.updateMember(member);

        return new VoucherResponse(voucher);
    }

    @Transactional(readOnly = true)
    public PagedVoucherResponse findPageByUsername(String username, Pageable pageable) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        Page<VoucherResponse> pageResult = likedVoucherRepository.findPageByMember(member, pageable)
                .map(likedVoucher -> new VoucherResponse(likedVoucher.getVoucher()));

        return new PagedVoucherResponse(pageResult);
    }

    public void deleteByUsernameAndVoucherId(String username, Long voucherId) {
        likedVoucherRepository.deleteByUsernameAndVoucherId(username, voucherId);
    }
}
