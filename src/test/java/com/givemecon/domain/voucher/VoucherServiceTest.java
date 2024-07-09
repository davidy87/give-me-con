package com.givemecon.domain.voucher;

import com.givemecon.domain.image.voucher.VoucherImage;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.givemecon.domain.voucher.VoucherDto.*;
import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class VoucherServiceTest {

    @Mock
    VoucherRepository voucherRepository;

    @Mock
    VoucherForSaleRepository voucherForSaleRepository;

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
        VoucherForSale voucherForSale = VoucherForSale.builder()
                .price(4_000L)
                .build();

        voucherForSale.updateStatus(FOR_SALE);
        voucherForSale.updateVoucher(voucher);

        Mockito.when(voucherForSaleRepository.findOneWithMinPrice(eq(voucher), eq(FOR_SALE), any(Pageable.class)))
                .thenReturn(List.of(voucherForSale));

        // when
        VoucherResponse voucherResponse = voucherService.find(1L);

        // then
        Assertions.assertThat(voucherResponse.getMinPrice()).isEqualTo(voucherForSale.getPrice());
    }

    @Test
    @DisplayName("Voucher 최소 가격 테스트 2 - 최소 가격 조회 시 결과가 없다면 0으로 설정한다.")
    void minPriceNotForSale() {
        Mockito.when(voucherForSaleRepository.findOneWithMinPrice(eq(voucher), eq(FOR_SALE), any(Pageable.class)))
                .thenReturn(List.of());

        // when
        VoucherResponse voucherResponse = voucherService.find(1L);

        // then
        Assertions.assertThat(voucherResponse.getMinPrice()).isEqualTo(0L);
    }
}