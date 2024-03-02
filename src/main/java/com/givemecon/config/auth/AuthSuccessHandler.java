package com.givemecon.config.auth;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.config.auth.jwt.JwtTokenProvider;
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
import java.nio.charset.StandardCharsets;

import static com.givemecon.config.auth.enums.ClientUrl.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        log.info("--- In AuthSuccessHandler ---");
        Member member = memberRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        log.info("Login username = {}", member.getUsername());

        TokenInfo tokenInfo = jwtTokenProvider.getTokenInfo(member);

        String url = UriComponentsBuilder.fromUriString(LOGIN_URL.getUrl())
                .queryParam("grantType", tokenInfo.getGrantType())
                .queryParam("accessToken", tokenInfo.getAccessToken())
                .queryParam("refreshToken", tokenInfo.getRefreshToken())
                .queryParam("username", member.getUsername())
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, url);
    }
}
