package com.givemecon.controller;

import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.common.auth.jwt.token.JwtTokenService;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucher.VoucherImage;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.repository.MemberRepository;
import com.givemecon.domain.repository.voucher.VoucherImageRepository;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import com.givemecon.domain.repository.voucherkind.VoucherKindRepository;
import com.givemecon.infrastructure.s3.S3MockConfig;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.givemecon.application.dto.MemberDto.TokenRequest;
import static com.givemecon.domain.entity.member.Role.ADMIN;
import static com.givemecon.domain.entity.member.Role.USER;
import static com.givemecon.common.auth.enums.JwtAuthHeader.AUTHORIZATION;
import static com.givemecon.domain.entity.voucher.VoucherStatus.FOR_SALE;
import static com.givemecon.domain.entity.voucher.VoucherStatus.SALE_REQUESTED;
import static com.givemecon.util.ApiDocumentUtils.getDocumentRequestWithAuth;
import static com.givemecon.util.ApiDocumentUtils.getDocumentResponse;
import static com.givemecon.util.TokenHeaderUtils.getAccessTokenHeader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import(S3MockConfig.class)
@Transactional
@SpringBootTest
class VoucherControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    VoucherKindRepository voucherKindRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherImageRepository voucherImageRepository;

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    S3Mock s3Mock;

    @Autowired
    S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    Member user;

    Member admin;

    VoucherKind voucherKind;

    TokenInfo userTokenInfo;

    TokenInfo adminTokenInfo;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDoc) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDoc))
                .alwaysDo(print())
                .build();

        s3Mock.start();
        s3Client.createBucket(CreateBucketRequest.builder()
                .bucket(bucketName)
                .build());

        voucherKind = voucherKindRepository.save(VoucherKind.builder()
                .title("voucherKind")
                .build());

        user = memberRepository.save(Member.builder()
                .email("user@gmail.com")
                .username("user")
                .role(USER)
                .build());

        admin = memberRepository.save(Member.builder()
                .email("admin@gmail.com")
                .username("admin")
                .role(ADMIN)
                .build());

        userTokenInfo = jwtTokenService.getTokenInfo(new TokenRequest(user));
        adminTokenInfo = jwtTokenService.getTokenInfo(new TokenRequest(admin));
    }

    @AfterEach
    void stop() {
        s3Mock.stop();
    }

    @Test
    void save() throws Exception {
        // given
        Long price = 4_000L;
        LocalDate expDate = LocalDate.now().plusDays(1);
        String barcode = "1111 1111 1111";
        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile",
                "Americano_T.png",
                "image/png",
                "Americano_T.png".getBytes());

        // when
        ResultActions response = mockMvc.perform(multipart("/api/vouchers")
                .file(imageFile)
                .part(new MockPart("voucherKindId", voucherKind.getId().toString().getBytes()))
                .part(new MockPart("price", price.toString().getBytes()))
                .part(new MockPart("expDate", expDate.toString().getBytes()))
                .part(new MockPart("barcode", barcode.getBytes()))
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(userTokenInfo))
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        List<Voucher> voucherList = voucherRepository.findAll();
        assertThat(voucherList).isNotEmpty();

        Voucher voucher = voucherList.get(0);

        response.andExpect(status().isAccepted())
                .andExpect(jsonPath("id").value(voucher.getId()))
                .andExpect(jsonPath("price").value(voucher.getPrice()))
                .andExpect(jsonPath("title").value(voucher.getTitle()))
                .andExpect(jsonPath("barcode").value(voucher.getBarcode()))
                .andExpect(jsonPath("expDate").value(voucher.getExpDate().toString()))
                .andExpect(jsonPath("status").value(voucher.getStatus().name()))
                .andExpect(jsonPath("saleRequestedDate").value(voucher.getSaleRequestedDate().toString()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        requestParts(
                                partWithName("voucherKindId").description("기프티콘 종류 id"),
                                partWithName("price").description("판매할 기프티콘 가격"),
                                partWithName("expDate").description("판매할 기프티콘 유효기한"),
                                partWithName("barcode").description("판매할 기프티콘 바코드"),
                                partWithName("imageFile").description("판매할 기프티콘 이미지 파일")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("판매 기프티콘 id"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("판매 기프티콘 가격"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("판매 기프티콘 타이틀"),
                                fieldWithPath("barcode").type(JsonFieldType.STRING).description("판매 기프티콘 바코드"),
                                fieldWithPath("expDate").type(JsonFieldType.STRING).description("판매 기프티콘 유효기간"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("판매 기프티콘 상태"),
                                fieldWithPath("saleRequestedDate").type(JsonFieldType.STRING).description("기프티콘 판매 요청일자")
                        ))
                );
    }

    @Test
    void findAllBySeller() throws Exception {
        // given
        List<Voucher> toSaveList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Voucher toSave = Voucher.builder()
                    .price(4_000L)
                    .barcode("1111 1111 1111")
                    .expDate(LocalDate.now())
                    .voucherKind(voucherKind)
                    .seller(user)
                    .build();

            toSaveList.add(toSave);
        }

        voucherRepository.saveAll(toSaveList);

        // when
        ResultActions response = mockMvc.perform(get("/api/vouchers")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(userTokenInfo)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("판매 기프티콘 id"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("판매 기프티콘 가격"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("판매 기프티콘 타이틀"),
                                fieldWithPath("[].barcode").type(JsonFieldType.STRING).description("판매 기프티콘 바코드"),
                                fieldWithPath("[].expDate").type(JsonFieldType.STRING).description("판매 기프티콘 유효기간"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("판매 기프티콘 상태"),
                                fieldWithPath("[].saleRequestedDate").type(JsonFieldType.STRING).description("기프티콘 판매 요청일자")
                        ))
                );
    }

    @Test
    void findAllByStatusAndUsername() throws Exception {
        // given
        List<Voucher> toSaveList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Voucher toSave = Voucher.builder()
                    .price(4_000L)
                    .barcode("1111 1111 1111")
                    .expDate(LocalDate.now())
                    .voucherKind(voucherKind)
                    .seller(user)
                    .build();

            if (i < 2) {
                toSave.updateStatus(FOR_SALE);
            }

            toSaveList.add(toSave);
        }

        voucherRepository.saveAll(toSaveList);

        // when
        ResultActions response = mockMvc.perform(get("/api/vouchers")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(userTokenInfo))
                .queryParam("statusCode", String.valueOf(SALE_REQUESTED.ordinal())));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("statusCode").description("기프티콘 상태코드 (0 ~ 5)")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("판매 기프티콘 id"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("판매 기프티콘 가격"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("판매 기프티콘 타이틀"),
                                fieldWithPath("[].barcode").type(JsonFieldType.STRING).description("판매 기프티콘 바코드"),
                                fieldWithPath("[].expDate").type(JsonFieldType.STRING).description("판매 기프티콘 유효기간"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("판매 기프티콘 상태"),
                                fieldWithPath("[].saleRequestedDate").type(JsonFieldType.STRING).description("기프티콘 판매 요청일자")
                        ))
                );
    }

    @Test
    void findAllByVoucherKindId() throws Exception {
        // given
        Member seller = memberRepository.save(Member.builder()
                .email("seller@gmail.com")
                .username("seller")
                .role(USER)
                .build());

        List<Voucher> toSaveList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Voucher toSave = Voucher.builder()
                    .price(4_000L)
                    .barcode("1111 1111 1111")
                    .expDate(LocalDate.now())
                    .voucherKind(voucherKind)
                    .seller(seller)
                    .build();

            if (i < 2) {
                toSave.updateStatus(FOR_SALE);
            }

            toSaveList.add(toSave);
        }

        voucherRepository.saveAll(toSaveList);

        // when
        ResultActions response = mockMvc.perform(get("/api/vouchers")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(adminTokenInfo))
                .queryParam("voucherKindId", String.valueOf(voucherKind.getId())));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("voucherKindId").description("기프티콘 종류 id")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("판매 기프티콘 id"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("판매 기프티콘 가격"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("판매 기프티콘 타이틀"),
                                fieldWithPath("[].barcode").type(JsonFieldType.STRING).description("판매 기프티콘 바코드"),
                                fieldWithPath("[].expDate").type(JsonFieldType.STRING).description("판매 기프티콘 유효기간"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("판매 기프티콘 상태"),
                                fieldWithPath("[].saleRequestedDate").type(JsonFieldType.STRING).description("기프티콘 판매 요청일자")
                        ))
                );
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("기프티콘 이미지 조회 API 테스트")
    void findImageUrl() throws Exception {
        // given
        VoucherImage voucherImage =
                voucherImageRepository.save(VoucherImage.builder()
                        .imageKey("imageKey")
                        .imageUrl("imageUrl")
                        .originalName("voucherImage")
                        .build());

        Voucher voucher = Voucher.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now())
                .voucherImage(voucherImage)
                .build();

        voucherRepository.save(voucher);

        // when
        ResultActions response = mockMvc.perform(get("/api/vouchers/{id}/image", voucher.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("imageUrl").value(voucherImage.getImageUrl()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("판매 중인 기프티콘 id")
                        ),
                        responseFields(
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("판매 기프티콘 이미지 URL")
                        ))
                );
    }
}