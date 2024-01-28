package com.givemecon.web.api;

import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.voucher.VoucherImage;
import com.givemecon.domain.voucher.VoucherImageRepository;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.voucherforsale.VoucherForSaleImage;
import com.givemecon.domain.voucherforsale.VoucherForSaleImageRepository;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import com.givemecon.domain.voucher.VoucherRepository;
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

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static com.givemecon.web.ApiDocumentUtils.*;
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
@WithMockUser(roles = "ADMIN")
class VoucherApiControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherImageRepository voucherImageRepository;

    @Autowired
    VoucherForSaleRepository voucherForSaleRepository;

    @Autowired
    VoucherForSaleImageRepository voucherForSaleImageRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    S3Client s3Client;

    @Autowired
    S3Mock s3Mock;

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
        Long price = 4_000L;
        String title = "Americano T";
        String image = "tall_americano.png";
        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile",
                image,
                "image/png",
                image.getBytes());

        // when
        ResultActions response = mockMvc.perform(multipart("/api/vouchers")
                .file(imageFile)
                .part(new MockPart("price", price.toString().getBytes(StandardCharsets.UTF_8)))
                .part(new MockPart("title", title.getBytes(StandardCharsets.UTF_8)))
        );

        // then
        List<Voucher> voucherList = voucherRepository.findAll();

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(voucherList.get(0).getId()))
                .andExpect(jsonPath("price").value(voucherList.get(0).getPrice()))
                .andExpect(jsonPath("imageUrl").value(voucherList.get(0).getVoucherImage().getImageUrl()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParts(
                                partWithName("price").description("저장할 기프티콘 최소 가격"),
                                partWithName("title").description("저장할 기프티콘 타이틀"),
                                partWithName("imageFile").description("저장할 기프티콘 이미지 파일")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("저장된 기프티콘 id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("저장된 기프티콘 타이틀"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("저장된 기프티콘 가격"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("저장된 기프티콘 이미지")
                        ))
                );
    }

    @Test
    void findOne() throws Exception {
        // given
        Voucher voucher = voucherRepository.save(Voucher.builder()
                .price(20_000L)
                .title("Ice Cream Cake")
                .build());

        VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("ice_cream_cake.png")
                .build());

        voucher.setVoucherImage(voucherImage);

        // when
        ResultActions response = mockMvc.perform(get("/api/vouchers/{id}", voucher.getId()));

        // then
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(voucher.getId()))
                .andExpect(jsonPath("price").value(voucher.getPrice()))
                .andExpect(jsonPath("imageUrl").value(voucherImage.getImageUrl()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("기프티콘 id")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("기프티콘 id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("기프티콘 타이틀"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("기프티콘 가격"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("기프티콘 이미지 URL")
                        ))
                );
    }

    @Test
    void findAll() throws Exception {
        // given
        for (int i = 1; i <= 5; i++) {
            Voucher voucher = voucherRepository.save(Voucher.builder()
                    .price(10_000L)
                    .title("Voucher " + i)
                    .build());

            VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("voucherImage" + i + ".png")
                    .build());

            voucher.setVoucherImage(voucherImage);
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/vouchers"));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("기프티콘 id"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("기프티콘 타이틀"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("기프티콘 가격"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("기프티콘 이미지 URL")
                        ))
                );
    }

    @Test
    void findAllByBrandName() throws Exception {
        // given
        Brand brand = Brand.builder()
                .name("Test Brand")
                .build();

        Brand brandSaved = brandRepository.save(brand);

        for (int i = 1; i <= 5; i++) {
            Voucher voucher = voucherRepository.save(Voucher.builder()
                    .price(10_000L)
                    .title("Voucher " + i)
                    .build());

            VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("voucherImage" + i + ".png")
                    .build());

            voucher.setVoucherImage(voucherImage);
            brandSaved.addVoucher(voucher);
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/vouchers?brandName={brandName}", brandSaved.getName()));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("brandName").description("브랜드 이름")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("기프티콘 id"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("기프티콘 타이틀"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("기프티콘 가격"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("기프티콘 이미지 URL")
                        ))
                );
    }

    @Test
    void findSellingListByVoucherId() throws Exception {
        // given
        Long price = 4_000L;
        String title = "Americano T";
        Voucher voucher = Voucher.builder()
                .price(price)
                .title(title)
                .build();

        Voucher voucherSaved = voucherRepository.save(voucher);

        for (int i = 1; i <= 5; i++) {
            VoucherForSale voucherForSale = voucherForSaleRepository.save(
                    VoucherForSale.builder()
                            .title("Americano T")
                            .price(4_000L)
                            .expDate(LocalDate.now().plusDays(1))
                            .barcode("1111 1111 1111")
                            .build());

            VoucherForSaleImage voucherForSaleImage = voucherForSaleImageRepository.save(
                    VoucherForSaleImage.builder()
                            .imageKey("imageKey" + i)
                            .imageUrl("imageUrl" + i)
                            .originalName("voucherImage" + i + ".png")
                            .build());

            voucherForSale.setVoucherForSaleImage(voucherForSaleImage);
            voucherSaved.addVoucherForSale(voucherForSale);
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/vouchers/{id}/selling-list", voucherSaved.getId()));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("기프티콘 id")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("판매중인 기프티콘 id"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("판매중인 기프티콘 타이틀"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("판매중인 기프티콘 가격"),
                                fieldWithPath("[].expDate").type(JsonFieldType.STRING).description("판매중인 기프티콘 가격"),
                                fieldWithPath("[].barcode").type(JsonFieldType.STRING).description("판매중인 기프티콘 가격"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("판매중인 기프티콘 이미지")
                        ))
                );
    }

    @Test
    void update() throws Exception {
        // given
        Voucher voucher = voucherRepository.save(Voucher.builder()
                .price(3_000L)
                .title("oldTitle")
                .build());

        VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("oldVoucherImage.jpg")
                .build());

        voucher.setVoucherImage(voucherImage);

        String newTitle = "newTitle";
        MockMultipartFile imageFileToUpdate = new MockMultipartFile(
                "imageFile",
                "oldImage.png",
                "image/png",
                "oldImage.png".getBytes());

        // when
        ResultActions response = mockMvc.perform(multipart("/api/vouchers/{id}", voucher.getId())
                .file(imageFileToUpdate)
                .part(new MockPart("title", newTitle.getBytes(StandardCharsets.UTF_8)))
        );

        // then
        List<Voucher> voucherList = voucherRepository.findAll();

        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(voucherList.get(0).getId()))
                .andExpect(jsonPath("price").value(voucherList.get(0).getPrice()))
                .andExpect(jsonPath("imageUrl").value(voucherList.get(0).getVoucherImage().getImageUrl()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("기프티콘 id")
                        ),
                        requestParts(
                                partWithName("title").optional().description("수정할 기프티콘 타이틀"),
                                partWithName("description").optional().description("수정할 기프티콘 상세설명"),
                                partWithName("caution").optional().description("수정할 기프티콘 주의사항"),
                                partWithName("imageFile").optional().description("수정할 기프티콘 이미지 파일")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("수정된 기프티콘 id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("수정된 기프티콘 타이틀"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("수정된 기프티콘 가격"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("수정된 기프티콘 이미지")
                        ))
                );
    }

    @Test
    void deleteOne() throws Exception {
        // given
        Long price = 4_000L;
        String title = "Milk Tea L";
        Voucher voucher = Voucher.builder()
                .price(price)
                .title(title)
                .build();

        Long id = voucherRepository.save(voucher).getId();

        // when
        ResultActions response = mockMvc.perform(delete("/api/vouchers/{id}", id))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("기프티콘 id")
                        ))
                );

        // then
        response.andExpect(status().isNoContent());
        List<Voucher> voucherList = voucherRepository.findAll();
        assertThat(voucherList).isEmpty();
    }
}