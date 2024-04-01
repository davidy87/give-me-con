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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static com.givemecon.domain.member.MemberDto.*;
import static java.nio.charset.StandardCharsets.*;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;

    private final JwtTokenService jwtTokenService;

    private final ClientUrlProperties clientUrlProperties;

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Member member = memberRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

//        log.info("[Log] Login Username = {}", member.getUsername());
        TokenInfo tokenInfo = jwtTokenService.getTokenInfo(new TokenRequest(member));

        String url = UriComponentsBuilder.fromHttpUrl(clientUrlProperties.getLoginUrl())
                .queryParam(GRANT_TYPE, tokenInfo.getGrantType())
                .queryParam(ACCESS_TOKEN, tokenInfo.getAccessToken())
                .queryParam(REFRESH_TOKEN, tokenInfo.getRefreshToken())
                .queryParam(USERNAME, member.getUsername())
                .encode(UTF_8)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, url);
    }
}
