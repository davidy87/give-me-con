package com.givemecon.domain.brand;

import com.givemecon.domain.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByName(String name);

    Page<Brand> findPageByCategory(Category category, Pageable pageable);
}
