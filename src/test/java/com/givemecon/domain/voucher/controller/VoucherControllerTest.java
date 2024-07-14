package com.givemecon.domain.voucher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.token.JwtTokenService;
import com.givemecon.domain.image.entity.VoucherImage;
import com.givemecon.domain.member.entity.Member;
import com.givemecon.domain.member.repository.MemberRepository;
import com.givemecon.domain.voucher.entity.Voucher;
import com.givemecon.domain.voucherkind.entity.VoucherKind;
import com.givemecon.domain.voucherkind.repository.VoucherKindRepository;
import com.givemecon.domain.image.repository.VoucherForSaleImageRepository;
import com.givemecon.domain.voucher.repository.VoucherRepository;
import com.givemecon.util.s3.S3MockConfig;
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

import static com.givemecon.config.enums.JwtAuthHeader.*;
import static com.givemecon.config.enums.Authority.*;
import static com.givemecon.util.ApiDocumentUtils.*;
import static com.givemecon.util.TokenHeaderUtils.getAccessTokenHeader;
import static com.givemecon.domain.member.dto.MemberDto.*;
import static com.givemecon.domain.voucher.dto.VoucherDto.*;
import static com.givemecon.domain.voucher.dto.VoucherStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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
    VoucherForSaleImageRepository voucherForSaleImageRepository;

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
                .authority(USER)
                .build());

        admin = memberRepository.save(Member.builder()
                .email("admin@gmail.com")
                .username("admin")
                .authority(ADMIN)
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
                .part(new MockPart("voucherId", voucherKind.getId().toString().getBytes()))
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
                                partWithName("voucherId").description("기프티콘 상품 id"),
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
                    .build();

            VoucherImage voucherImage =
                    voucherForSaleImageRepository.save(VoucherImage.builder()
                            .imageUrl("imageUrl" + i)
                            .originalName("voucherImage" + i)
                            .imageKey("imageKey" + i)
                            .build());

            toSave.updateVoucherImage(voucherImage);
            toSave.updateVoucherKind(voucherKind);
            toSave.updateSeller(user);
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
    void findAllByStatus() throws Exception {
        // given
        List<Voucher> toSaveList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Voucher toSave = Voucher.builder()
                    .price(4_000L)
                    .barcode("1111 1111 1111")
                    .expDate(LocalDate.now())
                    .build();

            VoucherImage voucherImage =
                    voucherForSaleImageRepository.save(VoucherImage.builder()
                            .imageUrl("imageUrl" + i)
                            .originalName("voucherImage" + i)
                            .imageKey("imageKey" + i)
                            .build());

            if (i < 2) {
                toSave.updateStatus(FOR_SALE);
            }

            toSave.updateVoucherImage(voucherImage);
            toSave.updateVoucherKind(voucherKind);
            toSave.updateSeller(user);
            toSaveList.add(toSave);
        }

        voucherRepository.saveAll(toSaveList);

        // when
        ResultActions response = mockMvc.perform(get("/api/vouchers")
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(adminTokenInfo))
                .queryParam("statusCode", String.valueOf(NOT_YET_PERMITTED.ordinal())));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("statusCode").description("기프티콘 상태코드 (0 ~ 4)")
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
        Voucher voucher = Voucher.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now())
                .build();

        VoucherImage voucherImage =
                voucherForSaleImageRepository.save(VoucherImage.builder()
                        .imageKey("imageKey")
                        .imageUrl("imageUrl")
                        .originalName("voucherImage")
                        .build());

        voucher.updateVoucherImage(voucherImage);
        voucher.updateVoucherKind(voucherKind);
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

    @Test
    void updateStatus() throws Exception {
        // given
        Voucher voucher = Voucher.builder()
                .price(4_000L)
                .barcode("1111 1111 1111")
                .expDate(LocalDate.now())
                .build();

        VoucherImage voucherImage =
                voucherForSaleImageRepository.save(VoucherImage.builder()
                        .imageUrl("imageUrl")
                        .originalName("voucherImage")
                        .imageKey("imageKey")
                        .build());

        voucher.updateVoucherImage(voucherImage);
        voucher.updateVoucherKind(voucherKind);
        voucherRepository.save(voucher);

        // when
        StatusUpdateRequest requestBody = new StatusUpdateRequest();
        requestBody.setStatusCode(FOR_SALE.ordinal());

        ResultActions response = mockMvc.perform(put("/api/vouchers/{id}", voucher.getId())
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(adminTokenInfo))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestBody)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(voucher.getId()))
                .andExpect(jsonPath("price").value(voucher.getPrice()))
                .andExpect(jsonPath("title").value(voucher.getTitle()))
                .andExpect(jsonPath("barcode").value(voucher.getBarcode()))
                .andExpect(jsonPath("expDate").value(voucher.getExpDate().toString()))
                .andExpect(jsonPath("status").value(FOR_SALE.name()))
                .andExpect(jsonPath("saleRequestedDate").value(voucher.getSaleRequestedDate().toString()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("판매중(or 판매 대기 중)인 기프티콘 id")
                        ),
                        requestFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("기프티콘 상태코드 (0 ~ 4)"),
                                fieldWithPath("rejectedReason")
                                        .type(JsonFieldType.STRING).optional()
                                        .description("판매 요청 거절 사유 (statusCode가 3(REJECTED)일 경우 필수)")
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
    void deleteOne() throws Exception {
        // given
        Voucher voucher = voucherRepository.save(Voucher.builder()
                .price(4_000L)
                .expDate(LocalDate.now().plusDays(1))
                .barcode("1111 1111 1111")
                .build());

        VoucherImage voucherImage = voucherForSaleImageRepository.save(VoucherImage.builder()
                .imageUrl("imageUrl")
                .imageKey("imageKey")
                .originalName("Americano_T.png")
                .build());

        voucher.updateSeller(user);
        voucher.updateVoucherImage(voucherImage);
        voucher.updateVoucherKind(voucherKind);

        // when
        ResultActions response = mockMvc.perform(delete("/api/vouchers/{id}", voucher.getId())
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(adminTokenInfo)));

        // then
        response.andExpect(status().isNoContent())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("판매중인 기프티콘 id")
                        ))
                );

        List<Voucher> voucherList = voucherRepository.findAll();
        assertThat(voucherList).isEmpty();
    }
}