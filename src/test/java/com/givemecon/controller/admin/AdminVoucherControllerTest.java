package com.givemecon.controller.admin;

import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
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

import static com.givemecon.domain.entity.voucher.VoucherStatus.FOR_SALE;
import static com.givemecon.domain.entity.voucher.VoucherStatus.SALE_REQUESTED;
import static com.givemecon.util.ApiDocumentUtils.getDocumentRequestWithAuth;
import static com.givemecon.util.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import(S3MockConfig.class)
@Transactional
@SpringBootTest
class AdminVoucherControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    VoucherKindRepository voucherKindRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    S3Mock s3Mock;

    @Autowired
    S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    VoucherKind voucherKind;

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
    }

    @AfterEach
    void tearDown() {
        s3Mock.stop();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin용 VoucherStatus별 Voucher 조회")
    void findAllByStatus() throws Exception {
        // given
        List<Voucher> toSaveList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Voucher toSave = Voucher.builder()
                    .price(4_000L)
                    .barcode("1111 1111 1111")
                    .expDate(LocalDate.now())
                    .voucherKind(voucherKind)
                    .build();

            if (i < 2) {
                toSave.updateStatus(FOR_SALE);
            }

            toSaveList.add(toSave);
        }

        voucherRepository.saveAll(toSaveList);

        // when
        ResultActions response = mockMvc.perform(get("/api/admin/vouchers")
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

}