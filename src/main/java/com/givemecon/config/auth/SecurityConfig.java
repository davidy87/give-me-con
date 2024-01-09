package com.givemecon.config.auth;

import com.givemecon.config.auth.jwt.JwtAuthenticationFilter;
import com.givemecon.config.auth.jwt.JwtExceptionFilter;
import com.givemecon.config.auth.jwt.JwtTokenProvider;
import com.givemecon.domain.member.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET,"/api/categories/**"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/brands/**"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/vouchers/**"),
                                AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/vouchers-for-sale/**"),
                                AntPathRequestMatcher.antMatcher("/api/auth/refresh"),
                                AntPathRequestMatcher.antMatcher("/api/members/admin/**")
                        ).permitAll()
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher("/api/categories/**"),
                                AntPathRequestMatcher.antMatcher("/api/brands/**"),
                                AntPathRequestMatcher.antMatcher("/api/vouchers/**"),
                                AntPathRequestMatcher.antMatcher("/api/vouchers-for-sale/**")
                        ).hasRole(Role.ADMIN.name())
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher("/**")
                        ).authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
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
        config.setAllowedOrigins(List.of("http://localhost:8081"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
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
