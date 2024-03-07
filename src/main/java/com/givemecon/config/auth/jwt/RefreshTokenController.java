package com.givemecon.config.auth.jwt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RefreshTokenController {

    private final JwtTokenProvider jwtTokenProvider;

    private final RefreshTokenService refreshTokenService;

    @GetMapping("/api/auth/refresh")
    public String reissueAccessToken(HttpServletRequest request) {
        log.info("--- In RefreshTokenController ---");
        String refreshToken = jwtTokenProvider.retrieveToken(request);
        return refreshTokenService.reissueAccessToken(refreshToken);
    }
}
