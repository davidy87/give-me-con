package com.givemecon.common.auth.jwt.token;

import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.common.exception.concrete.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.givemecon.common.auth.enums.JwtAuthHeader.REFRESH_TOKEN;
import static com.givemecon.common.error.GlobalErrorCode.INVALID_AUTHORIZATION_CODE;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class TokenIssueApiController {

    private final TokenReissueService tokenReissueService;

    @GetMapping("/success")
    public TokenInfo issueToken(HttpSession session, @RequestParam String authorizationCode) {
        return Optional.ofNullable(session)
                .map(s -> getTokenInfoFromSession(s, authorizationCode))
                .orElseThrow(() -> new AuthenticationException(INVALID_AUTHORIZATION_CODE));
    }

    private TokenInfo getTokenInfoFromSession(HttpSession session, String authorizationCode) {
        TokenInfo tokenInfo = Optional.ofNullable(authorizationCode)
                .filter(StringUtils::hasText)
                .map(authCode -> (TokenInfo) session.getAttribute(authCode))
                .orElse(null);

        session.invalidate();
        return tokenInfo;
    }

    @GetMapping("/reissue")
    public TokenInfo reissueToken(HttpServletRequest request) {
        String refreshTokenHeader = request.getHeader(REFRESH_TOKEN.getName());
        return tokenReissueService.reissueToken(refreshTokenHeader);
    }
}
