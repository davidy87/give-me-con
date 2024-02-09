package com.givemecon.domain.category;

import com.givemecon.domain.AwsS3Service;
import com.givemecon.util.FileUtils;
import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import static com.givemecon.util.error.ErrorCode.*;

import java.util.List;
import java.util.UUID;

import static com.givemecon.web.dto.CategoryDto.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryIconRepository categoryIconRepository;

    private final AwsS3Service awsS3Service;

    public CategoryResponse save(CategorySaveRequest requestDto) {
        MultipartFile iconFile = requestDto.getIconFile();
        String originalName = iconFile.getOriginalFilename();
        String imageKey = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(originalName);
        String imageUrl = awsS3Service.upload(imageKey, iconFile);

        CategoryIcon categoryIcon = categoryIconRepository.save(
                CategoryIcon.builder()
                        .imageKey(imageKey)
                        .imageUrl(imageUrl)
                        .originalName(originalName)
                        .build());

        Category category = categoryRepository.save(requestDto.toEntity());
        category.updateCategoryIcon(categoryIcon);

        return new CategoryResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::new)
                .toList();
    }

    public CategoryResponse update(Long id, CategoryUpdateRequest requestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        String newCategoryName = requestDto.getName();
        MultipartFile newIconFile = requestDto.getIconFile();

        if (newCategoryName != null) {
            category.updateName(newCategoryName);
        }

        if (FileUtils.isValidFile(newIconFile)) {
            CategoryIcon categoryIcon = category.getCategoryIcon();
            String imageKey = categoryIcon.getImageKey();
            String newImageUrl = awsS3Service.upload(imageKey, newIconFile);
            String newOriginalName = newIconFile.getOriginalFilename();
            categoryIcon.update(newImageUrl, newOriginalName);
        }

        return new CategoryResponse(category);
    }

    public Long delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        categoryRepository.delete(category);

        return id;
    }
}
