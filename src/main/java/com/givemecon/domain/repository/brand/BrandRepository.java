package com.givemecon.domain.repository.brand;

import com.givemecon.domain.entity.brand.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByName(String name);

    @Query("select b from Brand b join fetch b.brandIcon")
    List<Brand> findAllWithBrandIcon();

    @Query("select b from Brand b join fetch b.brandIcon where b.category.id = :categoryId")
    List<Brand> findAllWithBrandIconByCategoryId(Long categoryId);
}
