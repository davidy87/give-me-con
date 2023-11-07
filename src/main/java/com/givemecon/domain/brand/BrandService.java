package com.givemecon.domain.brand;

import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.util.error.ErrorCode.*;
import static com.givemecon.web.dto.BrandDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandResponse save(BrandSaveRequest requestDto) {
        Brand brand = brandRepository.save(requestDto.toEntity());

        return BrandResponse.builder()
                .name(brand.getName())
                .icon(brand.getIcon())
                .build();
    }

    @Transactional(readOnly = true)
    public BrandResponse find(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        return BrandResponse.builder()
                .name(brand.getName())
                .icon(brand.getIcon())
                .build();
    }

    public BrandResponse update(Long id, BrandUpdateRequest requestDto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        brand.update(requestDto.getName(), requestDto.getIcon());

        return BrandResponse.builder()
                .name(brand.getName())
                .icon(brand.getIcon())
                .build();
    }

    public Long delete(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        brandRepository.delete(brand);

        return id;
    }
}
