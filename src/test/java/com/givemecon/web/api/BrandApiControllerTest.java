package com.givemecon.web.api;

import com.givemecon.s3.S3MockConfig;
import com.givemecon.domain.brand.Brand;
import com.givemecon.domain.brand.BrandIcon;
import com.givemecon.domain.brand.BrandIconRepository;
import com.givemecon.domain.brand.BrandRepository;
import com.givemecon.domain.category.Category;
import com.givemecon.domain.category.CategoryRepository;
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
import java.util.List;

import static com.givemecon.web.ApiDocumentUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Import(S3MockConfig.class)
@Transactional
@WithMockUser(roles = "ADMIN")
@SpringBootTest
class BrandApiControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    BrandIconRepository brandIconRepository;

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
        Category categorySaved = categoryRepository.save(Category.builder()
                .name("coffee")
                .build());

        String name = "Starbucks";
        String icon = "starbucks.jpg";
        MockMultipartFile iconFile = new MockMultipartFile("icon", icon, "image/jpg", icon.getBytes());

        // when
        ResultActions result = mockMvc.perform(multipart("/api/brands")
                .file(iconFile)
                .part(new MockPart("categoryId", String.valueOf(categorySaved.getId()).getBytes(StandardCharsets.UTF_8)))
                .part(new MockPart("name", name.getBytes(StandardCharsets.UTF_8)))
        );

        // then
        List<Brand> brandList = brandRepository.findAll();

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(brandList.get(0).getId()))
                .andExpect(jsonPath("name").value(brandList.get(0).getName()))
                .andExpect(jsonPath("icon").value(brandList.get(0).getBrandIcon().getImageUrl()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParts(
                                partWithName("categoryId").description("설정할 카테고리 id"),
                                partWithName("name").description("저장할 브랜드 이름"),
                                partWithName("icon").description("저장할 브랜드 아이콘 파일")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("저장된 브랜드 id"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("저장된 브랜드 이름"),
                                fieldWithPath("icon").type(JsonFieldType.STRING).description("저장된 브랜드 아이콘")
                        ))
                );
    }

    @Test
    void findAll() throws Exception {
        // given
        for (int i = 1; i <= 5; i++) {
            Brand brand = brandRepository.save(Brand.builder()
                    .name("brand" + i)
                    .build());

            BrandIcon brandIcon = brandIconRepository.save(BrandIcon.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("brand_icon_" + i + ".jpg")
                    .build());

            brand.setBrandIcon(brandIcon);
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/brands"));

        // then
        response.andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("브랜드 id"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("브랜드명"),
                                fieldWithPath("[].icon").type(JsonFieldType.STRING).description("브랜드 아이콘")
                        ))
                );
    }

    @Test
    void findAllByCategoryId() throws Exception {
        // given
        String name = "Bubble Tea";
        Category category = categoryRepository.save(Category.builder()
                .name(name)
                .build());

        for (int i = 1; i <= 5; i++) {
            Brand brand = brandRepository.save(Brand.builder()
                    .name(name + "Brand " + i)
                    .build());

            BrandIcon brandIcon = brandIconRepository.save(BrandIcon.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("brand_icon_" + i + ".jpg")
                    .build());

            brand.setBrandIcon(brandIcon);
            category.addBrand(brand);
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/brands?categoryId={categoryId}", category.getId()));

        // then
        response.andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("categoryId").description("카테고리 id")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("브랜드 id"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("브랜드명"),
                                fieldWithPath("[].icon").type(JsonFieldType.STRING).description("브랜드 아이콘")
                        ))
                );

        List<Brand> brandList = brandRepository.findAll();

        for (Brand brand : brandList) {
            assertThat(brand.getCategory()).isEqualTo(category);
        }
    }

    @Test
    void update() throws Exception {
        // given
        Category categorySaved = categoryRepository.save(Category.builder()
                .name("category")
                .build());

        Brand brand = brandRepository.save(Brand.builder()
                .name("oldBrand")
                .build());

        BrandIcon brandIcon = brandIconRepository.save(BrandIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("brand_icon.jpg")
                .build());

        brand.setBrandIcon(brandIcon);

        String newName = "newBrand";
        MockMultipartFile newIconFile = new MockMultipartFile(
                "icon",
                "new_brand_icon.jpg",
                "image/jpg",
                "new_brand_icon.jpg".getBytes());

        // when
        ResultActions response = mockMvc.perform(multipart("/api/brands/{id}", brand.getId())
                .file(newIconFile)
                .part(new MockPart("categoryId", String.valueOf(categorySaved.getId()).getBytes(StandardCharsets.UTF_8)))
                .part(new MockPart("name", newName.getBytes(StandardCharsets.UTF_8)))
        );

        // then
        List<Brand> brandList = brandRepository.findAll();

        response.andExpect(status().isOk())
                .andExpect(jsonPath("id").value(brandList.get(0).getId()))
                .andExpect(jsonPath("name").value(brandList.get(0).getName()))
                .andExpect(jsonPath("icon").value(brandList.get(0).getBrandIcon().getImageUrl()))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("브랜드 id")
                        ),
                        requestParts(
                                partWithName("categoryId").description("변경할 카테고리 id").optional(),
                                partWithName("name").description("변경할 브랜드 이름").optional(),
                                partWithName("icon").description("변경할 브랜드 아이콘 파일").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("변경된 브랜드 id"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("변경된 브랜드명"),
                                fieldWithPath("icon").type(JsonFieldType.STRING).description("변경된 브랜드 아이콘")
                        ))
                );
    }

    @Test
    void deleteOne() throws Exception {
        // given
        String name = "Starbucks";
        String icon = "starbucks.jpg";
        Brand brand = Brand.builder()
                .name(name)
                .build();

        Long id = brandRepository.save(brand).getId();

        // when
        ResultActions response = mockMvc.perform(delete("/api/brands/{id}", id));

        // then
        response.andExpect(status().isNoContent())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("브랜드 id")
                        ))
                );

        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).isEmpty();
    }
}