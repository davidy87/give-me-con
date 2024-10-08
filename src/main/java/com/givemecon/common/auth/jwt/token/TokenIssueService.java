package com.givemecon.common.auth.jwt.token;

import com.givemecon.common.auth.dto.TokenInfo;
import com.givemecon.common.auth.jwt.exception.AuthenticationException;
import com.givemecon.common.auth.jwt.exception.InvalidTokenException;
import com.givemecon.domain.entity.member.Member;
import com.givemecon.domain.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static com.givemecon.application.dto.MemberDto.TokenRequest;
import static com.givemecon.common.auth.jwt.exception.TokenErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TokenIssueService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final RedisTemplate<String, TokenInfo> redisTemplate;

    private final MemberRepository memberRepository;

    private final JwtTokenService jwtTokenService;

    public TokenInfo issueToken(String authorizationCode) {
        return Optional.ofNullable(getTokenInfo(authorizationCode))
                .orElseThrow(() -> new AuthenticationException(INVALID_AUTHORIZATION_CODE));
    }

    private TokenInfo getTokenInfo(String authorizationCode) {
        return Optional.ofNullable(authorizationCode)
                .filter(StringUtils::hasText)
                .map(authCode -> redisTemplate.opsForValue().get(authorizationCode))
                .orElse(null);
    }

    public TokenInfo reissueToken(String tokenHeader) {
        String refreshToken = jwtTokenService.retrieveToken(tokenHeader);
        Member member = findRefreshTokenOwner(refreshToken);

        // Access token과 refresh token을 같이 재발급한다.
        return jwtTokenService.getTokenInfo(new TokenRequest(member));
    }

    /**
     * Refresh token 검증에 성공하면 token 소유자(Member)를 찾아 반환
     * @param refreshToken 검증할 refresh token
     * @return {@link Member} (token 소유자에 해당하는 Member entity)
     */
    private Member findRefreshTokenOwner(String refreshToken) {
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
