package com.givemecon.application.service;

import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.entity.voucherkind.VoucherKindImage;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import com.givemecon.domain.repository.voucherkind.VoucherKindRepository;
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

import static com.givemecon.application.dto.VoucherKindDto.VoucherKindResponse;
import static com.givemecon.domain.entity.voucher.VoucherStatus.FOR_SALE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class VoucherKindServiceTest {

    @Mock
    VoucherKindRepository voucherKindRepository;

    @Mock
    VoucherRepository voucherRepository;

    @InjectMocks
    VoucherKindService voucherKindService;

    VoucherKind voucherKind;

    @BeforeEach
    void setup() {
        VoucherKindImage voucherKindImage = VoucherKindImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("voucherKindImage")
                .build();

        voucherKind = VoucherKind.builder()
                .voucherKindImage(voucherKindImage)
                .build();

        Mockito.when(voucherKindRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(voucherKind));

    }

    @Test
    @DisplayName("VoucherKind 최소 가격 테스트 1")
    void minPrice() {
        // given
        Voucher voucher = Voucher.builder()
                .price(4_000L)
                .voucherKind(voucherKind)
                .build();

        voucher.updateStatus(FOR_SALE);

        Mockito.when(voucherRepository.findOneWithMinPrice(eq(voucherKind), eq(FOR_SALE), any(Pageable.class)))
                .thenReturn(List.of(voucher));

        // when
        VoucherKindResponse voucherKindResponse = voucherKindService.find(1L);

        // then
        Assertions.assertThat(voucherKindResponse.getMinPrice()).isEqualTo(voucher.getPrice());
    }

    @Test
    @DisplayName("VoucherKind 최소 가격 테스트 2 - 최소 가격 조회 시 결과가 없다면 0으로 설정한다.")
    void minPriceNotForSale() {
        Mockito.when(voucherRepository.findOneWithMinPrice(eq(voucherKind), eq(FOR_SALE), any(Pageable.class)))
                .thenReturn(List.of());

        // when
        VoucherKindResponse voucherKindResponse = voucherKindService.find(1L);

        // then
        Assertions.assertThat(voucherKindResponse.getMinPrice()).isEqualTo(0L);
    }
}