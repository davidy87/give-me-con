package com.givemecon.controller;

import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.voucher.Voucher;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.entity.voucherkind.VoucherKindImage;
import com.givemecon.domain.repository.brand.BrandRepository;
import com.givemecon.domain.repository.category.CategoryRepository;
import com.givemecon.domain.repository.voucher.VoucherRepository;
import com.givemecon.domain.repository.voucherkind.VoucherKindImageRepository;
import com.givemecon.domain.repository.voucherkind.VoucherKindRepository;
import com.givemecon.infrastructure.s3.S3MockConfig;
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
import java.util.List;

import static com.givemecon.application.dto.VoucherKindDto.VoucherKindResponse;
import static com.givemecon.util.ApiDocumentUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
class VoucherKindControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    VoucherKindRepository voucherKindRepository;

    @Autowired
    VoucherKindImageRepository voucherKindImageRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    S3Client s3Client;

    @Autowired
    S3Mock s3Mock;

    @Value("${spring.cloud.aws.s3.bucket}")
    String bucketName;

    Brand brand;

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

        Category category = categoryRepository.save(Category.builder()
                .name("category")
                .build());

        brand = brandRepository.save(Brand.builder()
                .name("Starbucks")
                .category(category)
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

        // when
        ResultActions response = mockMvc.perform(multipart("/api/voucher-kinds")
                .file(imageFile)
                .part(new MockPart("brandId", brand.getId().toString().getBytes()))
                .part(new MockPart("title", title.getBytes()))
        );

        // then
        List<VoucherKind> voucherKindList = voucherKindRepository.findAll();
        assertThat(voucherKindList).isNotEmpty();

        VoucherKind voucherKind = voucherKindList.get(0);
        assertThat(voucherKind.getBrand()).isEqualTo(brand);

        VoucherKindResponse voucherKindResponse = new VoucherKindResponse(voucherKind);

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(voucherKindResponse.getId()))
                .andExpect(jsonPath("minPrice").value(0L))
                .andExpect(jsonPath("title").value(voucherKindResponse.getTitle()))
                .andExpect(jsonPath("description").value(voucherKindResponse.getDescription()))
                .andExpect(jsonPath("caution").value(voucherKindResponse.getCaution()))
                .andExpect(jsonPath("imageUrl").value(voucherKindResponse.getImageUrl()))
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
        VoucherKindImage voucherKindImage = voucherKindImageRepository.save(VoucherKindImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("ice_cream_cake.png")
                .build());

        VoucherKind voucherKind = voucherKindRepository.save(VoucherKind.builder()
                .title("Ice Cream Cake")
                .description("This is Ice Cream Cake.")
                .caution("This Ice Cream Cake is extremely cold.")
                .voucherKindImage(voucherKindImage)
                .build());

        // when
        ResultActions response = mockMvc.perform(get("/api/voucher-kinds/{id}", voucherKind.getId()));

        // then
        VoucherKindResponse voucherKindResponse = new VoucherKindResponse(voucherKind);

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(voucherKindResponse.getId()))
                .andExpect(jsonPath("minPrice").value(voucherKindResponse.getMinPrice()))
                .andExpect(jsonPath("title").value(voucherKindResponse.getTitle()))
                .andExpect(jsonPath("imageUrl").value(voucherKindResponse.getImageUrl()))
                .andExpect(jsonPath("description").value(voucherKindResponse.getDescription()))
                .andExpect(jsonPath("caution").value(voucherKindResponse.getCaution()))
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
            VoucherKindImage voucherKindImage = voucherKindImageRepository.save(VoucherKindImage.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("voucherKindImage" + i + ".png")
                    .build());

            voucherKindRepository.save(VoucherKind.builder()
                    .title("VoucherKind" + i)
                    .description("This is VoucherKind" + i + ".")
                    .caution("This voucherKind is awesome.")
                    .voucherKindImage(voucherKindImage)
                    .build());
        }

        // when
        String uri = UriComponentsBuilder.fromPath("/api/voucher-kinds")
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
        for (int i = 1; i <= 20; i++) {
            VoucherKindImage voucherKindImage = voucherKindImageRepository.save(VoucherKindImage.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("voucherKindImage" + i + ".png")
                    .build());

            voucherKindRepository.save(VoucherKind.builder()
                    .title("VoucherKind " + i)
                    .description("This is VoucherKind" + i + ".")
                    .caution("This voucherKind is awesome.")
                    .voucherKindImage(voucherKindImage)
                    .brand(brand)
                    .build());
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/voucher-kinds")
                .queryParam("brandId", String.valueOf(brand.getId())));

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
    void update() throws Exception {
        // given
        VoucherKindImage voucherKindImage = voucherKindImageRepository.save(VoucherKindImage.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("oldVoucherImage.jpg")
                .build());

        VoucherKind voucherKind = voucherKindRepository.save(VoucherKind.builder()
                .title("oldTitle")
                .description("This is an old voucherKind.")
                .caution("This voucherKind will be updated.")
                .voucherKindImage(voucherKindImage)
                .build());

        String newTitle = "newTitle";
        MockMultipartFile imageFileToUpdate = new MockMultipartFile(
                "imageFile",
                "oldImage.png",
                "image/png",
                "oldImage.png".getBytes());

        // when
        ResultActions response = mockMvc.perform(multipart("/api/voucher-kinds/{id}", voucherKind.getId())
                .file(imageFileToUpdate)
                .part(new MockPart("title", newTitle.getBytes(StandardCharsets.UTF_8)))
        );

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(voucherKind.getId()))
                .andExpect(jsonPath("minPrice").value(0L))
                .andExpect(jsonPath("imageUrl").value(voucherKind.getImageUrl()))
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
        VoucherKind voucherKind = voucherKindRepository.save(VoucherKind.builder()
                .title("title")
                .description("description")
                .caution("caution")
                .build());

        // when
        ResultActions response = mockMvc.perform(delete("/api/voucher-kinds/{id}", voucherKind.getId()));

        // then
        response.andExpect(status().isNoContent())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("기프티콘 종류의 id")
                        ))
                );

        List<VoucherKind> voucherKindList = voucherKindRepository.findAll();
        List<Voucher> voucherList = voucherRepository.findAll();
        assertThat(voucherKindList).isEmpty();
        assertThat(voucherList).isEmpty();
    }
}