package com.givemecon.domain.brand;

import com.givemecon.domain.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByName(String name);

    List<Brand> findAllByCategory(Category category);

    Page<Brand> findPageByCategory(Category category, Pageable pageable);

    void deleteAllByCategory(Category category);
}
