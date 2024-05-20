package com.givemecon.domain.brand;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByName(String name);

    Page<Brand> findPageByCategoryId(Long categoryId, Pageable pageable);

    void deleteAllByCategoryId(Long categoryId);
}
