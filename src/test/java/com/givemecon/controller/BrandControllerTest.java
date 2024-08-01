package com.givemecon.controller;

import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.brand.BrandIcon;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.category.CategoryIcon;
import com.givemecon.domain.repository.brand.BrandIconRepository;
import com.givemecon.domain.repository.brand.BrandRepository;
import com.givemecon.domain.repository.category.CategoryIconRepository;
import com.givemecon.domain.repository.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
@SpringBootTest
class BrandControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryIconRepository categoryIconRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    BrandIconRepository brandIconRepository;

    Category category;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDoc) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDoc))
                .alwaysDo(print())
                .build();

        CategoryIcon categoryIcon = categoryIconRepository.save(CategoryIcon.builder()
                .imageKey("imageKey")
                .imageUrl("imageUrl")
                .originalName("categoryIcon")
                .build());

        category = categoryRepository.save(Category.builder()
                .name("category")
                .categoryIcon(categoryIcon)
                .build());
    }

    @Test
    void findAllByCategoryId() throws Exception {
        // given
        for (int i = 1; i <= 5; i++) {
            BrandIcon brandIcon = brandIconRepository.save(BrandIcon.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("brandIcon" + i + ".jpg")
                    .build());

            brandRepository.save(Brand.builder()
                    .name("Brand " + i)
                    .brandIcon(brandIcon)
                    .category(category)
                    .build());
        }

        // when
        String uri = UriComponentsBuilder.fromPath("/api/brands")
                .queryParam("categoryId", category.getId())
                .build()
                .toString();

        ResultActions response = mockMvc.perform(get(uri));

        // then
        List<Brand> brandList = brandRepository.findAll();

        for (Brand brand : brandList) {
            assertThat(brand.getCategory()).isEqualTo(category);
        }

        response.andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pagingQueryParameters(
                                parameterWithName("categoryId").description("카테고리 id")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("브랜드 id"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("브랜드 name"),
                                fieldWithPath("[].iconUrl").type(JsonFieldType.STRING).description("브랜드 iconUrl")
                        ))
                );
    }
}