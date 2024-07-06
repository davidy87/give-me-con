package com.givemecon.domain.brand;

import com.givemecon.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByName(String name);

    List<Brand> findAllByCategory(Category category);

    @Query("select b from Brand b join fetch b.brandIcon")
    List<Brand> findAllWithBrandIcon();

    @Query("select b from Brand b join fetch b.brandIcon where b.category.id = :categoryId")
    List<Brand> findAllWithBrandIconByCategoryId(Long categoryId);

    void deleteAllByCategory(Category category);
}
