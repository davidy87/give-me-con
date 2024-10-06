package com.givemecon.common.jwt;

import com.givemecon.common.auth.jwt.token.JwtTokenService;
import com.givemecon.common.auth.jwt.token.RefreshTokenRepository;
import com.givemecon.domain.repository.MemberRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenService(generateRandomSecretKey(), refreshTokenRepository, memberRepository);
    }

    private String generateRandomSecretKey() {
        return RandomStringUtils.random(50, true, true);
    }

    @Test
    void retrieveTokenFromCorrectHeader() {
        // given
        String header = "Bearer foobar";

        // when
        String token = jwtTokenService.retrieveToken(header);

        // then
        assertThat(token).isEqualTo("foobar");
    }

    @Test
    void retrieveTokenFromIncorrectHeader() {
        // given
        String[] incorrectHeaders = {
                "Bearerfoobar",
                "bearer foobar",
                "Bearerf oobar",
                "Bearer foobar  ",
                " Bearer foobar",
                " Bearer foobar ",
                "  Bearer foobar  ",
                "\n Bearer foobar  ",
                "Bearer ",
                "Bearer \nfoobar",
                null
        };

        // when & then
        Arrays.stream(incorrectHeaders)
                .map(header -> jwtTokenService.retrieveToken(header))
                .forEach(token -> assertThat(token).isEmpty());
    }
}