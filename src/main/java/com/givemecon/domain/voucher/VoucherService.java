package com.givemecon.domain.voucher;

import com.givemecon.domain.image_entity_util.ImageEntityUtils;
import com.givemecon.util.FileUtils;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.category.CategoryRepository;
import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.givemecon.util.error.ErrorCode.*;
import static com.givemecon.web.dto.VoucherDto.*;
import static com.givemecon.web.dto.VoucherForSaleDto.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class VoucherService {

    private final CategoryRepository categoryRepository;

    private final BrandRepository brandRepository;

    private final VoucherRepository voucherRepository;

    private final VoucherImageRepository voucherImageRepository;

    private final ImageEntityUtils imageEntityUtils;

    public VoucherResponse save(VoucherSaveRequest requestDto) {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        Brand brand = brandRepository.findById(requestDto.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        MultipartFile imageFile = requestDto.getImageFile();

        Voucher voucher = voucherRepository.save(requestDto.toEntity());
        VoucherImage voucherImage = voucherImageRepository.save(
                (VoucherImage) imageEntityUtils.createImageEntity(VoucherImage.class, imageFile));

        voucher.updateVoucherImage(voucherImage);
        category.addVoucher(voucher);
        brand.addVoucher(voucher);

        return new VoucherResponse(voucher);
    }

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

        if (StringUtils.hasText(newTitle)) {
            voucher.updateTitle(newTitle);
        }

        if (StringUtils.hasText(newDescription)) {
            voucher.updateDescription(newDescription);
        }

        if (StringUtils.hasText(newCaution)) {
            voucher.updateCaution(newCaution);
        }

        if (FileUtils.isValidFile(newImageFile)) {
            VoucherImage voucherImage = voucher.getVoucherImage();
            imageEntityUtils.updateImageEntity(voucherImage, newImageFile);
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
