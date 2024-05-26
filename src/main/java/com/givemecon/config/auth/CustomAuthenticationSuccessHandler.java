package com.givemecon.config.auth;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.token.JwtTokenService;
import com.givemecon.config.auth.util.ClientUrlProperties;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.givemecon.config.enums.OAuth2ParameterName.*;
import static com.givemecon.domain.member.MemberDto.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final int SESSION_DURATION = 10;

    private final MemberRepository memberRepository;

    private final JwtTokenService jwtTokenService;

    private final ClientUrlProperties clientUrlProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Member member = memberRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        TokenInfo tokenInfo = jwtTokenService.getTokenInfo(new TokenRequest(member));
        String authorizationCode = UUID.randomUUID().toString();

        // TokenInfo를 session에 임시 보관
        Optional.ofNullable(request.getSession())
                .ifPresent(session -> {
                    session.setAttribute(authorizationCode, tokenInfo);
                    session.setMaxInactiveInterval(SESSION_DURATION);
                });

        String redirectUrl =
                UriComponentsBuilder.fromHttpUrl(clientUrlProperties.getLoginUrl())
                        .queryParam(SUCCESS.getName())
                        .queryParam(AUTHORIZATION_CODE.getName(), authorizationCode)
                        .build()
                        .toString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
