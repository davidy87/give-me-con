package com.givemecon.domain.likedvoucher;

import com.givemecon.config.enums.Authority;
import com.givemecon.domain.image.voucherkind.VoucherKindImage;
import com.givemecon.domain.image.voucherkind.VoucherKindImageRepository;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucherkind.VoucherKind;
import com.givemecon.domain.voucherkind.VoucherKindRepository;
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
    void findAllFetchedByUsername(@Autowired MemberRepository memberRepository,
                                  @Autowired VoucherKindRepository voucherKindRepository,
                                  @Autowired VoucherKindImageRepository voucherKindImageRepository) {

        // given
        Member member = memberRepository.save(Member.builder()
                .username("tester")
                .email("tester@gmail.com")
                .authority(Authority.USER)
                .build());

        VoucherKind voucherKind = VoucherKind.builder()
                .title("voucherKind")
                .build();

        VoucherKindImage voucherKindImage = VoucherKindImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("originalName")
                .build();

        voucherKind.updateVoucherImage(voucherKindImage);
        voucherKindRepository.save(voucherKind);
        voucherKindImageRepository.save(voucherKindImage);

        LikedVoucher likedVoucher = new LikedVoucher(member, voucherKind);
        likedVoucherRepository.save(likedVoucher);

        // when
        List<LikedVoucher> result = likedVoucherRepository.findAllFetchedByUsername(member.getUsername());

        // then
        assertThat(result).hasSize(1);

        LikedVoucher found = result.get(0);
        assertThat(found.getMember()).isEqualTo(member);
        assertThat(found.getVoucherKind()).isEqualTo(voucherKind);
    }
}