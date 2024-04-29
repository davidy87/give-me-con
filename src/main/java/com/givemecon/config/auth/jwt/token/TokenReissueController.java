package com.givemecon.config.auth.jwt.token;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.util.error.ErrorCode;
import com.givemecon.util.exception.concrete.InvalidTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.givemecon.config.enums.JwtAuthHeader.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TokenReissueController {

    private final TokenReissueService tokenReissueService;

    @GetMapping("/api/auth/success")
    public TokenInfo afterAuthSuccess(HttpSession session) {
        TokenInfo tokenInfo = null;

        if (session != null) {
            tokenInfo = (TokenInfo) session.getAttribute("tokenInfo");
            session.invalidate();
        }

        if (tokenInfo == null) {
            throw new InvalidTokenException(ErrorCode.TOKEN_NOT_AUTHENTICATED);
        }

        return tokenInfo;
    }

    @GetMapping("/api/auth/reissue")
    public TokenInfo reissueToken(HttpServletRequest request) {
        String refreshTokenHeader = request.getHeader(REFRESH_TOKEN.getName());
        return tokenReissueService.reissueToken(refreshTokenHeader);
    }
}
