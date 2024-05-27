package com.givemecon.config.auth.jwt.token;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.util.exception.concrete.InvalidTokenException;
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

import static com.givemecon.config.enums.JwtAuthHeader.*;
import static com.givemecon.util.error.ErrorCode.*;

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
                .orElseThrow(() -> new InvalidTokenException(TOKEN_NOT_AUTHENTICATED));
    }

    private TokenInfo getTokenInfoFromSession(HttpSession session, String authorizationCode) {
        if (!StringUtils.hasText(authorizationCode)) {
            session.invalidate();
            return null;
        }

        TokenInfo tokenInfo = (TokenInfo) session.getAttribute(authorizationCode);
        session.invalidate();

        return tokenInfo;
    }

    @GetMapping("/reissue")
    public TokenInfo reissueToken(HttpServletRequest request) {
        String refreshTokenHeader = request.getHeader(REFRESH_TOKEN.getName());
        return tokenReissueService.reissueToken(refreshTokenHeader);
    }
}
