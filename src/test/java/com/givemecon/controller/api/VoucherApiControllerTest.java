package com.givemecon.controller.api;

import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.category.CategoryRepository;
import com.givemecon.domain.voucher.Voucher;
import com.givemecon.domain.image.voucher.VoucherImage;
import com.givemecon.domain.image.voucher.VoucherImageRepository;
import com.givemecon.domain.voucherforsale.VoucherForSale;
import com.givemecon.domain.image.voucherforsale.VoucherForSaleImage;
import com.givemecon.domain.image.voucherforsale.VoucherForSaleImageRepository;
import com.givemecon.domain.voucherforsale.VoucherForSaleRepository;
import com.givemecon.domain.voucher.VoucherRepository;
import com.givemecon.util.s3.S3MockConfig;
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
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static com.givemecon.controller.ApiDocumentUtils.*;
import static com.givemecon.domain.voucher.VoucherDto.*;
import static com.givemecon.domain.voucherforsale.VoucherForSaleStatus.*;
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
    CategoryRepository categoryRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    VoucherImageRepository voucherImageRepository;

    @Autowired
    VoucherForSaleRepository voucherForSaleRepository;

    @Autowired
    VoucherForSaleImageRepository voucherForSaleImageRepository;

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
        String title = "Americano T";
        String image = "tall_americano.png";

        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile",
                image,
                "image/png",
                image.getBytes());

        Category category = categoryRepository.save(Category.builder()
                .name("Cafe")
                .build());

        Brand brand = brandRepository.save(Brand.builder()
                .name("Starbucks")
                .build());

        brand.updateCategory(category);

        // when
        ResultActions response = mockMvc.perform(multipart("/api/vouchers")
                .file(imageFile)
                .part(new MockPart("brandId", brand.getId().toString().getBytes()))
                .part(new MockPart("title", title.getBytes()))
        );

        // then
        List<Voucher> voucherList = voucherRepository.findAll();
        assertThat(voucherList).isNotEmpty();

        Voucher voucher = voucherList.get(0);
        assertThat(voucher.getBrand()).isEqualTo(brand);
        assertThat(voucher.getBrand().getCategory()).isEqualTo(category);

        VoucherResponse voucherResponse = new VoucherResponse(voucher);

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(voucherResponse.getId()))
                .andExpect(jsonPath("minPrice").value(voucherResponse.getMinPrice()))
                .andExpect(jsonPath("title").value(voucherResponse.getTitle()))
                .andExpect(jsonPath("description").value(voucherResponse.getDescription()))
                .andExpect(jsonPath("caution").value(voucherResponse.getCaution()))
                .andExpect(jsonPath("imageUrl").value(voucherResponse.getImageUrl()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        requestParts(
                                partWithName("brandId").description("저장할 기프티콘의 브랜드 id"),
                                partWithName("title").description("저장할 기프티콘 타이틀"),
                                partWithName("description").optional().description("저장할 기프티콘 최소 가격 (생략 가능)"),
                                partWithName("caution").optional().description("저장할 기프티콘 타이틀 (생략 가능)"),
                                partWithName("imageFile").description("저장할 기프티콘 이미지 파일")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("저장된 기프티콘 id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("저장된 기프티콘 타이틀"),
                                fieldWithPath("minPrice").type(JsonFieldType.NUMBER).description("저장된 기프티콘 가격"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("저장된 기프티콘 이미지"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("상품 설명").optional(),
                                fieldWithPath("caution").type(JsonFieldType.STRING).description("사용 시 유의사항").optional()
                        ))
                );
    }

    @Test
    void findOne() throws Exception {
        // given
        Voucher voucher = voucherRepository.save(Voucher.builder()
                .title("Ice Cream Cake")
                .description("This is Ice Cream Cake.")
                .caution("This Ice Cream Cake is extremely cold.")
                .build());

        VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("ice_cream_cake.png")
                .build());

        voucher.updateVoucherImage(voucherImage);

        // when
        ResultActions response = mockMvc.perform(get("/api/vouchers/{id}", voucher.getId()));

        // then
        VoucherResponse voucherResponse = new VoucherResponse(voucher);

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(voucherResponse.getId()))
                .andExpect(jsonPath("minPrice").value(voucherResponse.getMinPrice()))
                .andExpect(jsonPath("title").value(voucherResponse.getTitle()))
                .andExpect(jsonPath("imageUrl").value(voucherResponse.getImageUrl()))
                .andExpect(jsonPath("description").value(voucherResponse.getDescription()))
                .andExpect(jsonPath("caution").value(voucherResponse.getCaution()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("기프티콘 id")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("기프티콘 id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("기프티콘 타이틀"),
                                fieldWithPath("minPrice").type(JsonFieldType.NUMBER).description("기프티콘 최소 가격"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("기프티콘 이미지 URL"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("caution").type(JsonFieldType.STRING).description("사용 시 유의사항")
                        ))
                );
    }

    @Test
    void findAll() throws Exception {
        // given
        for (int i = 1; i <= 20; i++) {
            Voucher voucher = voucherRepository.save(Voucher.builder()
                    .title("Voucher" + i)
                    .description("This is Voucher" + i + ".")
                    .caution("This voucher is awesome.")
                    .build());

            VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("voucherImage" + i + ".png")
                    .build());

            voucher.updateVoucherImage(voucherImage);
        }

        // when
        String uri = UriComponentsBuilder.fromPath("/api/vouchers")
                .build()
                .toString();

        ResultActions response = mockMvc.perform(get(uri));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pagingQueryParameters(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("기프티콘 종류의 id"),
                                fieldWithPath("[].minPrice").type(JsonFieldType.NUMBER).description("기프티콘 종류의 최소 가격"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("기프티콘 종류의 타이틀"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("기프티콘 종류의 이미지"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("[].caution").type(JsonFieldType.STRING).description("사용 시 유의사항")
                        ))
                );
    }

    @Test
    void findAllByBrandId() throws Exception {
        // given
        Brand brand = Brand.builder()
                .name("Test Brand")
                .build();

        Brand brandSaved = brandRepository.save(brand);

        for (int i = 1; i <= 20; i++) {
            Voucher voucher = voucherRepository.save(Voucher.builder()
                    .title("Voucher " + i)
                    .description("This is Voucher" + i + ".")
                    .caution("This voucher is awesome.")
                    .build());

            VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("voucherImage" + i + ".png")
                    .build());

            voucher.updateVoucherImage(voucherImage);
            voucher.updateBrand(brandSaved);
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/vouchers")
                .queryParam("brandId", String.valueOf(brandSaved.getId())));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pagingQueryParameters(
                                parameterWithName("brandId").description("브랜드 id")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("기프티콘 종류의 id"),
                                fieldWithPath("[].minPrice").type(JsonFieldType.NUMBER).description("기프티콘 종류의 최소 가격"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("기프티콘 종류의 타이틀"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("기프티콘 종류의 이미지"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("[].caution").type(JsonFieldType.STRING).description("사용 시 유의사항")
                        ))
                );
    }

    @Test
    void findSellingListByVoucherId() throws Exception {
        // given
        Voucher voucher = Voucher.builder()
                .title("Americano T")
                .description("This is Americano T")
                .caution("This voucher is from Starbucks.")
                .build();

        Voucher voucherSaved = voucherRepository.save(voucher);

        for (int i = 1; i <= 10; i++) {
            VoucherForSale voucherForSale = voucherForSaleRepository.save(
                    VoucherForSale.builder()
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

            voucherForSale.updateVoucherForSaleImage(voucherForSaleImage);
            voucher.addVoucherForSale(voucherForSale);
            voucherForSale.updateStatus(FOR_SALE);
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
                                parameterWithName("id").description("판매 기프티콘 id")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("판매 기프티콘 id"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("판매 기프티콘 가격"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("판매 기프티콘 타이틀"),
                                fieldWithPath("[].barcode").type(JsonFieldType.STRING).description("판매 기프티콘 바코드"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("판매 기프티콘 이미지"),
                                fieldWithPath("[].expDate").type(JsonFieldType.STRING).description("판매 기프티콘 유효기한"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("판매 기프티콘 상태"),
                                fieldWithPath("[].saleRequestedDate").type(JsonFieldType.STRING).description("기프티콘 판매 요청일자")
                        ))
                );
    }

    @Test
    void update() throws Exception {
        // given
        Voucher voucher = voucherRepository.save(Voucher.builder()
                .title("oldTitle")
                .description("This is an old voucher.")
                .caution("This voucher will be updated.")
                .build());

        VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("oldVoucherImage.jpg")
                .build());

        voucher.updateVoucherImage(voucherImage);

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
        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(voucher.getId()))
                .andExpect(jsonPath("minPrice").value(0L))
                .andExpect(jsonPath("imageUrl").value(voucher.getImageUrl()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("기프티콘 id")
                        ),
                        requestParts(
                                partWithName("title").optional().description("수정할 기프티콘 종류의 타이틀"),
                                partWithName("description").optional().description("수정할 기프티콘 종류의 상세설명"),
                                partWithName("caution").optional().description("수정할 기프티콘 종류의 주의사항"),
                                partWithName("imageFile").optional().description("수정할 기프티콘 종류의 이미지 파일")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("수정된 기프티콘 종류의 id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("수정된 기프티콘 종류의 타이틀"),
                                fieldWithPath("minPrice").type(JsonFieldType.NUMBER).description("수정된 기프티콘 종류의 최소 가격"),
                                fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("수정된 기프티콘 종류의 이미지"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("caution").type(JsonFieldType.STRING).description("사용 시 유의사항")
                        ))
                );
    }

    @Test
    void deleteOne() throws Exception {
        // given
        Voucher voucher = voucherRepository.save(Voucher.builder()
                .title("voucher")
                .build());

        VoucherImage voucherImage = voucherImageRepository.save(VoucherImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("voucherImage.jpg")
                .build());

        voucher.updateVoucherImage(voucherImage);

        // when
        ResultActions response = mockMvc.perform(delete("/api/vouchers/{id}", voucher.getId()));

        // then
        response.andExpect(status().isNoContent())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("기프티콘 종류의 id")
                        ))
                );

        List<Voucher> voucherList = voucherRepository.findAll();
        List<VoucherForSale> voucherForSaleList = voucherForSaleRepository.findAll();
        assertThat(voucherList).isEmpty();
        assertThat(voucherForSaleList).isEmpty();
    }
}