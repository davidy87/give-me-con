package com.givemecon.domain.voucherforsale;

import com.givemecon.domain.image_entity_util.ImageEntityUtils;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.givemecon.util.error.ErrorCode.*;
import static com.givemecon.domain.voucherforsale.VoucherForSaleDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class VoucherForSaleService {

    private final MemberRepository memberRepository;

    private final VoucherRepository voucherRepository;

    private final VoucherForSaleRepository voucherForSaleRepository;

    private final VoucherForSaleImageRepository voucherForSaleImageRepository;

    private final ImageEntityUtils imageEntityUtils;

    public VoucherForSaleResponse save(String username, VoucherForSaleRequest requestDto) {
        Member seller = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        Voucher voucher = voucherRepository.findById(requestDto.getVoucherId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        MultipartFile imageFile = requestDto.getImageFile();

        VoucherForSale voucherForSale = voucherForSaleRepository.save(requestDto.toEntity());
        VoucherForSaleImage voucherForSaleImage = voucherForSaleImageRepository.save(
                (VoucherForSaleImage) imageEntityUtils.createImageEntity(VoucherForSaleImage.class, imageFile));

        voucherForSale.updateVoucherForSaleImage(voucherForSaleImage);
        voucherForSale.updateSeller(seller);
        voucher.addVoucherForSale(voucherForSale);

        return new VoucherForSaleResponse(voucherForSale);
    }

    public Long delete(Long id) {
        VoucherForSale voucherForSale = voucherForSaleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        voucherForSaleRepository.delete(voucherForSale);
        voucherForSale.getVoucher().removeVoucherForSale(voucherForSale);

        return id;
    }
}
