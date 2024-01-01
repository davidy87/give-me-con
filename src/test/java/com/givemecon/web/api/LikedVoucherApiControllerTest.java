package com.givemecon.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.JwtTokenProvider;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.member.Role;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.domain.likedvoucher.LikedVoucher;
import com.givemecon.domain.likedvoucher.LikedVoucherRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
@SpringBootTest(webEnvironment = RANDOM_PORT)
class LikedVoucherApiControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LikedVoucherRepository likedVoucherRepository;

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
        Voucher voucher = Voucher.builder()
                .title("voucher")
                .price(4_000L)
                .image("voucher.png")
                .build();

        Member member = Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build();

        Voucher voucherSaved = voucherRepository.save(voucher);
        Member memberSaved = memberRepository.save(member);

        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(memberSaved);
        String url = "http://localhost:" + port + "/api/liked-vouchers";

        // when
        ResultActions response = mockMvc.perform(post(url)
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(voucherSaved.getId())))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessResponse(prettyPrint()),
                        requestBody(),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("기프티콘 id"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("기프티콘 가격"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("기프티콘 타이틀"),
                                fieldWithPath("image").type(JsonFieldType.STRING).description("기프티콘 이미지")
                        ))
                );

        // then
        response.andExpect(status().isCreated());

        List<LikedVoucher> likedVoucherList = likedVoucherRepository.findAll();
        LikedVoucher found = likedVoucherList.get(0);

        assertThat(found.getVoucher()).isEqualTo(voucherSaved);
        assertThat(found.getMember()).isEqualTo(memberSaved);
    }

    @Test
    void findAllByUsername() throws Exception {
        // given
        Member member = Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build();

        Member memberSaved = memberRepository.save(member);

        for (int i = 1; i <= 5; i++) {
            Voucher voucher = Voucher.builder()
                    .title("voucher" + i)
                    .price(4_000L)
                    .image("voucher" + i + ".png")
                    .build();

            Voucher voucherSaved = voucherRepository.save(voucher);
            memberSaved.addLikedVoucher(new LikedVoucher(voucherSaved));
        }

        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(memberSaved);
        String url = "http://localhost:" + port + "/api/liked-vouchers";

        // when
        ResultActions response = mockMvc.perform(get(url)
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken()))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("기프티콘 id"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("기프티콘 가격"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("기프티콘 타이틀"),
                                fieldWithPath("[].image").type(JsonFieldType.STRING).description("기프티콘 이미지")
                        ))
                );

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void deleteOne() throws Exception {
        // given
        Voucher voucher = Voucher.builder()
                .title("voucher")
                .price(4_000L)
                .image("voucher.png")
                .build();

        Member member = Member.builder()
                .email("tester@gmail.com")
                .username("tester")
                .role(Role.USER)
                .build();

        Voucher voucherSaved = voucherRepository.save(voucher);
        Member memberSaved = memberRepository.save(member);
        LikedVoucher likedVoucherSaved = likedVoucherRepository.save(LikedVoucher.builder().voucher(voucherSaved).build());
        likedVoucherSaved.setMember(member);

        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(memberSaved);
        String url = "http://localhost:" + port + "/api/liked-vouchers/" + voucherSaved.getId();

        // when
        ResultActions response = mockMvc.perform(delete(url)
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken()))
                .andDo(print())
                .andDo(document("{class-name}/{method-name}"));

        // then
        response.andExpect(status().isNoContent());
        assertThat(likedVoucherRepository.existsById(likedVoucherSaved.getId())).isFalse();
    }
}