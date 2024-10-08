package com.givemecon.common.auth;

import com.givemecon.common.auth.jwt.filter.JwtAuthenticationFilter;
import com.givemecon.common.auth.jwt.filter.JwtExceptionFilter;
import com.givemecon.common.auth.jwt.token.JwtTokenService;
import com.givemecon.common.auth.util.ClientUrlProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.givemecon.common.auth.util.RequestMatcherList.*;
import static com.givemecon.domain.entity.member.Role.ADMIN;
import static com.givemecon.domain.entity.member.Role.USER;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService;

    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    private final AuthenticationFailureHandler authenticationFailureHandler;

    private final JwtTokenService jwtTokenService;

    private final ClientUrlProperties clientUrlProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsFilter()))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(ofPermitAll()).permitAll()
                        .requestMatchers(ofAuthenticated()).authenticated()
                        .requestMatchers(ofRoleAdmin()).hasRole(ADMIN.name())
                        .requestMatchers(ofAnyRole()).hasAnyRole(ADMIN.name(), USER.name())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(STATELESS)
                )
                .oauth2Login(login -> login
                        .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService))
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenService),
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
        config.setAllowedOrigins(List.of(clientUrlProperties.getBaseUrl()));
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
