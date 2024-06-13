package com.givemecon.domain.likedvoucher;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class LikedVoucherRepositoryTest {

    @Autowired
    LikedVoucherRepository likedVoucherRepository;

    @Test
    void saveAndFindAll() {
        // given
        LikedVoucher likedVoucher = new LikedVoucher();

        // when
        LikedVoucher saved = likedVoucherRepository.save(likedVoucher);
        List<LikedVoucher> likedVoucherList = likedVoucherRepository.findAll();

        // then
        LikedVoucher found = likedVoucherList.get(0);
        assertThat(found.getId()).isEqualTo(saved.getId());
    }

    @Test
    void BaseTimeEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        likedVoucherRepository.save(new LikedVoucher());

        // when
        List<LikedVoucher> likedVoucherList = likedVoucherRepository.findAll();

        // then
        LikedVoucher found = likedVoucherList.get(0);
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }
}