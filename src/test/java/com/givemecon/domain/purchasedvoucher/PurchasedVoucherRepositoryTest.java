package com.givemecon.domain.purchasedvoucher;

import com.givemecon.domain.image.voucherkind.VoucherKindImage;
import com.givemecon.domain.image.voucherkind.VoucherKindImageRepository;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucherkind.VoucherKind;
import com.givemecon.domain.voucherkind.VoucherKindRepository;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.domain.voucher.VoucherStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.givemecon.config.enums.Authority.*;
import static com.givemecon.domain.purchasedvoucher.PurchasedVoucherStatus.*;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class PurchasedVoucherRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    VoucherKindRepository voucherKindRepository;

    @Autowired
    VoucherKindImageRepository voucherKindImageRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    PurchasedVoucherRepository purchasedVoucherRepository;

    Member member;

    Voucher voucher;

    @BeforeEach
    void setup() {
        member = memberRepository.save(Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .authority(USER)
                .build());

        VoucherKind voucherKind = voucherKindRepository.save(VoucherKind.builder()
                .title("voucherKind")
                .build());

        VoucherKindImage voucherKindImage = voucherKindImageRepository.save(VoucherKindImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("originalName")
                .build());

        voucher = voucherRepository.save(Voucher.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now())
                .build());

        voucherKind.updateVoucherKindImage(voucherKindImage);
        voucher.updateVoucherKind(voucherKind);
    }

    @Test
    void saveAndFindAll() {
        // given
        PurchasedVoucher purchasedVoucher = new PurchasedVoucher(voucher, member);

        // when
        purchasedVoucherRepository.save(purchasedVoucher);
        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();

        // then
        PurchasedVoucher found = purchasedVoucherList.get(0);
        assertThat(found.getStatus()).isEqualTo(USABLE);
        assertThat(found.getOwner()).isEqualTo(member);
        assertThat(found.getVoucher()).isEqualTo(voucher);
    }

    @Test
    void findByVoucher() {
        // given
        PurchasedVoucher saved = purchasedVoucherRepository.save(new PurchasedVoucher(voucher, member));

        // when
        Optional<PurchasedVoucher> found = purchasedVoucherRepository.findByVoucher(voucher);

        // then
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get()).isEqualTo(saved);
        assertThat(found.get().getVoucher()).isEqualTo(voucher);
    }

    @Test
    @DisplayName("PurchasedVoucher fetch join 단일 조회 테스트")
    void findOneFetchedByIdAndUsername() {
        // given
        PurchasedVoucher saved =
                purchasedVoucherRepository.save(new PurchasedVoucher(voucher, member));

        // when
        Optional<PurchasedVoucher> result =
                purchasedVoucherRepository.findOneFetchedByIdAndUsername(saved.getId(), member.getUsername());

        // then
        assertThat(result).isPresent();

        PurchasedVoucher found = result.get();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getVoucher()).isEqualTo(voucher);
        assertThat(found.getOwner()).isEqualTo(member);
    }

    @Test
    @DisplayName("PurchasedVoucher fetch join 전체 조회 테스트")
    void findAllFetchedByUsername() {
        // given
        PurchasedVoucher saved =
                purchasedVoucherRepository.save(new PurchasedVoucher(voucher, member));

        // when
        List<PurchasedVoucher> result = purchasedVoucherRepository.findAllFetchedByUsername(member.getUsername());

        // then
        assertThat(result).isNotEmpty();

        PurchasedVoucher found = result.get(0);
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getVoucher()).isEqualTo(voucher);
        assertThat(found.getOwner()).isEqualTo(member);
    }

    @Test
    @DisplayName("PurchasedVoucher의 VoucherForSale의 state가 EXPIRED일 경우, 해당 PurchasedVoucher도 EXPIRED로 변경한다.")
    void updateAllStatusForExpired() {
        // given
        voucher.updateStatus(VoucherStatus.EXPIRED);
        PurchasedVoucher saved = purchasedVoucherRepository.save(new PurchasedVoucher(voucher, member));

        // when
        int numModified = purchasedVoucherRepository.updateAllStatusForExpired();

        // then
        assertThat(numModified).isEqualTo(1);

        Optional<PurchasedVoucher> found = purchasedVoucherRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(PurchasedVoucherStatus.EXPIRED);
    }

    @Test
    @DisplayName("VoucherForSale의 status가 EXPIRED가 아닌 PurchasedVoucher는 status를 변경하지 않는다.")
    void ignoreVoucherForSaleNotExpired() {
        // given
        voucher.updateStatus(VoucherStatus.FOR_SALE);
        PurchasedVoucher saved = purchasedVoucherRepository.save(new PurchasedVoucher(voucher, member));

        // when
        int numModified = purchasedVoucherRepository.updateAllStatusForExpired();

        // then
        assertThat(numModified).isEqualTo(0);

        Optional<PurchasedVoucher> found = purchasedVoucherRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isNotEqualTo(PurchasedVoucherStatus.EXPIRED);
    }

    @Test
    @DisplayName("PurchasedVoucher의 status가 USABLE일 경우에만 status를 변경한다.")
    void ignoreStatusNotUsable() {
        // given
        voucher.updateStatus(VoucherStatus.FOR_SALE);
        PurchasedVoucher saved = purchasedVoucherRepository.save(new PurchasedVoucher(voucher, member));
        saved.updateStatus(USED);

        // when
        int numModified = purchasedVoucherRepository.updateAllStatusForExpired();

        // then
        assertThat(numModified).isEqualTo(0);

        Optional<PurchasedVoucher> found1 = purchasedVoucherRepository.findById(saved.getId());
        assertThat(found1).isPresent();
        assertThat(found1.get().getStatus()).isNotEqualTo(PurchasedVoucherStatus.EXPIRED);
    }

    @Test
    void BaseTimeEntity() {
        // given
        LocalDateTime now = LocalDateTime.now();
        PurchasedVoucher purchasedVoucher = new PurchasedVoucher(voucher, member);

        // when
        purchasedVoucherRepository.save(purchasedVoucher);
        List<PurchasedVoucher> purchasedVoucherList = purchasedVoucherRepository.findAll();

        // then
        PurchasedVoucher found = purchasedVoucherList.get(0);
        assertThat(found.getCreatedDate()).isAfterOrEqualTo(now);
        assertThat(found.getModifiedDate()).isAfterOrEqualTo(now);
    }
}