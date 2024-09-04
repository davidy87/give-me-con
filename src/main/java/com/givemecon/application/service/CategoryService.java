package com.givemecon.application.service;

import com.givemecon.application.exception.InvalidRequestFieldException;
import com.givemecon.common.util.FileUtils;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.category.CategoryIcon;
import com.givemecon.domain.repository.category.CategoryIconRepository;
import com.givemecon.domain.repository.category.CategoryRepository;
import com.givemecon.infrastructure.s3.image_entity.ImageEntityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.givemecon.application.dto.CategoryDto.*;
import static com.givemecon.application.exception.errorcode.CategoryErrorCode.INVALID_CATEGORY_ID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryIconRepository categoryIconRepository;

    private final ImageEntityUtils imageEntityUtils;

    public CategoryResponse save(CategorySaveRequest requestDto) {
        CategoryIcon categoryIcon = categoryIconRepository.save(
                imageEntityUtils.createImageEntity(CategoryIcon.class, requestDto.getIconFile()));

        Category category = categoryRepository.save(Category.builder()
                .name(requestDto.getName())
                .categoryIcon(categoryIcon)
                .build());

        return new CategoryResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAllWithCategoryIcon().stream()
                .map(CategoryResponse::new)
                .toList();
    }

    public CategoryResponse update(Long id, CategoryUpdateRequest requestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_CATEGORY_ID));

        String newCategoryName = requestDto.getName();
        MultipartFile newIconFile = requestDto.getIconFile();

        if (StringUtils.hasText(newCategoryName)) {
            category.updateName(newCategoryName);
        }

        if (FileUtils.isFileValid(newIconFile)) {
            imageEntityUtils.updateImageEntity(category.getCategoryIcon(), newIconFile);
        }

        return new CategoryResponse(category);
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new InvalidRequestFieldException(INVALID_CATEGORY_ID));

        categoryRepository.delete(category);
    }
}
