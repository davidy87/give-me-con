package com.givemecon.config.auth.jwt.token;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.util.exception.concrete.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
        Member member = validateRefreshToken(refreshToken);

        // Access token과 refresh token을 같이 재발급한다.
        return jwtTokenService.getTokenInfo(new TokenRequest(member));
    }

    /**
     * Refresh token 검증에 성공하면 token 재발급 요청자(Member)를 찾아 반환
     * @param refreshToken 검증할 refresh token
     * @return {@link Member} (token 재발급 요청자에 해당하는 Member entity)
     */
    private Member validateRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new InvalidTokenException(TOKEN_NOT_AUTHENTICATED);
        }

        RefreshToken tokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException(REFRESH_TOKEN_EXPIRED));

        try {
            jwtTokenService.getClaims(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException(REFRESH_TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new InvalidTokenException(TOKEN_NOT_AUTHENTICATED);
        } finally {
            refreshTokenRepository.delete(tokenEntity);
        }

        return memberRepository.findById(Long.valueOf(tokenEntity.getMemberId()))
                .orElseThrow(() -> new InvalidTokenException(TOKEN_NOT_AUTHENTICATED));
    }

    public void save(Long memberId, String refreshToken) {
        refreshTokenRepository.save(new RefreshToken(String.valueOf(memberId), refreshToken));
    }
}
