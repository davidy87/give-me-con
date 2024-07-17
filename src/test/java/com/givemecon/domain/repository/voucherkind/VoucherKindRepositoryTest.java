package com.givemecon.domain.repository.voucherkind;

import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.brand.BrandIcon;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.category.CategoryIcon;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.entity.voucherkind.VoucherKindImage;
import com.givemecon.domain.repository.brand.BrandIconRepository;
import com.givemecon.domain.repository.brand.BrandRepository;
import com.givemecon.domain.repository.category.CategoryIconRepository;
import com.givemecon.domain.repository.category.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@SpringBootTest
class VoucherKindRepositoryTest {

    @Autowired
    VoucherKindRepository voucherKindRepository;

    @Test
    void BaseTimeEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        voucherKindRepository.save(VoucherKind.builder()
                .title("Cake")
                .build());

        // when
        List<VoucherKind> voucherKindList = voucherKindRepository.findAll();

        // then
        VoucherKind found = voucherKindList.get(0);
        log.info(">>>>>>> createDate={}, modifiedDate={}", found.getCreatedDate(), found.getModifiedDate());
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }

    @Test
    void saveAndFindAll() {
        // given
        String title = "Americano T";
        String description = "This is Americano T";
        String caution = "This is hot, not iced";

        VoucherKind voucherKind = VoucherKind.builder()
                .title(title)
                .description(description)
                .caution(caution)
                .build();

        // when
        voucherKindRepository.save(voucherKind);
        List<VoucherKind> voucherKindList = voucherKindRepository.findAll();

        // then
        VoucherKind found = voucherKindList.get(0);
        assertThat(found.getTitle()).isEqualTo(title);
        assertThat(found.getDescription()).isEqualTo(description);
        assertThat(found.getCaution()).isEqualTo(caution);
    }

    @Test
    void findAllWithImageByBrandId(@Autowired CategoryRepository categoryRepository,
                                   @Autowired CategoryIconRepository categoryIconRepository,
                                   @Autowired BrandRepository brandRepository,
                                   @Autowired BrandIconRepository brandIconRepository,
                                   @Autowired VoucherKindImageRepository voucherKindImageRepository) {

        // given
        CategoryIcon categoryIcon = categoryIconRepository.save(CategoryIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("categoryIcon")
                .build());

        Category category = categoryRepository.save(Category.builder()
                .name("category")
                .categoryIcon(categoryIcon)
                .build());

        BrandIcon brandIcon = brandIconRepository.save(BrandIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("brandIcon")
                .build());

        Brand brand = brandRepository.save(Brand.builder()
                .name("Starbucks")
                .brandIcon(brandIcon)
                .category(category)
                .build());

        VoucherKind voucherKind = VoucherKind.builder()
                .title("Americano T")
                .description("This is Americano T")
                .caution("This is hot, not iced")
                .build();

        VoucherKindImage voucherKindImage = VoucherKindImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("originalName")
                .build();

        voucherKind.updateBrand(brand);
        voucherKind.updateVoucherKindImage(voucherKindImage);
        voucherKindImageRepository.save(voucherKindImage);
        voucherKindRepository.save(voucherKind);

        // when
        List<VoucherKind> result = voucherKindRepository.findAllWithImageByBrandId(brand.getId());

        // then
        assertThat(result).isNotEmpty();

        VoucherKind voucherKindFound = result.get(0);
        assertThat(voucherKindFound).isEqualTo(voucherKind);
        assertThat(voucherKindFound.getBrand()).isEqualTo(brand);
        assertThat(voucherKindFound.getVoucherKindImage()).isEqualTo(voucherKindImage);
    }
}