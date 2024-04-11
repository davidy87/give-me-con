package com.givemecon.config.auth.jwt.token;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.util.exception.concrete.InvalidTokenException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.domain.member.MemberDto.*;
import static com.givemecon.util.error.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TokenReissueService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final MemberRepository memberRepository;

    private final JwtTokenService jwtTokenService;

    public TokenInfo reissueToken(String tokenHeader) {
        String refreshToken = jwtTokenService.retrieveToken(tokenHeader);

        if (refreshToken == null) {
            throw new InvalidTokenException(TOKEN_NOT_AUTHENTICATED);
        }

        RefreshToken tokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException(REFRESH_TOKEN_EXPIRED));

        Member member = memberRepository.findById(Long.valueOf(tokenEntity.getMemberId()))
                .orElseThrow(() -> new InvalidTokenException(TOKEN_NOT_AUTHENTICATED));

        try {
            jwtTokenService.getClaims(tokenEntity.getRefreshToken());
        } catch (JwtException e) {
            refreshTokenRepository.delete(tokenEntity);
            throw new InvalidTokenException(REFRESH_TOKEN_EXPIRED);
        }

        // Access Token 재발급 후, Refresh Token도 같이 재발급한다.
        return jwtTokenService.getTokenInfo(new TokenRequest(member));
    }

    public void save(Long memberId, String refreshToken) {
        refreshTokenRepository.save(new RefreshToken(String.valueOf(memberId), refreshToken));
    }
}
