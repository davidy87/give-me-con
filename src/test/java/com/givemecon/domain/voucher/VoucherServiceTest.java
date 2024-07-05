package com.givemecon.domain.voucher;

import com.givemecon.domain.image.voucher.VoucherImage;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.givemecon.domain.voucher.VoucherDto.*;
import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class VoucherServiceTest {

    @Mock
    VoucherRepository voucherRepository;

    @InjectMocks
    VoucherService voucherService;

    Voucher voucher;

    @BeforeEach
    void setup() {
        voucher = Voucher.builder().build();
        VoucherImage voucherImage = VoucherImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("voucherImage")
                .build();

        voucher.updateVoucherImage(voucherImage);

        Mockito.when(voucherRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(voucher));
    }

    @Test
    @DisplayName("Voucher 최소 가격 테스트 1")
    void minPrice() {
        // given
        for (int i = 1; i <= 5; i++) {
            VoucherForSale voucherForSale = VoucherForSale.builder()
                    .price(4_000L)
                    .build();

            voucherForSale.updateStatus(FOR_SALE);
            voucher.addVoucherForSale(voucherForSale);
        }

        // when
        VoucherResponse voucherResponse = voucherService.find(1L);

        // then
        Assertions.assertThat(voucherResponse.getMinPrice()).isEqualTo(4_000L);
    }

    @Test
    @DisplayName("Voucher 최소 가격 테스트 2 - VoucherForSale의 상태가 FOR_SALE이 아니라면 해당 가격은 최소 가격 계산에 포함되지 않는다.")
    void minPriceNotForSale() {
        VoucherForSale voucherForSale = VoucherForSale.builder()
                .price(4_000L)
                .build();

        voucherForSale.updateStatus(SOLD);
        voucher.addVoucherForSale(voucherForSale);

        // when
        VoucherResponse voucherResponse = voucherService.find(1L);

        // then
        Assertions.assertThat(voucherResponse.getMinPrice()).isEqualTo(0L);
    }
}