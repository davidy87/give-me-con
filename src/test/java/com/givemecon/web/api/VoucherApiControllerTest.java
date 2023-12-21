package com.givemecon.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherForSale;
import com.givemecon.domain.voucher.VoucherForSaleRepository;
import com.givemecon.domain.voucher.VoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static com.givemecon.web.dto.VoucherDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = RANDOM_PORT)
@WithMockUser(roles = "ADMIN")
class VoucherApiControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherForSaleRepository voucherSellingRepository;

    @Autowired
    BrandRepository brandRepository;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void saveVoucher() throws Exception {
        // given
        Long price = 4_000L;
        String title = "Americano T";
        String image = "tall_americano.jpg";
        VoucherSaveRequest requestDto = VoucherSaveRequest.builder()
                .price(price)
                .title(title)
                .image(image)
                .build();

        String url = "http://localhost:" + port + "/api/vouchers";

        // when
        ResultActions response = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)));

        // then
        List<Voucher> voucherList = voucherRepository.findAll();

        response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(voucherList.get(0).getId()))
                .andExpect(jsonPath("price").value(voucherList.get(0).getPrice()))
                .andExpect(jsonPath("image").value(voucherList.get(0).getImage()));
    }

    @Test
    void findVoucher() throws Exception {
        // given
        Long price = 20_000L;
        String title = "Ice Cream Cake";
        String image = "ice_cream_cake.jpg";
        Voucher voucher = Voucher.builder()
                .price(price)
                .title(title)
                .image(image)
                .build();

        Long id = voucherRepository.save(voucher).getId();
        String url = "http://localhost:" + port + "/api/vouchers/" + id;

        // when
        ResultActions response = mockMvc.perform(get(url));

        // then
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("price").value(price))
                .andExpect(jsonPath("image").value(image));
    }

    @Test
    void findAllVouchers() throws Exception {
        // given
        for (int i = 1; i <= 10; i++) {
            Voucher voucher = Voucher.builder()
                    .price(10_000L)
                    .title("Voucher" + i)
                    .image("brand_" + i + ".png")
                    .build();

            voucherRepository.save(voucher);
        }

        String url = "http://localhost:" + port + "/api/vouchers";

        // when
        ResultActions response = mockMvc.perform(get(url));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void findAllByBrandId() throws Exception {
        // given
        Brand brand = Brand.builder()
                .name("Test Brand")
                .icon("test_brand.png")
                .build();

        Brand brandSaved = brandRepository.save(brand);

        for (int i = 1; i <= 10; i++) {
            Voucher voucher = Voucher.builder()
                    .price(10_000L)
                    .title("Voucher" + i)
                    .image("brand_" + i + ".png")
                    .build();

            voucher.setBrand(brandSaved);
            voucherRepository.save(voucher);
        }

        String url = "http://localhost:" + port + "/api/vouchers?brandId=" + brandSaved.getId();

        // when
        ResultActions response = mockMvc.perform(get(url));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void findSellingListByVoucherId() throws Exception {
        // given
        Long price = 4_000L;
        String title = "Americano T";
        String image = "americano.jpg";
        Voucher voucher = Voucher.builder()
                .price(price)
                .title(title)
                .image(image)
                .build();

        Voucher voucherSaved = voucherRepository.save(voucher);

        for (int i = 1; i <= 10; i++) {
            VoucherForSale voucherSelling = VoucherForSale.builder()
                    .title("Americano T")
                    .image("americano.jpg")
                    .price(4_000L)
                    .expDate(LocalDate.now())
                    .barcode("1111 1111 1111")
                    .build();

            voucherSellingRepository.save(voucherSelling);
            voucherSaved.addVoucherForSale(voucherSelling);
        }

        String url = "http://localhost:" + port + "/api/vouchers/" + voucherSaved.getId() + "/selling-list";

        // when
        ResultActions response = mockMvc.perform(get(url));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void updateVoucher() throws Exception {
        // given
        Long price = 3_000L;
        String title = "Shine Musket Tang Hoo Ru";
        String image = "shine_musket_tang_hoo_ru.jpg";
        Voucher voucher = Voucher.builder()
                .price(price)
                .title(title)
                .image(image)
                .build();

        Long id = voucherRepository.save(voucher).getId();
        String url = "http://localhost:" + port + "/api/vouchers/" + id;
        VoucherUpdateRequest requestDto = VoucherUpdateRequest.builder()
                .price(price)
                .image("strawberry_tang_hoo_ru.jpg")
                .build();

        // when
        ResultActions response = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)));

        // then
        List<Voucher> voucherList = voucherRepository.findAll();

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(voucherList.get(0).getId()))
                .andExpect(jsonPath("price").value(voucherList.get(0).getPrice()))
                .andExpect(jsonPath("image").value(voucherList.get(0).getImage()));
    }

    @Test
    void deleteVoucher() throws Exception {
        // given
        Long price = 4_000L;
        String title = "Milk Tea L";
        String image = "large_milk_tea.jpg";
        Voucher voucher = Voucher.builder()
                .price(price)
                .title(title)
                .image(image)
                .build();

        Long id = voucherRepository.save(voucher).getId();
        String url = "http://localhost:" + port + "/api/vouchers/" + id;

        // when
        ResultActions response = mockMvc.perform(delete(url));

        // then
        response.andExpect(status().isOk());

        List<Voucher> voucherList = voucherRepository.findAll();
        assertThat(voucherList).isEmpty();
    }
}