package com.givemecon.controller.admin;

import com.givemecon.application.dto.CategoryDto;
import com.givemecon.application.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
@RestController
public class AdminCategoryController {

    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CategoryDto.CategoryResponse save(@Validated @ModelAttribute CategoryDto.CategorySaveRequest requestDto) {
        return categoryService.save(requestDto);
    }

    @PostMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CategoryDto.CategoryResponse update(@PathVariable Long id,
                                               @ModelAttribute CategoryDto.CategoryUpdateRequest requestDto) {

        return categoryService.update(id, requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
