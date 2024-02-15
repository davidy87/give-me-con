package com.givemecon.controller.api;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.JwtTokenProvider;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.domain.member.Role;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.image.voucherforsale.VoucherForSaleImage;
import com.givemecon.domain.image.voucherforsale.VoucherForSaleImageRepository;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import com.givemecon.s3.S3MockConfig;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.time.LocalDate;
import java.util.List;

import static com.givemecon.controller.ApiDocumentUtils.*;
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
class VoucherForSaleApiControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherForSaleRepository voucherForSaleRepository;

    @Autowired
    VoucherForSaleImageRepository voucherForSaleImageRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    S3Mock s3Mock;

    @Autowired
    S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

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
    }

    @AfterEach
    void stop() {
        s3Mock.stop();
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
        LocalDate expDate = LocalDate.now().plusDays(1);
        String barcode = "1111 1111 1111";
        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile",
                "Americano_T.png",
                "image/png",
                "Americano_T.png".getBytes());

        Voucher voucher = voucherRepository.save(Voucher.builder()
                .title(title)
                .price(0L)
                .build());

        // when
        ResultActions response = mockMvc.perform(multipart("/api/vouchers-for-sale")
                .file(imageFile)
                .part(new MockPart("voucherId", voucher.getId().toString().getBytes()))
                .part(new MockPart("price", price.toString().getBytes()))
                .part(new MockPart("expDate", expDate.toString().getBytes()))
                .part(new MockPart("barcode", barcode.getBytes()))
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken())
                .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        assertThat(voucher.getPrice()).isEqualTo(price);
        List<VoucherForSale> voucherForSaleList = voucherForSaleRepository.findAll();

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(voucherForSaleList.get(0).getId()))
                .andExpect(jsonPath("title").value(voucherForSaleList.get(0).getTitle()))
                .andExpect(jsonPath("price").value(voucherForSaleList.get(0).getPrice()))
                .andExpect(jsonPath("expDate").value(voucherForSaleList.get(0).getExpDate().toString()))
                .andExpect(jsonPath("barcode").value(voucherForSaleList.get(0).getBarcode()))
                .andExpect(jsonPath("imageUrl").value(voucherForSaleList.get(0).getImageUrl()))
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
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("판매중인 기프티콘 id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("판매중인 기프티콘 타이틀"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("판매중인 기프티콘 가격"),
                                fieldWithPath("expDate").type(JsonFieldType.STRING).description("판매중인 기프티콘 가격"),
                                fieldWithPath("barcode").type(JsonFieldType.STRING).description("판매중인 기프티콘 가격"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("판매중인 기프티콘 이미지 URL")
                        ))
                );
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

        Voucher voucher = voucherRepository.save(Voucher.builder()
                .title("voucher")
                .price(4_000L)
                .build());

        VoucherForSale voucherForSale = voucherForSaleRepository.save(VoucherForSale.builder()
                .price(4_000L)
                .expDate(LocalDate.now().plusDays(1))
                .barcode("1111 1111 1111")
                .build());

        VoucherForSaleImage voucherForSaleImage = voucherForSaleImageRepository.save(VoucherForSaleImage.builder()
                .imageUrl("imageUrl")
                .imageKey("imageKey")
                .originalName("Americano_T.png")
                .build());

        voucherForSale.updateVoucherForSaleImage(voucherForSaleImage);
        voucher.addVoucherForSale(voucherForSale);

        // when
        ResultActions response = mockMvc.perform(delete("/api/vouchers-for-sale/{id}", voucherForSale.getId())
                .header("Authorization", tokenInfo.getGrantType() + " " + tokenInfo.getAccessToken()));

        // then
        assertThat(voucher.getPrice()).isEqualTo(0L);

        response.andExpect(status().isNoContent())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("판매중인 기프티콘 id")
                        ))
                );

        List<VoucherForSale> voucherForSaleList = voucherForSaleRepository.findAll();
        assertThat(voucherForSaleList).isEmpty();
    }
}