package com.givemecon.application.service;

import com.givemecon.application.exception.InvalidRequestFieldException;
import com.givemecon.common.util.FileUtils;
import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.brand.BrandIcon;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.repository.brand.BrandIconRepository;
import com.givemecon.domain.repository.brand.BrandRepository;
import com.givemecon.domain.repository.category.CategoryRepository;
import com.givemecon.infrastructure.s3.image_entity.ImageEntityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.givemecon.application.dto.BrandDto.*;
import static com.givemecon.application.exception.errorcode.BrandErrorCode.INVALID_BRAND_ID;
import static com.givemecon.application.exception.errorcode.CategoryErrorCode.INVALID_CATEGORY_ID;

@RequiredArgsConstructor
@Service
@Transactional
public class BrandService {

    private final CategoryRepository categoryRepository;

    private final BrandRepository brandRepository;

    private final BrandIconRepository brandIconRepository;

    private final ImageEntityUtils imageEntityUtils;

    public BrandResponse save(BrandSaveRequest requestDto) {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_CATEGORY_ID));

        BrandIcon brandIcon = brandIconRepository.save(
                imageEntityUtils.createImageEntity(BrandIcon.class, requestDto.getIconFile()));

        Brand brand = brandRepository.save(Brand.builder()
                .name(requestDto.getName())
                .brandIcon(brandIcon)
                .category(category)
                .build());

        return new BrandResponse(brand);
    }

    @Transactional(readOnly = true)
    public List<BrandResponse> findAll() {
        return brandRepository.findAllWithBrandIcon()
                .stream()
                .map(BrandResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BrandResponse> findAllByCategory(Long categoryId) {
        return brandRepository.findAllWithBrandIconByCategoryId(categoryId).stream()
                .map(BrandResponse::new)
                .toList();
    }

    public BrandResponse update(Long id, BrandUpdateRequest requestDto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_BRAND_ID));

        Long categoryId = requestDto.getCategoryId();
        String newBrandName = requestDto.getName();
        MultipartFile newIconFile = requestDto.getIconFile();

        if (categoryId != null) {
            Category newCategory = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new InvalidRequestFieldException(INVALID_CATEGORY_ID));

            brand.updateCategory(newCategory);
        }

        if (StringUtils.hasText(newBrandName)) {
            brand.updateName(newBrandName);
        }

        if (FileUtils.isFileValid(newIconFile)) {
            imageEntityUtils.updateImageEntity(brand.getBrandIcon(), newIconFile);
        }

        return new BrandResponse(brand);
    }

    public void delete(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_BRAND_ID));

        brandRepository.delete(brand);
    }
}
