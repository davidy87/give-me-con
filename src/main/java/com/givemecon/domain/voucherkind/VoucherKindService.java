package com.givemecon.domain.voucherkind;

import com.givemecon.domain.image.voucherkind.VoucherKindImage;
import com.givemecon.domain.image.voucherkind.VoucherKindImageRepository;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import com.givemecon.util.image_entity.ImageEntityUtils;
import com.givemecon.util.FileUtils;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.category.Category;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.givemecon.domain.voucherkind.VoucherKindDto.*;
import static com.givemecon.domain.voucherforsale.VoucherForSaleDto.*;
import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class VoucherKindService {

    private final BrandRepository brandRepository;

    private final VoucherKindRepository voucherKindRepository;

    private final VoucherKindImageRepository voucherKindImageRepository;

    private final ImageEntityUtils imageEntityUtils;

    private final VoucherForSaleRepository voucherForSaleRepository;

    public VoucherKindResponse save(VoucherKindSaveRequest requestDto) {
        Brand brand = brandRepository.findById(requestDto.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException(Brand.class));

        if (brand.getCategory() == null) {
            throw new EntityNotFoundException(Category.class);
        }

        VoucherKindImage voucherKindImage = voucherKindImageRepository.save(
                imageEntityUtils.createImageEntity(VoucherKindImage.class, requestDto.getImageFile()));

        VoucherKind voucherKind = voucherKindRepository.save(requestDto.toEntity());
        voucherKind.updateVoucherImage(voucherKindImage);
        voucherKind.updateBrand(brand);

        return new VoucherKindResponse(voucherKind);
    }

    @Transactional(readOnly = true)
    public VoucherKindResponse find(Long id) {
        VoucherKind voucherKind = voucherKindRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VoucherKind.class));

        return getMinPriceResponse(voucherKind);
    }

    @Transactional(readOnly = true)
    public List<VoucherKindResponse> findAll() {
        return voucherKindRepository.findAll().stream()
                .map(VoucherKindResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VoucherKindResponse> findAllByBrandId(Long brandId) {
        return voucherKindRepository.findAllWithImageByBrandId(brandId).stream()
                .map(this::getMinPriceResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PagedVoucherKindResponse findPage(Pageable pageable) {
        Page<VoucherKindResponse> pageResult = voucherKindRepository.findAll(pageable)
                .map(this::getMinPriceResponse);

        return new PagedVoucherKindResponse(pageResult);
    }

    @Transactional(readOnly = true)
    public PagedVoucherKindResponse findPageByBrandName(String brandName, Pageable pageable) {
        Brand brand = brandRepository.findByName(brandName)
                .orElseThrow(() -> new EntityNotFoundException(Brand.class));

        Page<VoucherKindResponse> pageResult = voucherKindRepository.findPageByBrand(brand, pageable)
                .map(this::getMinPriceResponse);

        return new PagedVoucherKindResponse(pageResult);
    }

    // 최소 가격을 구해 VoucherKindResponse DTO 반환
    private VoucherKindResponse getMinPriceResponse(VoucherKind voucherKind) {
        Pageable limit = PageRequest.of(0, 1);
        Long minPrice = voucherForSaleRepository.findOneWithMinPrice(voucherKind, FOR_SALE, limit).stream()
                .findFirst()
                .map(VoucherForSale::getPrice)
                .orElse(0L);

        return new VoucherKindResponse(voucherKind, minPrice);
    }

    @Transactional(readOnly = true)
    public List<VoucherForSaleResponse> findSellingListByVoucherId(Long voucherId) {
        return voucherForSaleRepository.findAllByVoucherKindIdAndStatus(voucherId, FOR_SALE).stream()
                .map(VoucherForSaleResponse::new)
                .toList();
    }

    public VoucherKindResponse update(Long id, VoucherKindUpdateRequest requestDto) {
        VoucherKind voucherKind = voucherKindRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VoucherKind.class));

        String newTitle = requestDto.getTitle();
        String newDescription = requestDto.getDescription();
        String newCaution = requestDto.getCaution();
        MultipartFile newImageFile = requestDto.getImageFile();

        if (StringUtils.hasText(newTitle)) {
            voucherKind.updateTitle(newTitle);
        }

        if (StringUtils.hasText(newDescription)) {
            voucherKind.updateDescription(newDescription);
        }

        if (StringUtils.hasText(newCaution)) {
            voucherKind.updateCaution(newCaution);
        }

        if (FileUtils.isFileValid(newImageFile)) {
            imageEntityUtils.updateImageEntity(voucherKind.getVoucherKindImage(), newImageFile);
        }

        return new VoucherKindResponse(voucherKind);
    }

    public void delete(Long id) {
        VoucherKind voucherKind = voucherKindRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VoucherKind.class));

        voucherKindRepository.delete(voucherKind);
    }
}
