package com.givemecon.domain.voucher;

import com.givemecon.domain.AwsS3Service;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.givemecon.util.error.ErrorCode.*;
import static com.givemecon.web.dto.VoucherDto.*;
import static com.givemecon.web.dto.VoucherForSaleDto.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class VoucherService {

    private final BrandRepository brandRepository;

    private final VoucherRepository voucherRepository;

    private final VoucherImageRepository voucherImageRepository;

    private final AwsS3Service awsS3Service;

    public VoucherResponse save(VoucherSaveRequest requestDto) {
        Brand brand = brandRepository.findById(requestDto.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        Voucher voucher = voucherRepository.save(requestDto.toEntity());
        brand.addVoucher(voucher);

        MultipartFile imageFile = requestDto.getImageFile();
        String originalName = imageFile.getOriginalFilename();
        String imageKey = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(originalName);

        try {
            String imageUrl = awsS3Service.upload(imageKey, imageFile.getInputStream());
            VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                    .imageKey(imageKey)
                    .imageUrl(imageUrl)
                    .originalName(originalName)
                    .build());

            voucher.setVoucherImage(voucherImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new VoucherResponse(voucher);
    }

//    /**
//     * VoucherForSale 생성 시, 해당 VoucherForSale의 title과 일치하는 Voucher entity가 없다면, Voucher 새로
//     * 저장 후 VoucherForSale 등록
//     * @param voucherForSale 생성된 VoucherForSale entity
//     */
//    public void saveIfNotExist(VoucherForSale voucherForSale) {
//        Voucher voucher = voucherRepository.findByTitle(voucherForSale.getTitle())
//                .orElse(Voucher.builder()
//                        .title(voucherForSale.getTitle())
//                        .price(voucherForSale.getPrice())
//                        .build());
//
//        Voucher voucherSaved = voucherRepository.save(voucher);
//        voucherSaved.addVoucherForSale(voucherForSale);
//    }

    @Transactional(readOnly = true)
    public VoucherResponse find(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        return new VoucherResponse(voucher);
    }

    @Transactional(readOnly = true)
    public List<VoucherResponse> findAll() {
        return voucherRepository.findAll().stream()
                .map(VoucherResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VoucherResponse> findAllByBrandName(String brandName) {
        Brand brand = brandRepository.findByName(brandName)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        return brand.getVoucherList().stream()
                .map(VoucherResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VoucherForSaleResponse> findSellingListByVoucherId(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        return voucher.getVoucherForSaleList().stream()
                .map(VoucherForSaleResponse::new)
                .toList();
    }

    public VoucherResponse update(Long id, VoucherUpdateRequest requestDto) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        String newTitle = requestDto.getTitle();
        String newDescription = requestDto.getDescription();
        String newCaution = requestDto.getCaution();
        MultipartFile newImageFile = requestDto.getImageFile();

        if (newTitle != null) {
            voucher.updateTitle(newTitle);
        }

        if (newDescription != null) {
            voucher.updateDescription(newDescription);
        }

        if (newCaution != null) {
            voucher.updateCaution(newCaution);
        }

        if (newImageFile != null && !newImageFile.isEmpty()) {
            VoucherImage voucherImage = voucher.getVoucherImage();

            try {
                String newImageUrl = awsS3Service.upload(voucherImage.getImageKey(), newImageFile.getInputStream());
                String newOriginalName = newImageFile.getOriginalFilename();
                voucherImage.update(newImageUrl, newOriginalName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return new VoucherResponse(voucher);
    }

    public Long delete(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        voucherRepository.delete(voucher);

        return id;
    }
}
