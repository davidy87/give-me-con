package com.givemecon.jwt;

import com.givemecon.common.auth.jwt.token.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class JwtTokenServiceTest {

    @Autowired
    JwtTokenService jwtTokenService;

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