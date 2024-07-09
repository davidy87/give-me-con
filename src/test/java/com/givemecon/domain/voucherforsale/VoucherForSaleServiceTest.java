package com.givemecon.domain.voucherforsale;

import com.givemecon.domain.image.voucherforsale.VoucherForSaleImage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.givemecon.domain.voucherforsale.VoucherForSaleDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class VoucherForSaleServiceTest {

    @Mock
    VoucherForSaleRepository voucherForSaleRepository;

    @InjectMocks
    VoucherForSaleService voucherForSaleService;

    @Test
    void findImageUrl() {
        // given
        VoucherForSale voucherForSale = VoucherForSale.builder()
                .build();

        VoucherForSaleImage voucherForSaleImage = VoucherForSaleImage.builder()
                .imageUrl("imageUrl")
                .build();

        voucherForSale.updateVoucherForSaleImage(voucherForSaleImage);

        Mockito.when(voucherForSaleRepository.findOneWithImage(any(Long.class)))
                .thenReturn(Optional.of(voucherForSale));

        // when
        ImageResponse imageResponse = voucherForSaleService.findImageUrl(1L);

        // then
        assertThat(imageResponse.getImageUrl()).isEqualTo(voucherForSaleImage.getImageUrl());
    }
}