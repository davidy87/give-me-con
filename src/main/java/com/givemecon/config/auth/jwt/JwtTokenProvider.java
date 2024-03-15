package com.givemecon.config.auth.jwt;

import com.givemecon.config.auth.dto.TokenInfo;
import com.givemecon.domain.member.Member;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.givemecon.config.auth.enums.GrantType.*;
import static com.givemecon.util.error.ErrorCode.*;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final long ACCESS_TOKEN_DURATION = Duration.ofMinutes(30).toMillis(); // 30 mins

    private static final long REFRESH_TOKEN_DURATION = Duration.ofDays(14).toMillis(); // 14 days

    private final SecretKey secretKey;

    private final RefreshTokenRepository refreshTokenRepository;


    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            RefreshTokenRepository refreshTokenRepository) {

        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * 로그인 성공 시, 응답으로 전달하는 토큰 정보
     * @param member 로그인 성공 후 생성된 사용자 entity
     * @return {@link TokenInfo} (Grant type, access token, refresh token이 담겨있는 DTO)
     */
    @Transactional
    public TokenInfo getTokenInfo(Member member) {
        String accessToken = generateAccessToken(member);
        String refreshToken = generateRefreshToken();

        refreshTokenRepository.findByMemberId(member.getId())
                .ifPresentOrElse(
                        entity -> entity.setRefreshToken(refreshToken),
                        () -> refreshTokenRepository.save(new RefreshToken(member.getId(), refreshToken))
                );

        return TokenInfo.builder()
                .grantType(BEARER.getType())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(member.getRole())
                .build();
    }

    public String generateAccessToken(Member member) {
        String authoritiesInString = Stream.of(new SimpleGrantedAuthority(member.getRoleKey()))
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .claim("username", member.getUsername())
                .claim("authorities", authoritiesInString)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_DURATION))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken() {
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_DURATION))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * @param token 사용자의 token
     * @return {@link Authentication} claim에 담겨있는 정보를 바탕으로 만든 authentication token.
     * @throws JwtException getClaims 호출 시, token이 올바르지 않다면, JwtException을 던짐
     * @throws InvalidTokenException token의 claim에 권한 정보가 존재하지 않을 경우 던짐
     */
    public Authentication getAuthentication(String token) throws JwtException, InvalidTokenException {
        Claims claims = getClaims(token);
        String username = (String) claims.get("username");
        String authoritiesInString = (String) claims.get("authorities");

        if (authoritiesInString == null) {
            throw new InvalidTokenException(TOKEN_NOT_AUTHENTICATED);
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(authoritiesInString.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        UserDetails principal = new User(username, "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 요청으로 전달된 토큰을 추출
     * @param tokenHeader Token 정보가 들어있는 HTTP Header
     * @return Access token 혹은 Refresh token (만약 Authorization header가 없는 요청이거나 올바르지 않은 요청일 경우, <code>null</code>)
     */
    public String retrieveToken(String tokenHeader) {
        if (StringUtils.hasText(tokenHeader) && StringUtils.startsWithIgnoreCase(tokenHeader, BEARER.getType())) {
            String[] headerSplit = tokenHeader.split(" ");
            return headerSplit.length == 2 ? headerSplit[1] : null;
        }

        return null;
    }

    public Claims getClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
