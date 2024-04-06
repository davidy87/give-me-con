package com.givemecon.domain.voucherforsale;

import com.givemecon.domain.image.voucherforsale.VoucherForSaleImage;
import com.givemecon.domain.image.voucherforsale.VoucherForSaleImageRepository;
import com.givemecon.util.gcp.ocr.TextExtractor;
import com.givemecon.util.image_entity.ImageEntityUtils;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.givemecon.domain.voucherforsale.VoucherForSaleDto.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class VoucherForSaleService {

    private final MemberRepository memberRepository;

    private final VoucherRepository voucherRepository;

    private final VoucherForSaleRepository voucherForSaleRepository;

    private final VoucherForSaleImageRepository voucherForSaleImageRepository;

    private final TextExtractor textExtractor;

    private final ImageEntityUtils imageEntityUtils;


    public VoucherForSaleResponse save(String username, VoucherForSaleRequest requestDto) {
        Member seller = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        Voucher voucher = voucherRepository.findById(requestDto.getVoucherId())
                .orElseThrow(() -> new EntityNotFoundException(Voucher.class));

        MultipartFile imageFile = requestDto.getImageFile();

        VoucherForSale voucherForSale = voucherForSaleRepository.save(requestDto.toEntity());
        VoucherForSaleImage voucherForSaleImage = voucherForSaleImageRepository.save(
                imageEntityUtils.createImageEntity(VoucherForSaleImage.class, imageFile));

        voucherForSale.updateVoucherForSaleImage(voucherForSaleImage);
        voucherForSale.updateSeller(seller);
        voucher.addVoucherForSale(voucherForSale);

        return new VoucherForSaleResponse(voucherForSale);
    }

    public Long delete(Long id) {
        VoucherForSale voucherForSale = voucherForSaleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VoucherForSale.class));

        voucherForSaleRepository.delete(voucherForSale);
        voucherForSale.getVoucher().removeVoucherForSale(voucherForSale);

        return id;
    }

    public List<String[]> extractTextFromImage(MultipartFile imageFile) {
        String textFromImage = textExtractor.extractTextFromImage(imageFile.getResource());
        log.info("Extracted text = {}", textFromImage);

        return textFromImage.lines()
                .filter(StringUtils::hasText)
                .map(textLine -> textLine.split(" "))
                .toList();
    }
}
