package com.givemecon.controller.service;

import com.givemecon.controller.IntegrationTest;
import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.category.Category;
import com.givemecon.domain.entity.voucherkind.VoucherKind;
import com.givemecon.domain.entity.voucherkind.VoucherKindImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.UriComponentsBuilder;

import static com.givemecon.application.dto.VoucherKindDto.VoucherKindDetailResponse;
import static com.givemecon.util.ApiDocumentUtils.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VoucherKindControllerTest extends IntegrationTest {

    Brand brand;

    @BeforeEach
    void setup() {
        Category category = categoryRepository.save(Category.builder()
                .name("category")
                .build());

        brand = brandRepository.save(Brand.builder()
                .name("Starbucks")
                .category(category)
                .build());
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
        VoucherKindDetailResponse voucherKindDetailResponse = new VoucherKindDetailResponse(voucherKind);

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(voucherKindDetailResponse.getId()))
                .andExpect(jsonPath("minPrice").value(voucherKindDetailResponse.getMinPrice()))
                .andExpect(jsonPath("title").value(voucherKindDetailResponse.getTitle()))
                .andExpect(jsonPath("imageUrl").value(voucherKindDetailResponse.getImageUrl()))
                .andExpect(jsonPath("description").value(voucherKindDetailResponse.getDescription()))
                .andExpect(jsonPath("caution").value(voucherKindDetailResponse.getCaution()))
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
        for (int i = 1; i <= 5; i++) {
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
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("기프티콘 종류의 id"),
                                fieldWithPath("[].minPrice").type(JsonFieldType.NUMBER).description("기프티콘 종류의 최소 가격"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("기프티콘 종류의 타이틀"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("기프티콘 종류의 이미지")
                        ))
                );
    }

    @Test
    void findAllByBrandId() throws Exception {
        // given
        for (int i = 1; i <= 5; i++) {
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
                        queryParameters(
                                parameterWithName("brandId").description("브랜드 id")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("기프티콘 종류의 id"),
                                fieldWithPath("[].minPrice").type(JsonFieldType.NUMBER).description("기프티콘 종류의 최소 가격"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("기프티콘 종류의 타이틀"),
                                fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("기프티콘 종류의 이미지")
                        ))
                );
    }
}