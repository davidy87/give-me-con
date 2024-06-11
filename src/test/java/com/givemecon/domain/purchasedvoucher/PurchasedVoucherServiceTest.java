package com.givemecon.domain.purchasedvoucher;

import com.givemecon.domain.image.voucherforsale.VoucherForSaleImage;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.givemecon.domain.purchasedvoucher.PurchasedVoucherDto.*;
import static com.givemecon.domain.purchasedvoucher.PurchasedVoucherStatus.*;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class PurchasedVoucherServiceTest {

    @Autowired
    PurchasedVoucherRepository purchasedVoucherRepository;

    @Autowired
    PurchasedVoucherService purchasedVoucherService;

    VoucherForSale voucherForSale;

    @BeforeEach
    void setup() {
        Voucher voucher = Voucher.builder()
                .title("voucher")
                .build();

        VoucherForSaleImage voucherForSaleImage = VoucherForSaleImage.builder()
                .imageUrl("imageUrl")
                .build();

        voucherForSale = VoucherForSale.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now())
                .build();

        voucherForSale.updateVoucher(voucher);
        voucherForSale.updateVoucherForSaleImage(voucherForSaleImage);
    }


    @Test
    @DisplayName("PurchasedVoucher의 현재 상태가 USABLE이 아닌 경우, USED로 변경되지 않는다.")
    void setUsedOnNotUsable() {
        // given
        Member owner = Member.builder().build();
        PurchasedVoucher purchasedVoucher =
                purchasedVoucherRepository.save(new PurchasedVoucher(voucherForSale, owner));

        purchasedVoucher.updateStatus(EXPIRED);

        // when
        PurchasedVoucherResponse response = purchasedVoucherService.setUsed(purchasedVoucher.getId());

        // then
        assertThat(response.getStatus()).isSameAs(EXPIRED);
    }
}