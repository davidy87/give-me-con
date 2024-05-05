package com.givemecon.config.auth.jwt.token;

import com.givemecon.config.auth.dto.TokenInfo;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.givemecon.config.enums.GrantType.*;
import static com.givemecon.config.enums.TokenDuration.ACCESS_TOKEN_DURATION;
import static com.givemecon.config.enums.TokenDuration.REFRESH_TOKEN_DURATION;
import static com.givemecon.domain.member.MemberDto.*;
import static com.givemecon.util.error.ErrorCode.*;

@Slf4j
@Component
public class JwtTokenService {

    private static final String CLAIM_NAME_USERNAME = "username";

    private static final String CLAIM_NAME_AUTHORITIES = "authorities";

    private static final String TOKEN_HEADER_DELIMITER = " ";

    private final SecretKey secretKey;

    private final RefreshTokenRepository refreshTokenRepository;


    public JwtTokenService(@Value("${jwt.secret}") String secretKey,
                           RefreshTokenRepository refreshTokenRepository) {

        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * 로그인 성공 시, 응답으로 전달하는 토큰 정보
     * @param tokenRequest 토큰을 요청하는 사용자의 정보가 담긴 DTO
     * @return {@link TokenInfo} (Grant type, access token, refresh token이 담겨있는 DTO)
     */
    public TokenInfo getTokenInfo(TokenRequest tokenRequest) {
        String accessToken = generateAccessToken(tokenRequest);
        String refreshToken = generateRefreshToken();

        refreshTokenRepository.findByMemberId(String.valueOf(tokenRequest.getMemberId()))
                .ifPresentOrElse(
                        entity -> {
                            entity.updateRefreshToken(refreshToken);
                            refreshTokenRepository.save(entity);
                        },
                        () -> refreshTokenRepository.save(
                                new RefreshToken(String.valueOf(tokenRequest.getMemberId()), refreshToken)
                        ));

        return TokenInfo.builder()
                .grantType(BEARER.getType())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(tokenRequest.getUsername())
                .authority(tokenRequest.getAuthority())
                .build();
    }

    private String generateAccessToken(TokenRequest tokenRequest) {
        String authoritiesInString = Stream.of(new SimpleGrantedAuthority(tokenRequest.getRole()))
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long currentTime = System.currentTimeMillis();

        return Jwts.builder()
                .claim(CLAIM_NAME_USERNAME, tokenRequest.getUsername())
                .claim(CLAIM_NAME_AUTHORITIES, authoritiesInString)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime + ACCESS_TOKEN_DURATION.toMillis()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken() {
        long currentTime = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(UUID.randomUUID().toString())
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime + REFRESH_TOKEN_DURATION.toMillis()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 요청으로 전달된 토큰을 추출
     * @param tokenHeader Token 정보가 들어있는 HTTP Header
     * @return Access token 혹은 Refresh token (만약 올바르지 않은 형식의 Authentication(혹은 Refresh-Token) header일 경우,
     *         <code>null</code>)
     */
    public String retrieveToken(String tokenHeader) {
        String[] headerSplit = StringUtils.split(tokenHeader, TOKEN_HEADER_DELIMITER);

        if (headerSplit == null) {
            return null;
        }

        boolean isHeaderValid = headerSplit[0].equals(BEARER.getType())
                        && StringUtils.hasText(headerSplit[1])
                        && !StringUtils.containsWhitespace(headerSplit[1]);

        return isHeaderValid ? headerSplit[1] : null;
    }

    /**
     * @param token 사용자의 token
     * @return {@link Authentication} claim에 담겨있는 정보를 바탕으로 만든 authentication token.
     * @throws JwtException getClaims 호출 시, token이 올바르지 않다면, JwtException을 던짐
     * @throws InvalidTokenException token의 claim에 권한 정보가 존재하지 않을 경우 던짐
     */
    public Authentication getAuthentication(String token) throws JwtException, InvalidTokenException {
        Claims claims = getClaims(token);
        String username = (String) claims.get(CLAIM_NAME_USERNAME);
        String authoritiesInString = (String) claims.get(CLAIM_NAME_AUTHORITIES);

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

    public Claims getClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
