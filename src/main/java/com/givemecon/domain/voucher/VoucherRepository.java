package com.givemecon.domain.voucher;

import com.givemecon.domain.brand.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    Page<Voucher> findPageByBrand(Brand brand, Pageable pageable);

    void deleteAllByBrandId(Long brandId);
}
