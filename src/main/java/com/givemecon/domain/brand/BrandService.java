package com.givemecon.domain.brand;

import com.givemecon.domain.AwsS3Service;
import com.givemecon.util.FileUtils;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.category.CategoryRepository;
import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.givemecon.util.error.ErrorCode.*;

import java.util.List;

import static com.givemecon.web.dto.BrandDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class BrandService {

    private final CategoryRepository categoryRepository;

    private final BrandRepository brandRepository;

    private final BrandIconRepository brandIconRepository;

    private final AwsS3Service awsS3Service;

    public BrandResponse save(BrandSaveRequest requestDto) {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        MultipartFile iconFile = requestDto.getIconFile();
        String originalName = iconFile.getOriginalFilename();
        String imageKey = FileUtils.convertFilenameToKey(originalName);
        String imageUrl = awsS3Service.upload(imageKey, iconFile);

        BrandIcon brandIcon = brandIconRepository.save(
                BrandIcon.builder()
                        .imageKey(imageKey)
                        .originalName(originalName)
                        .imageUrl(imageUrl)
                        .build());

        Brand brand = brandRepository.save(requestDto.toEntity());
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
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        return category.getBrandList().stream()
                .map(BrandResponse::new)
                .toList();
    }

    public BrandResponse update(Long id, BrandUpdateRequest requestDto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        Long categoryId = requestDto.getCategoryId();
        String newBrandName = requestDto.getName();
        MultipartFile newIconFile = requestDto.getIconFile();

        if (categoryId != null) {
            Category newCategory = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

            newCategory.addBrand(brand);
        }

        if (newBrandName != null) {
            brand.updateName(newBrandName);
        }

        if (FileUtils.isValidFile(newIconFile)) {
            BrandIcon brandIcon = brand.getBrandIcon();
            String imageKey = brandIcon.getImageKey();
            String newImageUrl = awsS3Service.upload(imageKey, newIconFile);
            String newOriginalName = newIconFile.getOriginalFilename();
            brandIcon.update(newImageUrl, newOriginalName);
        }

        return new BrandResponse(brand);
    }

    public Long delete(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        brandRepository.delete(brand);

        return id;
    }
}
