package com.givemecon.domain.brand;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<BrandResponse> findAll() {
        return brandRepository.findAll()
                .stream()
                .map(brand -> BrandResponse.builder()
                        .name(brand.getName())
                        .icon(brand.getIcon())
                        .build())
                .toList();
    }


    @Transactional(readOnly = true)
    public BrandResponse find(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(RuntimeException::new); // TODO: 예외 처리

        return BrandResponse.builder()
                .name(brand.getName())
                .icon(brand.getIcon())
                .build();
    }

    public BrandResponse update(Long id, BrandUpdateRequest requestDto) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(RuntimeException::new); // TODO: 예외 처리

        brand.update(requestDto.getName(), requestDto.getIcon());

        return BrandResponse.builder()
                .name(brand.getName())
                .icon(brand.getIcon())
                .build();
    }

    public Long delete(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(RuntimeException::new); // TODO: 예외 처리

        brandRepository.delete(brand);

        return id;
    }
}
