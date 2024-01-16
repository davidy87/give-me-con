package com.givemecon.web.api;

import com.givemecon.domain.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.givemecon.web.dto.CategoryDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/categories")
@RestController
public class CategoryApiController {

    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CategoryResponse save(@RequestPart String name, @RequestPart MultipartFile icon) {
        CategorySaveRequest requestDto = CategorySaveRequest.builder()
                .name(name)
                .icon(icon)
                .build();

        return categoryService.save(requestDto);
    }

    @GetMapping
    public List<CategoryResponse> findAll() {
        return categoryService.findAll();
    }

    @PostMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CategoryResponse update(@PathVariable Long id,
                                   @RequestPart(required = false) String name,
                                   @RequestPart(required = false) MultipartFile icon) {

        CategoryUpdateRequest requestDto = CategoryUpdateRequest.builder()
                .name(name)
                .icon(icon)
                .build();

        return categoryService.update(id, requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
