package com.givemecon.domain.repository;

import com.givemecon.domain.entity.member.Role;
import com.givemecon.domain.entity.likedvoucher.LikedVoucher;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.entity.voucherkind.VoucherKindImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LikedVoucherRepositoryTest extends RepositoryTest {

    @Test
    void saveAndFindAll() {
        // given
        LikedVoucher likedVoucher = LikedVoucher.builder().build();

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
        LikedVoucher likedVoucher = LikedVoucher.builder().build();
        likedVoucherRepository.save(likedVoucher);

        // when
        List<LikedVoucher> likedVoucherList = likedVoucherRepository.findAll();

        // then
        LikedVoucher found = likedVoucherList.get(0);
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }

    @Nested
    @DisplayName("JPQL 테스트")
    class JPQLTest {

        @Test
        void findAllFetchedByUsername() {
            // given
            Member member = memberRepository.save(Member.builder()
                    .username("tester")
                    .email("tester@gmail.com")
                    .role(Role.USER)
                    .build());

            VoucherKindImage voucherKindImage = VoucherKindImage.builder()
                    .imageKey("imageKey")
                    .imageUrl("imageUrl")
                    .originalName("originalName")
                    .build();

            VoucherKind voucherKind = VoucherKind.builder()
                    .title("voucherKind")
                    .voucherKindImage(voucherKindImage)
                    .build();

            voucherKindRepository.save(voucherKind);
            voucherKindImageRepository.save(voucherKindImage);

            LikedVoucher likedVoucher = new LikedVoucher(member, voucherKind);
            likedVoucherRepository.save(likedVoucher);

            // when
            List<LikedVoucher> result = likedVoucherRepository.findAllFetchedByMember(member);

            // then
            assertThat(result).hasSize(1);

            LikedVoucher found = result.get(0);
            assertThat(found.getMember()).isEqualTo(member);
            assertThat(found.getVoucherKind()).isEqualTo(voucherKind);
        }
    }
}