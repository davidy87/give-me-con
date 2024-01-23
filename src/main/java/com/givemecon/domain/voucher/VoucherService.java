package com.givemecon.domain.voucher;

import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.givemecon.util.error.ErrorCode.*;
import static com.givemecon.web.dto.VoucherDto.*;
import static com.givemecon.web.dto.VoucherForSaleDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class VoucherService {

    private final BrandRepository brandRepository;

    private final VoucherRepository voucherRepository;

    public VoucherResponse save(VoucherSaveRequest requestDto) {
        Voucher voucher = voucherRepository.save(requestDto.toEntity());
        return new VoucherResponse(voucher);
    }

    /**
     * VoucherForSale 생성 시, 해당 VoucherForSale의 title과 일치하는 Voucher entity가 없다면, Voucher 새로
     * 저장 후 VoucherForSale 등록
     * @param voucherForSale 생성된 VoucherForSale entity
     */
    public void saveIfNotExist(VoucherForSale voucherForSale) {
        Voucher voucher = voucherRepository.findByTitle(voucherForSale.getTitle())
                .orElse(Voucher.builder()
                        .title(voucherForSale.getTitle())
                        .price(voucherForSale.getPrice())
                        .build());

        Voucher voucherSaved = voucherRepository.save(voucher);
        voucherSaved.addVoucherForSale(voucherForSale);
    }

    @Transactional(readOnly = true)
    public VoucherResponse find(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        return new VoucherResponse(voucher);
    }

    @Transactional(readOnly = true)
    public List<VoucherResponse> findAll() {
        return voucherRepository.findAll().stream()
                .map(VoucherResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VoucherResponse> findAllByBrandName(String brandName) {
        Brand brand = brandRepository.findByName(brandName)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        return brand.getVoucherList().stream()
                .map(VoucherResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VoucherForSaleResponse> findSellingListByVoucherId(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        return voucher.getVoucherForSaleList().stream()
                .map(VoucherForSaleResponse::new)
                .toList();
    }

    public VoucherResponse update(Long id, VoucherUpdateRequest requestDto) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        voucher.update(requestDto.getPrice(), requestDto.getImage());

        return new VoucherResponse(voucher);
    }

    public Long delete(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        voucherRepository.delete(voucher);

        return id;
    }
}
