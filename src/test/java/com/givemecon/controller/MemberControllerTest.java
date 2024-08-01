package com.givemecon.controller;

import com.givemecon.application.dto.MemberDto;
import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.common.auth.jwt.token.JwtTokenService;
import com.givemecon.common.exception.concrete.EntityNotFoundException;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static com.givemecon.common.auth.enums.JwtAuthHeader.AUTHORIZATION;
import static com.givemecon.common.error.GlobalErrorCode.ENTITY_NOT_FOUND;
import static com.givemecon.domain.entity.member.Role.USER;
import static com.givemecon.util.ApiDocumentUtils.*;
import static com.givemecon.util.TokenHeaderUtils.getAccessTokenHeader;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
@SpringBootTest
class MemberControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    JwtTokenService jwtTokenService;

    Member user;

    TokenInfo tokenInfo;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDoc) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .apply(documentationConfiguration(restDoc))
                .alwaysDo(print())
                .build();

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
            response.andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("error.status").value(ENTITY_NOT_FOUND.getStatus()))
                    .andExpect(jsonPath("error.code").value(ENTITY_NOT_FOUND.getCode()))
                    .andExpect(jsonPath("error.message")
                            .value(new EntityNotFoundException(Member.class).getMessage()));
        }
    }
}