package com.givemecon.controller.service;

import com.givemecon.application.dto.MemberDto;
import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.controller.IntegrationTest;
import com.givemecon.domain.entity.brand.Brand;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.infrastructure.gcp.ocr.TextExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.net.URL;
import java.time.LocalDate;

import static com.givemecon.domain.entity.member.Role.USER;
import static com.givemecon.common.auth.enums.JwtAuthHeader.AUTHORIZATION;
import static com.givemecon.util.ApiDocumentUtils.getDocumentRequestWithAuth;
import static com.givemecon.util.ApiDocumentUtils.getDocumentResponse;
import static com.givemecon.util.TokenHeaderUtils.getAccessTokenHeader;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ImageTextExtractionControllerTest extends IntegrationTest {

    @MockBean
    TextExtractor textExtractor;

    TokenInfo tokenInfo;

    Brand brand;

    @BeforeEach
    void setup() {
        Member user = memberRepository.save(Member.builder()
                .email("user@gmail.com")
                .username("user")
                .role(USER)
                .build());

        tokenInfo = jwtTokenService.getTokenInfo(new MemberDto.TokenRequest(user));

        brand = brandRepository.save(Brand.builder()
                .name("Starbucks")
                .build());
    }

    @Test
    void extractTextFromImage(@Value("${gcp.test-voucher-image.url}") String imageUrl) throws Exception {
        // given
        String brandName = brand.getName();
        String expDate = LocalDate.of(2024, 8, 7).toString();
        String barcode = "1111 1111 1111";

        MockMultipartFile imageFile = new MockMultipartFile("imageFile", new URL(imageUrl).openStream());
        Mockito.when(textExtractor.extractTextFromImage(eq(imageFile.getResource())))
                .thenReturn(String.format("%s\n%s\n%s\n", brandName, expDate, barcode));

        // when
        ResultActions response =
                mockMvc.perform(multipart("/api/images/extracted-texts")
                        .file(imageFile)
                        .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo))
                        .contentType(MediaType.MULTIPART_FORM_DATA));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("brandName").value(brandName))
                .andExpect(jsonPath("expDate").value(expDate))
                .andExpect(jsonPath("barcode").value(barcode))
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        requestParts(
                                partWithName("imageFile").description("텍스트를 추출할 기프티콘 이미지 파일")
                        ),
                        responseFields(
                                fieldWithPath("brandName").type(JsonFieldType.STRING).optional().description("추출된 기프티콘 이미지의 브랜드명"),
                                fieldWithPath("expDate").type(JsonFieldType.STRING).optional().description("추출된 기프티콘 이미지의 유효기간"),
                                fieldWithPath("barcode").type(JsonFieldType.STRING).optional().description("추출된 기프티콘 이미지의 바코드")
                        ))
                );
    }
}