package com.givemecon.domain.repository.voucherkind;

import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoucherKindRepository extends JpaRepository<VoucherKind, Long> {

    @Query("select vk from VoucherKind vk join fetch vk.voucherKindImage where vk.brand.id = :brandId")
    List<VoucherKind> findAllWithImageByBrandId(Long brandId);

    Page<VoucherKind> findPageByBrand(Brand brand, Pageable pageable);
}
