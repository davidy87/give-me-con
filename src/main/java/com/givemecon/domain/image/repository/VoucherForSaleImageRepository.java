package com.givemecon.domain.image.repository;

import com.givemecon.domain.image.entity.VoucherImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherForSaleImageRepository extends JpaRepository<VoucherImage, Long> {
}
