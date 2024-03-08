package com.givemecon.config.auth;

import com.givemecon.config.auth.jwt.JwtAuthenticationFilter;
import com.givemecon.config.auth.jwt.JwtExceptionFilter;
import com.givemecon.config.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.givemecon.config.auth.enums.ApiPathPattern.*;
import static com.givemecon.config.auth.enums.ClientUrl.BASE_URL;
import static com.givemecon.domain.member.Role.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.*;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.*;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    private final AuthSuccessHandler authSuccessHandler;

    private final AuthFailureHandler authFailureHandler;

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsFilter()))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                antMatcher(ADMIN_LOGIN_API.pattern()),
                                antMatcher(GET, CATEGORY_API.pattern()),
                                antMatcher(GET, BRAND_API.pattern()),
                                antMatcher(GET, VOUCHER_API.pattern()),
                                antMatcher(GET, VOUCHER_FOR_SALE_API.pattern())
                        ).permitAll()
                        .requestMatchers(
                                antMatcher(MEMBER_API.pattern()),
                                antMatcher(CATEGORY_API.pattern()),
                                antMatcher(BRAND_API.pattern()),
                                antMatcher(VOUCHER_API.pattern())
                        ).hasRole(ADMIN.name())
                        .requestMatchers(
                                antMatcher(AUTH_API.pattern()),
                                antMatcher(VOUCHER_FOR_SALE_API.pattern()),
                                antMatcher(LIKED_VOUCHER_API.pattern()),
                                antMatcher(PURCHASED_VOUCHER_API.pattern())
                        ).hasAnyRole(ADMIN.name(), USER.name())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(STATELESS)
                )
                .oauth2Login(login -> login
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(authSuccessHandler)
                        .failureHandler(authFailureHandler)
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        OAuth2LoginAuthenticationFilter.class
                )
                .addFilterBefore(
                        new JwtExceptionFilter(),
                        JwtAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of(BASE_URL.getUrl()));
        config.setAllowedMethods(List.of(GET.name(), POST.name(), PUT.name(), DELETE.name()));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
