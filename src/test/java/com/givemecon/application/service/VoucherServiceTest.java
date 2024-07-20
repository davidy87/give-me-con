package com.givemecon.application.service;

import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucher.VoucherImage;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.givemecon.application.dto.VoucherDto.ImageResponse;
import static org.assertj.core.api.Assertions.assertThat;
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
        VoucherImage voucherImage = VoucherImage.builder()
                .imageUrl("imageUrl")
                .build();

        Voucher voucher = Voucher.builder()
                .voucherImage(voucherImage)
                .build();

        Mockito.when(voucherRepository.findOneWithImage(any(Long.class)))
                .thenReturn(Optional.of(voucher));

        // when
        ImageResponse imageResponse = voucherService.findImageUrl(1L);

        // then
        assertThat(imageResponse.getImageUrl()).isEqualTo(voucherImage.getImageUrl());
    }
}