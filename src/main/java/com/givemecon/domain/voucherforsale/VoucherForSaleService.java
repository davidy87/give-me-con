package com.givemecon.domain.voucherforsale;

import com.givemecon.domain.AwsS3Service;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.givemecon.util.error.ErrorCode.*;
import static com.givemecon.web.dto.VoucherForSaleDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class VoucherForSaleService {

    private final MemberRepository memberRepository;

    private final VoucherRepository voucherRepository;

    private final VoucherForSaleRepository voucherForSaleRepository;

    private final VoucherForSaleImageRepository voucherForSaleImageRepository;

    private final AwsS3Service awsS3Service;

    public VoucherForSaleResponse save(String username, VoucherForSaleRequest requestDto) {
        Member seller = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        Voucher voucher = voucherRepository.findById(requestDto.getVoucherId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        VoucherForSale voucherForSale = voucherForSaleRepository.save(requestDto.toEntity());

        MultipartFile imageFile = requestDto.getImageFile();
        String originalName = imageFile.getOriginalFilename();
        String imageKey = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(originalName);

        try {
            String imageUrl = awsS3Service.upload(imageKey, imageFile.getInputStream());
            VoucherForSaleImage voucherForSaleImage = voucherForSaleImageRepository.save(VoucherForSaleImage.builder()
                    .imageKey(imageKey)
                    .imageUrl(imageUrl)
                    .originalName(originalName)
                    .build());

            voucherForSale.updateVoucherForSaleImage(voucherForSaleImage);
            voucherForSale.updateSeller(seller);
            voucher.addVoucherForSale(voucherForSale);
        } catch (IOException e) {
            throw new RuntimeException("판매할 기프티콘 이미지 업로드 실패.");
        }

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
