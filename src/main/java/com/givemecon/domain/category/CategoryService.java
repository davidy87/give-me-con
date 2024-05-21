package com.givemecon.domain.category;

import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.image.category.CategoryIcon;
import com.givemecon.domain.image.category.CategoryIconRepository;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.util.image_entity.ImageEntityUtils;
import com.givemecon.util.FileUtils;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.givemecon.domain.category.CategoryDto.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryIconRepository categoryIconRepository;

    private final BrandRepository brandRepository;

    private final VoucherRepository voucherRepository;

    private final ImageEntityUtils imageEntityUtils;

    public CategoryResponse save(CategorySaveRequest requestDto) {
        CategoryIcon categoryIcon = categoryIconRepository.save(
                imageEntityUtils.createImageEntity(CategoryIcon.class, requestDto.getIconFile()));

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
                .orElseThrow(() -> new EntityNotFoundException(Category.class));

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
                .orElseThrow(() -> new EntityNotFoundException(Category.class));

        brandRepository.findAllByCategory(category)
                .forEach(voucherRepository::deleteAllByBrand);
        brandRepository.deleteAllByCategory(category);
        categoryRepository.delete(category);
    }
}
