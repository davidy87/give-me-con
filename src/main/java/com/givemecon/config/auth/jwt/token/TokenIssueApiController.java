package com.givemecon.config.auth.jwt.token;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.util.exception.concrete.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.givemecon.config.enums.JwtAuthHeader.*;
import static com.givemecon.config.enums.SessionAttribute.TOKEN_INFO;
import static com.givemecon.util.error.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class TokenIssueApiController {

    private final TokenReissueService tokenReissueService;

    @GetMapping("/success")
    public TokenInfo afterAuthSuccess(HttpSession session) {
        TokenInfo tokenInfo = null;

        if (session != null) {
            tokenInfo = (TokenInfo) session.getAttribute(TOKEN_INFO.getName());
            session.invalidate();
        }

        if (tokenInfo == null) {
            throw new InvalidTokenException(TOKEN_NOT_AUTHENTICATED);
        }

        return tokenInfo;
    }

    @GetMapping("/reissue")
    public TokenInfo reissueToken(HttpServletRequest request) {
        String refreshTokenHeader = request.getHeader(REFRESH_TOKEN.getName());
        return tokenReissueService.reissueToken(refreshTokenHeader);
    }
}
