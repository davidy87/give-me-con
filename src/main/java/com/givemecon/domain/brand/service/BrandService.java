package com.givemecon.domain.brand.service;

import com.givemecon.domain.brand.entity.Brand;
import com.givemecon.domain.brand.repository.BrandRepository;
import com.givemecon.domain.image.entity.BrandIcon;
import com.givemecon.domain.image.repository.BrandIconRepository;
import com.givemecon.domain.voucherkind.repository.VoucherKindRepository;
import com.givemecon.util.image_entity.ImageEntityUtils;
import com.givemecon.util.FileUtils;
import com.givemecon.domain.category.entity.Category;
import com.givemecon.domain.category.repository.CategoryRepository;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.givemecon.domain.brand.dto.BrandDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class BrandService {

    private final CategoryRepository categoryRepository;

    private final BrandRepository brandRepository;

    private final BrandIconRepository brandIconRepository;

    private final VoucherKindRepository voucherKindRepository;

    private final ImageEntityUtils imageEntityUtils;

    public BrandResponse save(BrandSaveRequest requestDto) {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException(Category.class));

        BrandIcon brandIcon = brandIconRepository.save(
                imageEntityUtils.createImageEntity(BrandIcon.class, requestDto.getIconFile()));

        Brand brand = brandRepository.save(requestDto.toEntity());
        brand.updateCategory(category);
        brand.updateBrandIcon(brandIcon);

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
                .orElseThrow(() -> new EntityNotFoundException(Brand.class));

        Long categoryId = requestDto.getCategoryId();
        String newBrandName = requestDto.getName();
        MultipartFile newIconFile = requestDto.getIconFile();

        if (categoryId != null) {
            Category newCategory = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException(Category.class));

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
                .orElseThrow(() -> new EntityNotFoundException(Brand.class));

        voucherKindRepository.deleteAllByBrand(brand);
        brandRepository.delete(brand);
    }
}