package com.givemecon.domain.voucher;

import com.givemecon.domain.voucherforsale.VoucherForSale;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.givemecon.domain.voucher.VoucherDto.*;
import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class VoucherServiceTest {

    @Mock
    VoucherRepository voucherRepository;

    @Mock
    Voucher voucher;

    @InjectMocks
    VoucherService voucherService;

    @Test
    @DisplayName("Voucher 최소 가격 테스트")
    void minPrice() {
        // given
        List<VoucherForSale> voucherForSaleList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            VoucherForSale voucherForSale = VoucherForSale.builder()
                    .price(4_000L)
                    .build();

            voucherForSale.updateStatus(FOR_SALE);
            voucherForSaleList.add(voucherForSale);
        }

        Mockito.when(voucherRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(voucher));

        Mockito.when(voucher.getVoucherForSaleList())
                .thenReturn(voucherForSaleList);

        // when
        VoucherResponse voucherResponse = voucherService.find(1L);

        // then
        Assertions.assertThat(voucherResponse.getMinPrice()).isEqualTo(4_000L);
    }
}