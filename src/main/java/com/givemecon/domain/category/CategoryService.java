package com.givemecon.domain.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.givemecon.web.dto.CategoryDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse save(CategorySaveRequest requestDto) {
        Category category = categoryRepository.save(requestDto.toEntity());

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .icon(category.getIcon())
                .build();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .icon(category.getIcon())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse find(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(RuntimeException::new); // TODO: 예외 처리

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .icon(category.getIcon())
                .build();
    }

    public CategoryResponse update(Long id, CategoryUpdateRequest requestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(RuntimeException::new); // TODO: 예외 처리

        category.update(requestDto.getName(), requestDto.getIcon());

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .icon(category.getIcon())
                .build();
    }

    public Long delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(RuntimeException::new); // TODO: 예외 처리

        categoryRepository.delete(category);

        return id;
    }
}
