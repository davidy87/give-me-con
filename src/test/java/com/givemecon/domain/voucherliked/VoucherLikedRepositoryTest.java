package com.givemecon.domain.voucherliked;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class VoucherLikedRepositoryTest {

    @Autowired
    VoucherLikedRepository voucherLikedRepository;

    @Test
    void saveAndFindAll() {
        // given
        VoucherLiked voucherLiked = new VoucherLiked();

        // when
        VoucherLiked saved = voucherLikedRepository.save(voucherLiked);
        List<VoucherLiked> voucherLikedList = voucherLikedRepository.findAll();

        // then
        VoucherLiked found = voucherLikedList.get(0);
        assertThat(found.getId()).isEqualTo(saved.getId());
    }

    @Test
    void BaseTimeEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        voucherLikedRepository.save(new VoucherLiked());

        // when
        List<VoucherLiked> voucherLikedList = voucherLikedRepository.findAll();

        // then
        VoucherLiked found = voucherLikedList.get(0);
        assertThat(found.getCreatedDate()).isAfter(now);
        assertThat(found.getModifiedDate()).isAfter(now);
    }
}