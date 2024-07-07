package com.givemecon.domain.voucher;

import com.givemecon.domain.brand.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    @Query("select v from Voucher v join fetch v.voucherImage where v.brand.id = :brandId")
    List<Voucher> findAllWithVoucherImageByBrandId(Long brandId);

    Page<Voucher> findPageByBrand(Brand brand, Pageable pageable);

    void deleteAllByBrand(Brand brand);
}
