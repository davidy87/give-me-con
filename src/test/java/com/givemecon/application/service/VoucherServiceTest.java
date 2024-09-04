package com.givemecon.application.service;

import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucher.VoucherImage;
import com.givemecon.domain.entity.voucher.VoucherStatus;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.givemecon.application.dto.VoucherDto.*;
import static com.givemecon.application.dto.VoucherDto.ImageResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class VoucherServiceTest {

    @Mock
    VoucherRepository voucherRepository;

    @InjectMocks
    VoucherService voucherService;

    @Test
    void findAllByStatusAndUsername() {
        // given
        Member seller = Member.builder()
                .username("seller")
                .build();

        VoucherKind voucherKind = VoucherKind.builder()
                .title("title")
                .build();

        List<Voucher> voucherList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Voucher voucher = Voucher.builder()
                    .price(4_000L)
                    .expDate(LocalDate.now())
                    .barcode("1111 1111 1111")
                    .voucherKind(voucherKind)
                    .seller(seller)
                    .build();

            voucherList.add(voucher);
        }

        Mockito.when(voucherRepository.findAllByStatusAndUsername(any(VoucherStatus.class), eq(seller.getUsername())))
                .thenReturn(voucherList);

        // when
        QueryParameter queryParameter = new QueryParameter(null, 0);
        List<VoucherResponse> result = voucherService.findAllByStatusAndUsername(queryParameter, seller.getUsername());

        // then
        assertThat(result.size()).isEqualTo(voucherList.size());

        for (int i = 0; i < result.size(); i++) {
            Voucher saved = voucherList.get(i);
            VoucherResponse response = result.get(i);

            assertThat(response.getId()).isEqualTo(saved.getId());
        }
    }

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