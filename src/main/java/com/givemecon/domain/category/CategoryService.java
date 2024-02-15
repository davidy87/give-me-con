package com.givemecon.domain.category;

import com.givemecon.domain.image_entity_util.ImageEntityUtils;
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

import static com.givemecon.domain.category.CategoryDto.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryIconRepository categoryIconRepository;

    private final ImageEntityUtils imageEntityUtils;

    public CategoryResponse save(CategorySaveRequest requestDto) {
        MultipartFile iconFile = requestDto.getIconFile();

        Category category = categoryRepository.save(requestDto.toEntity());
        CategoryIcon categoryIcon = categoryIconRepository.save(
                (CategoryIcon) imageEntityUtils.createImageEntity(CategoryIcon.class, iconFile));

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

        if (StringUtils.hasText(newCategoryName)) {
            category.updateName(newCategoryName);
        }

        if (FileUtils.isValidFile(newIconFile)) {
            CategoryIcon categoryIcon = category.getCategoryIcon();
            imageEntityUtils.updateImageEntity(categoryIcon, newIconFile);
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
