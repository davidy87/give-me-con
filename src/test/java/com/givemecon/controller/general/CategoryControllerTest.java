package com.givemecon.controller.general;

import com.givemecon.controller.IntegrationTest;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.category.CategoryIcon;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static com.givemecon.util.ApiDocumentUtils.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerTest extends IntegrationTest {

    @Test
    void findAll() throws Exception {
        // given
        for (int i = 1; i <= 5; i++) {
            CategoryIcon categoryIcon = categoryIconRepository.save(CategoryIcon.builder()
                    .imageKey("imageKey" + i)
                    .imageUrl("imageUrl" + i)
                    .originalName("categoryIcon" + i + ".png")
                    .build());

            Category category = Category.builder()
                    .name("category" + i)
                    .categoryIcon(categoryIcon)
                    .build();

            categoryRepository.save(category);
        }

        // when
        ResultActions response = mockMvc.perform(get("/api/categories"));

        // then
        response.andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("카테고리 id"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("카테고리 이름"),
                                fieldWithPath("[].iconUrl").type(JsonFieldType.STRING).description("카테고리 아이콘")
                        ))
                );
    }
}