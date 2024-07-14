package com.givemecon.config.auth.jwt.token;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.domain.member.repository.MemberRepository;
import com.givemecon.util.exception.concrete.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.*;

import static com.givemecon.config.enums.GrantType.*;
import static com.givemecon.config.enums.TokenDuration.ACCESS_TOKEN_DURATION;
import static com.givemecon.config.enums.TokenDuration.REFRESH_TOKEN_DURATION;
import static com.givemecon.domain.member.dto.MemberDto.*;
import static com.givemecon.util.error.GlobalErrorCode.TOKEN_NOT_AUTHENTICATED;

@Slf4j
@Component
public class JwtTokenService {

    private static final String CLAIM_NAME_USERNAME = "username";

    private static final String CLAIM_NAME_ROLE = "role";

    private static final String TOKEN_HEADER_DELIMITER = " ";

    private final SecretKey secretKey;

    private final RefreshTokenRepository refreshTokenRepository;

    private final MemberRepository memberRepository;

    public JwtTokenService(@Value("${jwt.secret}") String secretKey,
                           RefreshTokenRepository refreshTokenRepository,
                           MemberRepository memberRepository) {

        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * 로그인 성공 시, 응답으로 전달하는 토큰 정보
     * @param tokenRequest 토큰을 요청하는 사용자의 정보가 담긴 DTO
     * @return {@link TokenInfo} (Grant type, access token, refresh token이 담겨있는 DTO)
     */
    public TokenInfo getTokenInfo(TokenRequest tokenRequest) {
        long issuedAt = System.currentTimeMillis();
        String accessToken = generateAccessToken(tokenRequest, issuedAt);
        String refreshToken = generateRefreshToken(issuedAt);

        saveNewOrUpdateRefreshToken(String.valueOf(tokenRequest.getMemberId()), refreshToken);

        return TokenInfo.builder()
                .grantType(BEARER.getType())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(tokenRequest.getUsername())
                .authority(tokenRequest.getAuthority())
                .build();
    }

    private void saveNewOrUpdateRefreshToken(String memberId, String refreshToken) {
        refreshTokenRepository.findByMemberId(memberId)
                .ifPresentOrElse(
                        entity -> {
                            entity.updateRefreshToken(refreshToken);
                            refreshTokenRepository.save(entity);
                        },
                        () -> refreshTokenRepository.save(new RefreshToken(memberId, refreshToken))
                );
    }

    private String generateRefreshToken(long issuedAt) {
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(issuedAt))
                .setExpiration(new Date(issuedAt + REFRESH_TOKEN_DURATION.toMillis()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateAccessToken(TokenRequest tokenRequest, long issuedAt) {
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .claim(CLAIM_NAME_USERNAME, tokenRequest.getUsername())
                .claim(CLAIM_NAME_ROLE, tokenRequest.getRole())
                .setIssuedAt(new Date(issuedAt))
                .setExpiration(new Date(issuedAt + ACCESS_TOKEN_DURATION.toMillis()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 요청으로 전달된 토큰을 추출
     * @param tokenHeader Token 정보가 들어있는 HTTP Header (null이 전달될 수 있음)
     * @return Access token 혹은 refresh token (만약 올바르지 않은 형식의 Authentication 혹은 Refresh-Token header일 경우, 빈 문자열 반환)
     * <br>
     * <p>
     *     올바른 형태의 header 예:
     * </p>
     * <pre>
     *     (GrantType)(sp)(공백없는 문자열) => "Bearer foobar"
     * </pre>
     */
    public String retrieveToken(String tokenHeader) {
        return Optional.ofNullable(StringUtils.split(tokenHeader, TOKEN_HEADER_DELIMITER))
                .filter(headerSplit -> isTokenFormatValid(headerSplit[0], headerSplit[1]))
                .map(headerSplit -> headerSplit[1])
                .orElse("");
    }

    private boolean isTokenFormatValid(String grantType, String token) {
        return grantType.equals(BEARER.getType())
                && StringUtils.hasText(token)
                && !StringUtils.containsWhitespace(token);
    }

    /**
     * @param token 사용자의 token
     * @return {@link Authentication} claim에 담겨있는 정보를 바탕으로 만든 authentication token.
     * @throws JwtException getClaims 호출 시, token이 올바르지 않다면, JwtException을 던짐
     * @throws InvalidTokenException token의 claim에 권한 정보가 올바르지 않을 경우 던짐
     */
    public Authentication getAuthentication(String token) throws JwtException, InvalidTokenException {
        Claims claims = getClaims(token);
        String username = (String) claims.get(CLAIM_NAME_USERNAME);
        String role = (String) claims.get(CLAIM_NAME_ROLE);

        // Claim에 있는 username과 authority가 올바른지 확인
        validateClaims(username, role);

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
        UserDetails principal = new User(username, "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private void validateClaims(String username, String role) {
        if (username == null || role == null) {
            throw new InvalidTokenException(TOKEN_NOT_AUTHENTICATED);
        }

        memberRepository.findByUsername(username)
                .filter(member -> role.equals(member.getRole()))
                .orElseThrow(() -> new InvalidTokenException(TOKEN_NOT_AUTHENTICATED));
    }

    public Claims getClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
