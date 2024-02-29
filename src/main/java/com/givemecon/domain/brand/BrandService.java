package com.givemecon.domain.brand;

import com.givemecon.domain.image.brand.BrandIcon;
import com.givemecon.domain.image.brand.BrandIconRepository;
import com.givemecon.util.image_entity.ImageEntityUtils;
import com.givemecon.util.FileUtils;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.category.CategoryRepository;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.givemecon.domain.brand.BrandDto.*;

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
                .orElseThrow(() -> new EntityNotFoundException(Category.class));

        MultipartFile iconFile = requestDto.getIconFile();

        Brand brand = brandRepository.save(requestDto.toEntity());
        BrandIcon brandIcon = brandIconRepository.save(
                imageEntityUtils.createImageEntity(BrandIcon.class, iconFile));

        brand.updateBrandIcon(brandIcon);
        brand.updateCategory(category);

        return new BrandResponse(brand);
    }

    @Transactional(readOnly = true)
    public List<BrandResponse> findAll() {
        return brandRepository.findAll()
                .stream()
                .map(BrandResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BrandResponse> findAllByCategoryId(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(Category.class));

        return category.getBrandList().stream()
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

            newCategory.addBrand(brand);
        }

        if (StringUtils.hasText(newBrandName)) {
            brand.updateName(newBrandName);
        }

        if (FileUtils.isValidFile(newIconFile)) {
            BrandIcon brandIcon = brand.getBrandIcon();
            imageEntityUtils.updateImageEntity(brandIcon, newIconFile);
        }

        return new BrandResponse(brand);
    }

    public Long delete(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Brand.class));

        brandRepository.delete(brand);

        return id;
    }
}
