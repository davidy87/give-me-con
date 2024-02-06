package com.givemecon.domain.category;

import com.givemecon.domain.AwsS3Service;
import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import static com.givemecon.util.error.ErrorCode.*;

import java.io.IOException;
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
        Category category = categoryRepository.save(requestDto.toEntity());
        MultipartFile iconFile = requestDto.getIconFile();
        String originalName = iconFile.getOriginalFilename();
        String imageKey = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(originalName);

        try {
            String imageUrl = awsS3Service.upload(imageKey, iconFile.getInputStream());
            CategoryIcon categoryIcon = categoryIconRepository.save(CategoryIcon.builder()
                    .imageKey(imageKey)
                    .imageUrl(imageUrl)
                    .originalName(originalName)
                    .build());

            category.updateCategoryIcon(categoryIcon);
        } catch (IOException e) {
            throw new RuntimeException("카테고리 아이콘 업로드 실패."); // TODO: 예외 처리
        }

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

        if (newIconFile != null && !newIconFile.isEmpty()) {
            CategoryIcon categoryIcon = category.getCategoryIcon();

            try {
                String newImageUrl = awsS3Service.upload(categoryIcon.getImageKey(), newIconFile.getInputStream());
                String newOriginalName = newIconFile.getOriginalFilename();
                categoryIcon.update(newImageUrl, newOriginalName);
            } catch (IOException e) {
                throw new RuntimeException("카테고리 아이콘 업로드 실패."); // TODO: 예외 처리
            }
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
