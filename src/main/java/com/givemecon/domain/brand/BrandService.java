package com.givemecon.domain.brand;

import com.givemecon.domain.AwsS3Service;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.category.CategoryRepository;
import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import static com.givemecon.util.error.ErrorCode.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
        Brand brand = brandRepository.save(requestDto.toEntity());
        MultipartFile iconFile = requestDto.getIcon();

        try {
            String originalName = iconFile.getOriginalFilename();
            String imageKey = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(originalName);
            String imageUrl = awsS3Service.upload(imageKey, iconFile.getInputStream());
            BrandIcon brandIcon = brandIconRepository.save(BrandIcon.builder()
                    .imageKey(imageKey)
                    .originalName(originalName)
                    .imageUrl(imageUrl)
                    .build());

            brand.setBrandIcon(brandIcon);
        } catch (IOException e) {
            throw new RuntimeException("브랜드 아이콘 업로드 실패"); // TODO: 예외 처리
        }

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

        brand.updateName(requestDto.getName());
        BrandIcon brandIcon = brand.getBrandIcon();
        MultipartFile newIconFile = requestDto.getIcon();

        try {
            String newImageUrl = awsS3Service.upload(brandIcon.getImageKey(), newIconFile.getInputStream());
            String newOriginalName = newIconFile.getOriginalFilename();
            brandIcon.update(newImageUrl, newOriginalName);
        } catch (IOException e) {
            throw new RuntimeException("브랜드 아이콘 업로드 실패"); // TODO: 예외 처리
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
