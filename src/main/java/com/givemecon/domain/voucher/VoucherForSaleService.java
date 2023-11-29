package com.givemecon.domain.voucher;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.util.error.ErrorCode;
import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.web.dto.VoucherDto.*;
import static com.givemecon.web.dto.VoucherForSaleDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class VoucherForSaleService {

    private final MemberRepository memberRepository;

    private final VoucherRepository voucherRepository;

    private final VoucherForSaleRepository voucherForSaleRepository;

    public VoucherForSaleResponse save(String username, VoucherForSaleRequest requestDto) {
        Member seller = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND));

        VoucherForSale voucherForSale = voucherForSaleRepository.save(requestDto.toEntity());

        // voucher가 없을 시, voucher 새로 저장 후 voucherForSale 등록
        Voucher voucher = voucherRepository.findByTitle(voucherForSale.getTitle())
                .orElse(Voucher.builder()
                        .title(voucherForSale.getTitle())
                        .price(voucherForSale.getPrice())
                        .image(voucherForSale.getImage())
                        .build());

        Voucher voucherSaved = voucherRepository.save(voucher);

        voucherForSale.setSeller(seller);
        voucherSaved.addVoucherForSale(voucherForSale);

        return new VoucherForSaleResponse(voucherForSale);
    }

    public Long delete(Long id) {
        VoucherForSale voucherForSale = voucherForSaleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND));

        voucherForSaleRepository.delete(voucherForSale);

        return id;
    }
}
