package com.givemecon.application.service;

import com.givemecon.domain.entity.purchasedvoucher.PurchasedVoucher;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucher.VoucherImage;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.entity.voucherkind.VoucherKindImage;
import com.givemecon.domain.repository.PurchasedVoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.givemecon.application.dto.PurchasedVoucherDto.PurchasedVoucherResponse;
import static com.givemecon.application.dto.PurchasedVoucherDto.StatusUpdateResponse;
import static com.givemecon.domain.entity.purchasedvoucher.PurchasedVoucherStatus.EXPIRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PurchasedVoucherServiceTest {

    @Mock
    PurchasedVoucherRepository purchasedVoucherRepository;

    @Mock
    PurchasedVoucher purchasedVoucher;

    @InjectMocks
    PurchasedVoucherService purchasedVoucherService;

    Voucher voucher;

    @BeforeEach
    void setup() {
        VoucherKindImage voucherKindImage = VoucherKindImage.builder()
                .imageUrl("voucherKindImageUrl")
                .build();

        VoucherKind voucherKind = VoucherKind.builder()
                .title("voucherKind")
                .voucherKindImage(voucherKindImage)
                .build();

        VoucherImage voucherImage = VoucherImage.builder()
                .imageUrl("imageUrl")
                .build();

        voucher = Voucher.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now())
                .voucherImage(voucherImage)
                .voucherKind(voucherKind)
                .build();
    }

    @Test
    @DisplayName("회원 닉네임별 구매한 기프티콘 조회 테스트")
    void findAllByUsername() {
        // given
        String username = "tester";

        Mockito.when(purchasedVoucherRepository.findAllFetchedByUsername(any(String.class)))
                .thenReturn(List.of(purchasedVoucher));

        Mockito.when(purchasedVoucher.getVoucher()).thenReturn(voucher);

        // when
        List<PurchasedVoucherResponse> response = purchasedVoucherService.findAllByUsername(username);

        // then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getTitle()).isEqualTo(voucher.getTitle());
        assertThat(response.get(0).getPrice()).isEqualTo(voucher.getPrice());
        assertThat(response.get(0).getExpDate()).isEqualTo(voucher.getExpDate());
        assertThat(response.get(0).getVoucherKindImageUrl()).isEqualTo(voucher.getVoucherKind().getVoucherKindImage().getImageUrl());
        assertThat(response.get(0).getStatus()).isEqualTo(purchasedVoucher.getStatus());
    }

    @Test
    @DisplayName("PurchasedVoucher의 현재 상태가 USABLE이 아닌 경우, USED로 변경되지 않는다.")
    void setUsedOnNotUsable() {
        // given
        Mockito.when(purchasedVoucherRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(purchasedVoucher));

        Mockito.when(purchasedVoucher.getStatus()).thenReturn(EXPIRED);

        // when
        StatusUpdateResponse response = purchasedVoucherService.setUsed(1L);

        // then
        assertThat(response.getStatus()).isSameAs(EXPIRED);
    }
}