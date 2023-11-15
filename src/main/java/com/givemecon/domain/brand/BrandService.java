package com.givemecon.domain.brand;

import com.givemecon.util.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.util.error.ErrorCode.*;
import java.util.List;

import static com.givemecon.web.dto.BrandDto.*;

@RequiredArgsConstructor
@Service
@Transactional
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandResponse save(BrandSaveRequest requestDto) {
        Brand brand = brandRepository.save(requestDto.toEntity());
        return new BrandResponse(brand);
    }

    @Transactional(readOnly = true)
    public List<BrandResponse> findAll() {
        return brandRepository.findAll()
                .stream()
                .map(BrandResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BrandResponse> findAllByCategoryId(Long categoryId) {
        return brandRepository.findAll()
                .stream()
                .filter(brand -> brand.getCategory().getId() == categoryId)
                .map(BrandResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public BrandResponse find(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        return new BrandResponse(brand);
    }

    public BrandResponse update(Long id, BrandUpdateRequest requestDto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        brand.update(requestDto.getName(), requestDto.getIcon());

        return new BrandResponse(brand);
    }

    public Long delete(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        brandRepository.delete(brand);

        return id;
    }
}
