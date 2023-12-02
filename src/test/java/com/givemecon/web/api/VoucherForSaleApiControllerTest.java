package com.givemecon.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.member.Role;
import com.givemecon.domain.voucher.VoucherForSale;
import com.givemecon.domain.voucher.VoucherForSaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static com.givemecon.web.dto.VoucherForSaleDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = RANDOM_PORT)
class VoucherForSaleApiControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    VoucherForSaleRepository voucherForSaleRepository;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void saveVoucherForSale() throws Exception {
        // given
        Member seller = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build());

        String title = "Americano T";
        Long price = 4_000L;
        LocalDate expDate = LocalDate.now();
        String barcode = "1111 1111 1111";
        String image = "Americano_T.png";

        VoucherForSaleRequest requestDto = VoucherForSaleRequest.builder()
                .title(title)
                .price(price)
                .expDate(expDate)
                .barcode(barcode)
                .image(image)
                .build();

        // when
        String url = "http://localhost:" + port + "/api/vouchers-for-sale";

        // when
        ResultActions response = mockMvc.perform(post(url)
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login()
                        .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                        .attributes(attributes -> attributes.put("sub", "tester"))
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(requestDto)));

        // then
        List<VoucherForSale> voucherForSaleList = voucherForSaleRepository.findAll();

        response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(voucherForSaleList.get(0).getId()))
                .andExpect(jsonPath("title").value(voucherForSaleList.get(0).getTitle()))
                .andExpect(jsonPath("price").value(voucherForSaleList.get(0).getPrice()))
                .andExpect(jsonPath("expDate").value(voucherForSaleList.get(0).getExpDate().toString()))
                .andExpect(jsonPath("barcode").value(voucherForSaleList.get(0).getBarcode()))
                .andExpect(jsonPath("image").value(voucherForSaleList.get(0).getImage()));
    }

    @Test
    void deleteVoucherForSale() throws Exception {
        // given
        Member seller = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build());

        String title = "Americano T";
        Long price = 4_000L;
        LocalDate expDate = LocalDate.now();
        String barcode = "1111 1111 1111";
        String image = "Americano_T.png";

        VoucherForSaleRequest requestDto = VoucherForSaleRequest.builder()
                .title(title)
                .price(price)
                .expDate(expDate)
                .barcode(barcode)
                .image(image)
                .build();

        VoucherForSale voucherForSaleSaved = voucherForSaleRepository.save(requestDto.toEntity());
        Long id = voucherForSaleSaved.getId();

        // when
        String url = "http://localhost:" + port + "/api/vouchers-for-sale/" + id;

        // when
        ResultActions response = mockMvc.perform(delete(url)
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login()
                        .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                        .attributes(attributes -> attributes.put("sub", "tester"))
                ));

        // then
        List<VoucherForSale> voucherForSaleList = voucherForSaleRepository.findAll();
        assertThat(voucherForSaleList).isEmpty();
    }
}