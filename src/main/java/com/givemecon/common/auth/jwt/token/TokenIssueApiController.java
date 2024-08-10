package com.givemecon.common.auth.jwt.token;

import com.givemecon.common.auth.dto.TokenInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.givemecon.common.auth.enums.JwtAuthHeader.REFRESH_TOKEN;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class TokenIssueApiController {

    private final TokenIssueService tokenIssueService;

    @GetMapping("/success")
    public TokenInfo issueToken(@RequestParam String authorizationCode) {
        return tokenIssueService.issueToken(authorizationCode);
    }

    @GetMapping("/reissue")
    public TokenInfo reissueToken(HttpServletRequest request) {
        String refreshTokenHeader = request.getHeader(REFRESH_TOKEN.getName());
        return tokenIssueService.reissueToken(refreshTokenHeader);
    }
}
