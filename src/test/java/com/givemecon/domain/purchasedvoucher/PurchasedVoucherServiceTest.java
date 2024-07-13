package com.givemecon.domain.purchasedvoucher;

import com.givemecon.domain.image.voucher.VoucherImage;
import com.givemecon.domain.image.voucherkind.VoucherKindImage;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucherkind.VoucherKind;
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

import static com.givemecon.domain.purchasedvoucher.PurchasedVoucherDto.*;
import static com.givemecon.domain.purchasedvoucher.PurchasedVoucherStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PurchasedVoucherServiceTest {

    @Mock
    PurchasedVoucherRepository purchasedVoucherRepository;

    @InjectMocks
    PurchasedVoucherService purchasedVoucherService;

    Voucher voucher;

    @Mock
    PurchasedVoucher purchasedVoucher;

    @BeforeEach
    void setup() {
        VoucherKind voucherKind = VoucherKind.builder()
                .title("voucherKind")
                .build();

        VoucherKindImage voucherKindImage = VoucherKindImage.builder()
                .imageUrl("voucherImageUrl")
                .build();

        voucherKind.updateVoucherKindImage(voucherKindImage);

        VoucherImage voucherImage = VoucherImage.builder()
                .imageUrl("imageUrl")
                .build();

        voucher = Voucher.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now())
                .build();

        voucher.updateVoucherKind(voucherKind);
        voucher.updateVoucherImage(voucherImage);
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
        assertThat(response.get(0).getBarcode()).isEqualTo(voucher.getBarcode());
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