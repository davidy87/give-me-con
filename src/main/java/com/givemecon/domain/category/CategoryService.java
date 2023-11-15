package com.givemecon.domain.category;

import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.util.error.ErrorCode.*;
import java.util.List;

import static com.givemecon.web.dto.CategoryDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse save(CategorySaveRequest requestDto) {
        Category category = categoryRepository.save(requestDto.toEntity());
        return new CategoryResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse find(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        return new CategoryResponse(category);
    }

    public CategoryResponse update(Long id, CategoryUpdateRequest requestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        category.update(requestDto.getName(), requestDto.getIcon());

        return new CategoryResponse(category);
    }

    public Long delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        categoryRepository.delete(category);

        return id;
    }
}
