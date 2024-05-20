package com.givemecon.domain.voucher;

import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class VoucherMinPriceTest {
    
    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherForSaleRepository voucherForSaleRepository;
    
    @Test
    @DisplayName("VoucherForSale 여러 개 추가 후 Voucher의 minPrice 확인")
    void checkMinPriceAfterInsertion() {
        // given
        Voucher voucher = voucherRepository.save(Voucher.builder()
                .title("voucher")
                .description("description")
                .caution("caution")
                .build());

        // when
        saveVoucherForSaleFor(voucher);

        // then
        Long minPrice = voucherRepository.findAll().get(0).getMinPrice();
        assertThat(minPrice).isEqualTo(1000L);
    }

    @Test
    void checkMinPriceAfterDeletion() {
        // given
        Voucher voucher = voucherRepository.save(Voucher.builder()
                .title("voucher")
                .description("description")
                .caution("caution")
                .build());

        saveVoucherForSaleFor(voucher);

        // when
        for (VoucherForSale voucherForSale : voucherForSaleRepository.findAll()) {
            if (voucherForSale.getPrice() == 1000L) {
                voucherForSale.delete();
                break;
            }
        }

        // then
        assertThat(voucher.getMinPrice()).isEqualTo(2000L);
    }

    private void saveVoucherForSaleFor(Voucher voucher) {
        List<VoucherForSale> voucherForSaleList = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            VoucherForSale voucherForSale = VoucherForSale.builder()
                    .price(1000L * i)
                    .barcode("1111 1111 1111")
                    .expDate(LocalDate.now())
                    .build();

            voucherForSaleList.add(voucherForSale);
            voucher.addVoucherForSale(voucherForSale);
        }

        voucherForSaleRepository.saveAll(voucherForSaleList);
    }
}