package com.givemecon.domain.likedvoucher;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.givemecon.domain.voucher.VoucherDto.*;
import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.FOR_SALE;

@RequiredArgsConstructor
@Service
@Transactional
public class LikedVoucherService {

    private final MemberRepository memberRepository;

    private final VoucherRepository voucherRepository;

    private final VoucherForSaleRepository voucherForSaleRepository;

    private final LikedVoucherRepository likedVoucherRepository;

    public VoucherResponse save(String username, Long voucherId) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new EntityNotFoundException(Voucher.class));

        likedVoucherRepository.save(LikedVoucher.builder()
                .member(member)
                .voucher(voucher)
                .build());

        return new VoucherResponse(voucher);
    }

    @Transactional(readOnly = true)
    public List<VoucherResponse> findAllByUsername(String username) {
        return likedVoucherRepository.findAllFetchedByUsername(username).stream()
                .map(likedVoucher -> getMinPriceResponse(likedVoucher.getVoucher()))
                .toList();
    }

    // 최소 가격을 구해 VoucherResponse DTO 반환
    private VoucherResponse getMinPriceResponse(Voucher voucher) {
        Pageable limit = PageRequest.of(0, 1);
        Long minPrice = voucherForSaleRepository.findOneWithMinPrice(voucher, FOR_SALE, limit).stream()
                .findFirst()
                .map(VoucherForSale::getPrice)
                .orElse(0L);

        return new VoucherResponse(voucher, minPrice);
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
