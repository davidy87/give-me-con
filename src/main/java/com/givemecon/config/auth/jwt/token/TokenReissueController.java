package com.givemecon.config.auth.jwt.token;

import com.givemecon.config.auth.dto.TokenInfo;
import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("/api/auth/reissue")
    public TokenInfo reissueToken(HttpServletRequest request) {
        String refreshTokenHeader = request.getHeader(REFRESH_TOKEN.getName());
        return tokenReissueService.reissueToken(refreshTokenHeader);
    }
}
