package com.givemecon.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.JwtTokenProvider;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.member.Role;
import com.givemecon.domain.voucher.VoucherForSale;
import com.givemecon.domain.voucher.VoucherForSaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
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

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDoc) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDoc))
                .build();
    }

    @Test
    void save() throws Exception {
        // given
        Member seller = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .role(Role.ADMIN)
                .build());

        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(seller);

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

        ResultActions response = mockMvc.perform(post(url)
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(requestDto)))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("저장할 기프티콘 타이틀"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("저장할 기프티콘 가격"),
                                fieldWithPath("expDate").type(JsonFieldType.ARRAY).description("판매중인 기프티콘 가격"),
                                fieldWithPath("barcode").type(JsonFieldType.STRING).description("판매중인 기프티콘 가격"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("저장할 기프티콘 이미지")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("판매중인 기프티콘 id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("판매중인 기프티콘 타이틀"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("판매중인 기프티콘 가격"),
                                fieldWithPath("expDate").type(JsonFieldType.STRING).description("판매중인 기프티콘 가격"),
                                fieldWithPath("barcode").type(JsonFieldType.STRING).description("판매중인 기프티콘 가격"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("판매중인 기프티콘 이미지")
                        ))
                );

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
    void deleteOne() throws Exception {
        // given
        Member seller = memberRepository.save(Member.builder()
                .email("test@gmail.com")
                .username("tester")
                .role(Role.ADMIN)
                .build());

        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(seller);

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
                        .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken()))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}"));

        // then
        response.andExpect(status().isNoContent());
        List<VoucherForSale> voucherForSaleList = voucherForSaleRepository.findAll();
        assertThat(voucherForSaleList).isEmpty();
    }
}