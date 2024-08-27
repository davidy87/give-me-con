package com.givemecon.controller.service;

import com.givemecon.application.dto.MemberDto;
import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.controller.ControllerTestEnvironment;
import com.givemecon.domain.entity.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.givemecon.application.exception.errorcode.MemberErrorCode.INVALID_MEMBER_ID;
import static com.givemecon.common.auth.enums.JwtAuthHeader.AUTHORIZATION;
import static com.givemecon.domain.entity.member.Role.USER;
import static com.givemecon.util.ApiDocumentUtils.*;
import static com.givemecon.util.TokenHeaderUtils.getAccessTokenHeader;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends ControllerTestEnvironment {

    Member user;

    TokenInfo tokenInfo;

    @BeforeEach
    void setup() {
        user = memberRepository.save(Member.builder()
                .email("user@gmail.com")
                .username("user")
                .role(USER)
                .build());

        tokenInfo = jwtTokenService.getTokenInfo(new MemberDto.TokenRequest(user));
    }

    @Test
    void deleteOne() throws Exception {
        // given
        Long userId = user.getId();

        // when
        ResultActions response = mockMvc.perform(delete("/api/members/{id}", userId)
                .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

        // then
        response.andExpect(status().isNoContent())
                .andDo(document("{class-name}/{method-name}",
                        getDocumentRequestWithAuth(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("회원 id")
                        ))
                );
    }

    @Nested
    class ExceptionTest {

        @Test
        void memberExceptionTest() throws Exception {
            // given
            Long invalidUserId = 1L;

            // when
            ResultActions response =
                    mockMvc.perform(MockMvcRequestBuilders.delete("/api/members/{id}" , invalidUserId)
                            .header(AUTHORIZATION.getName(), getAccessTokenHeader(tokenInfo)));

            // then
            response.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("error.status").value(INVALID_MEMBER_ID.getStatus()))
                    .andExpect(jsonPath("error.code").value(INVALID_MEMBER_ID.getCode()))
                    .andExpect(jsonPath("error.message").value(INVALID_MEMBER_ID.getMessage()));
        }
    }
}