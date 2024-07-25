package com.givemecon.application.service;

import com.givemecon.common.exception.concrete.EntityNotFoundException;
import com.givemecon.common.util.FileUtils;
import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.entity.voucherkind.VoucherKindImage;
import com.givemecon.domain.repository.MemberRepository;
import com.givemecon.domain.repository.brand.BrandRepository;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import com.givemecon.domain.repository.voucherkind.VoucherKindImageRepository;
import com.givemecon.domain.repository.voucherkind.VoucherKindRepository;
import com.givemecon.infrastructure.s3.image_entity.ImageEntityUtils;
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

import static com.givemecon.application.dto.VoucherKindDto.*;
import static com.givemecon.domain.entity.voucher.VoucherStatus.FOR_SALE;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class VoucherKindService {

    private final BrandRepository brandRepository;

    private final VoucherKindRepository voucherKindRepository;

    private final VoucherKindImageRepository voucherKindImageRepository;

    private final VoucherRepository voucherRepository;

    private final MemberRepository memberRepository;

    private final ImageEntityUtils imageEntityUtils;

    public VoucherKindDetailResponse save(VoucherKindSaveRequest requestDto) {
        Brand brand = brandRepository.findById(requestDto.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException(Brand.class));

        if (brand.getCategory() == null) {
            throw new EntityNotFoundException(Category.class);
        }

        VoucherKindImage voucherKindImage = voucherKindImageRepository.save(
                imageEntityUtils.createImageEntity(VoucherKindImage.class, requestDto.getImageFile()));

        VoucherKind voucherKind = voucherKindRepository.save(VoucherKind.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .caution(requestDto.getCaution())
                .voucherKindImage(voucherKindImage)
                .brand(brand)
                .build());

        return new VoucherKindDetailResponse(voucherKind);
    }

    @Transactional(readOnly = true)
    public VoucherKindDetailResponse findOne(Long id) {
        VoucherKind voucherKind = voucherKindRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VoucherKind.class));

        return new VoucherKindDetailResponse(voucherKind, getMinPrice(voucherKind));
    }

    @Transactional(readOnly = true)
    public VoucherKindDetailResponse findOne(Long id, String username) {
        VoucherKind voucherKind = voucherKindRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VoucherKind.class));

        return new VoucherKindDetailResponse(voucherKind, getMinPrice(voucherKind, username));
    }

    @Transactional(readOnly = true)
    public List<VoucherKindResponse> findAll() {
        return voucherKindRepository.findAll().stream()
                .map(VoucherKindResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VoucherKindResponse> findAllWithMinPriceByBrandId(Long brandId) {
        return voucherKindRepository.findAllWithImageByBrandId(brandId).stream()
                .map(voucherKind -> new VoucherKindResponse(voucherKind, getMinPrice(voucherKind)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VoucherKindResponse> findAllWithMinPriceByBrandId(Long brandId, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        return voucherKindRepository.findAllWithImageByBrandId(brandId).stream()
                .map(voucherKind -> new VoucherKindResponse(voucherKind, getMinPrice(voucherKind, member)))
                .toList();
    }

    @Transactional(readOnly = true)
    public PagedVoucherKindResponse findPage(Pageable pageable) {
        Page<VoucherKindResponse> pageResult = voucherKindRepository.findAll(pageable)
                .map(voucherKind -> new VoucherKindResponse(voucherKind, getMinPrice(voucherKind)));

        return new PagedVoucherKindResponse(pageResult);
    }

    @Transactional(readOnly = true)
    public PagedVoucherKindResponse findPageByBrandName(String brandName, Pageable pageable) {
        Brand brand = brandRepository.findByName(brandName)
                .orElseThrow(() -> new EntityNotFoundException(Brand.class));

        Page<VoucherKindResponse> pageResult = voucherKindRepository.findPageByBrand(brand, pageable)
                .map(voucherKind -> new VoucherKindResponse(voucherKind, getMinPrice(voucherKind)));

        return new PagedVoucherKindResponse(pageResult);
    }

    // 최소 가격 조회
    private Long getMinPrice(VoucherKind voucherKind) {
        Pageable limit = PageRequest.of(0, 1);
        return voucherRepository.findOneWithMinPrice(voucherKind, FOR_SALE, limit).stream()
                .findFirst()
                .map(Voucher::getPrice)
                .orElse(0L);
    }

    // 최소 가격 조회 (회원이 판매 중인 기프티콘은 제외)
    private Long getMinPrice(VoucherKind voucherKind, String username) {
        Pageable limit = PageRequest.of(0, 1);
        return voucherRepository.findOneWithMinPrice(username, voucherKind, FOR_SALE, limit).stream()
                .findFirst()
                .map(Voucher::getPrice)
                .orElse(0L);
    }

    // 최소 가격 조회 (회원이 판매 중인 기프티콘은 제외)
    private Long getMinPrice(VoucherKind voucherKind, Member member) {
        Pageable limit = PageRequest.of(0, 1);
        return voucherRepository.findOneWithMinPrice(member, voucherKind, FOR_SALE, limit).stream()
                .findFirst()
                .map(Voucher::getPrice)
                .orElse(0L);
    }

    public VoucherKindDetailResponse update(Long id, VoucherKindUpdateRequest requestDto) {
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

        return new VoucherKindDetailResponse(voucherKind);
    }

    public void delete(Long id) {
        VoucherKind voucherKind = voucherKindRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(VoucherKind.class));

        voucherKindRepository.delete(voucherKind);
    }
}
