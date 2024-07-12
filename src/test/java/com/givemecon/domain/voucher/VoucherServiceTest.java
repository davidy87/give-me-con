package com.givemecon.domain.voucher;

import com.givemecon.domain.image.voucher.VoucherImage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.givemecon.domain.voucher.VoucherDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class VoucherServiceTest {

    @Mock
    VoucherRepository voucherRepository;

    @InjectMocks
    VoucherService voucherService;

    @Test
    void findImageUrl() {
        // given
        Voucher voucher = Voucher.builder()
                .build();

        VoucherImage voucherImage = VoucherImage.builder()
                .imageUrl("imageUrl")
                .build();

        voucher.updateVoucherImage(voucherImage);

        Mockito.when(voucherRepository.findOneWithImage(any(Long.class)))
                .thenReturn(Optional.of(voucher));

        // when
        ImageResponse imageResponse = voucherService.findImageUrl(1L);

        // then
        assertThat(imageResponse.getImageUrl()).isEqualTo(voucherImage.getImageUrl());
    }
}