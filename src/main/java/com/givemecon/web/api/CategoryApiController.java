package com.givemecon.web.api;

import com.givemecon.domain.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.givemecon.web.dto.CategoryDto.*;

@RequiredArgsConstructor
@RequestMapping("/api/categories")
@RestController
public class CategoryApiController {

    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryResponse save(@RequestBody CategorySaveRequest requestDto) {
        return categoryService.save(requestDto);
    }

    @GetMapping
    public List<CategoryResponse> findAll() {
        return categoryService.findAll();
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id,
                                   @RequestBody CategoryUpdateRequest requestDto) {

        return categoryService.update(id, requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
