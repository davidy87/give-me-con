package com.givemecon.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.domain.voucher.Voucher;
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
@WithMockUser(roles = "USER")
class VoucherApiControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    VoucherRepository voucherRepository;

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
        String image = "tall_americano.jpg";
        VoucherSaveRequest requestDto = VoucherSaveRequest.builder()
                .price(price)
                .image(image)
                .build();

        String url = "http://localhost:" + port + "/api/vouchers";

        // when
        ResultActions response = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)));

        // then
        response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("price").value(price))
                .andExpect(jsonPath("image").value(image));

        List<Voucher> voucherList = voucherRepository.findAll();
        assertThat(voucherList.get(0).getPrice()).isEqualTo(price);
        assertThat(voucherList.get(0).getImage()).isEqualTo(image);
    }

    @Test
    void findVoucher() throws Exception {
        // given
        Long price = 20_000L;
        String image = "ice_cream_cake.jpg";
        Voucher voucher = Voucher.builder()
                .price(price)
                .image(image)
                .build();

        Long id = voucherRepository.save(voucher).getId();
        String url = "http://localhost:" + port + "/api/vouchers/" + id;

        // when
        ResultActions response = mockMvc.perform(get(url));

        // then
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("price").value(price))
                .andExpect(jsonPath("image").value(image));
    }

    @Test
    void updateVoucher() throws Exception {
        // given
        Long price = 3_000L;
        String image = "shine_musket_tang_hoo_ru.jpg";
        Voucher voucher = Voucher.builder()
                .price(price)
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
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("price").value(requestDto.getPrice()))
                .andExpect(jsonPath("image").value(requestDto.getImage()));

        List<Voucher> voucherList = voucherRepository.findAll();
        assertThat(voucherList.get(0).getPrice()).isEqualTo(requestDto.getPrice());
        assertThat(voucherList.get(0).getImage()).isEqualTo(requestDto.getImage());
    }

    @Test
    void deleteVoucher() throws Exception {
        // given
        Long price = 4_000L;
        String image = "large_milk_tea.jpg";
        Voucher voucher = Voucher.builder()
                .price(price)
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