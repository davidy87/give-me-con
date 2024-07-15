package com.givemecon.common.auth.handler;

import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.common.auth.jwt.token.JwtTokenService;
import com.givemecon.common.auth.util.ClientUrlProperties;
import com.givemecon.common.exception.concrete.EntityNotFoundException;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

import static com.givemecon.application.dto.MemberDto.TokenRequest;
import static com.givemecon.common.auth.enums.OAuth2ParameterName.AUTHORIZATION_CODE;
import static com.givemecon.common.auth.enums.OAuth2ParameterName.SUCCESS;

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
        HttpSession session = request.getSession();
        session.setAttribute(authorizationCode, tokenInfo);
        session.setMaxInactiveInterval(SESSION_DURATION);

        String redirectUrl =
                UriComponentsBuilder.fromHttpUrl(clientUrlProperties.getLoginUrl())
                        .queryParam(SUCCESS.getName())
                        .queryParam(AUTHORIZATION_CODE.getName(), authorizationCode)
                        .build()
                        .toString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
