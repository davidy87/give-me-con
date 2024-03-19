package com.givemecon.config.auth.jwt.token;

import com.givemecon.domain.member.Member;
import com.givemecon.domain.member.MemberRepository;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import com.givemecon.util.exception.concrete.InvalidTokenException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.givemecon.domain.member.MemberDto.*;
import static com.givemecon.util.error.ErrorCode.*;

@RequiredArgsConstructor
@Service
@Transactional
public class TokenReissueService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final MemberRepository memberRepository;

    private final JwtUtils jwtUtils;

    public String reissueAccessToken(String tokenHeader) {
        String refreshToken = jwtUtils.retrieveToken(tokenHeader);

        if (refreshToken == null) {
            throw new InvalidTokenException(TOKEN_NOT_AUTHENTICATED);
        }

        RefreshToken tokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException(RefreshToken.class));

        Member member = memberRepository.findById(tokenEntity.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException(Member.class));

        try {
            jwtUtils.getClaims(tokenEntity.getRefreshToken());
        } catch (JwtException e) {
            refreshTokenRepository.delete(tokenEntity);
            throw new InvalidTokenException(REFRESH_TOKEN_EXPIRED);
        }

        return jwtUtils.generateAccessToken(new TokenRequest(member));
    }

    public void save(Long memberId, String refreshToken) {
        refreshTokenRepository.save(new RefreshToken(memberId, refreshToken));
    }
}