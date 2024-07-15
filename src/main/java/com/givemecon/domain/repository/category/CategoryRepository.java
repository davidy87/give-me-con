package com.givemecon.domain.repository.category;

import com.givemecon.domain.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select c from Category c join fetch c.categoryIcon")
    List<Category> findAllWithCategoryIcon();
}
