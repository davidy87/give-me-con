package com.givemecon.domain.voucher;

import com.givemecon.domain.image.voucher.VoucherImage;
import com.givemecon.domain.image.voucher.VoucherImageRepository;
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

import static com.givemecon.domain.voucher.VoucherDto.*;
import static com.givemecon.domain.voucherforsale.VoucherForSaleDto.*;
import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class VoucherService {

    private final BrandRepository brandRepository;

    private final VoucherRepository voucherRepository;

    private final VoucherImageRepository voucherImageRepository;

    private final ImageEntityUtils imageEntityUtils;

    private final VoucherForSaleRepository voucherForSaleRepository;

    public VoucherResponse save(VoucherSaveRequest requestDto) {
        Brand brand = brandRepository.findById(requestDto.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException(Brand.class));

        if (brand.getCategory() == null) {
            throw new EntityNotFoundException(Category.class);
        }

        VoucherImage voucherImage = voucherImageRepository.save(
                imageEntityUtils.createImageEntity(VoucherImage.class, requestDto.getImageFile()));

        Voucher voucher = voucherRepository.save(requestDto.toEntity());
        voucher.updateVoucherImage(voucherImage);
        voucher.updateBrand(brand);

        return new VoucherResponse(voucher);
    }

    @Transactional(readOnly = true)
    public VoucherResponse find(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Voucher.class));

        return getMinPriceResponse(voucher);
    }

    @Transactional(readOnly = true)
    public List<VoucherResponse> findAll() {
        return voucherRepository.findAll().stream()
                .map(VoucherResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VoucherResponse> findAllByBrandId(Long brandId) {
        return voucherRepository.findAllWithVoucherImageByBrandId(brandId).stream()
                .map(this::getMinPriceResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PagedVoucherResponse findPage(Pageable pageable) {
        Page<VoucherResponse> pageResult = voucherRepository.findAll(pageable)
                .map(this::getMinPriceResponse);

        return new PagedVoucherResponse(pageResult);
    }

    @Transactional(readOnly = true)
    public PagedVoucherResponse findPageByBrandName(String brandName, Pageable pageable) {
        Brand brand = brandRepository.findByName(brandName)
                .orElseThrow(() -> new EntityNotFoundException(Brand.class));

        Page<VoucherResponse> pageResult = voucherRepository.findPageByBrand(brand, pageable)
                .map(this::getMinPriceResponse);

        return new PagedVoucherResponse(pageResult);
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
    public List<VoucherForSaleResponse> findSellingListByVoucherId(Long voucherId) {
        return voucherForSaleRepository.findAllByVoucherIdAndStatus(voucherId, FOR_SALE).stream()
                .map(VoucherForSaleResponse::new)
                .toList();
    }

    public VoucherResponse update(Long id, VoucherUpdateRequest requestDto) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Voucher.class));

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

        if (FileUtils.isFileValid(newImageFile)) {
            imageEntityUtils.updateImageEntity(voucher.getVoucherImage(), newImageFile);
        }

        return new VoucherResponse(voucher);
    }

    public void delete(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Voucher.class));

        voucherRepository.delete(voucher);
    }
}
