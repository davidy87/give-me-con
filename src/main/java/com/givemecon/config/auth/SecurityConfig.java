package com.givemecon.config.auth;

import com.givemecon.domain.member.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@EnableWebSecurity
//@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/css/**"),
                                new AntPathRequestMatcher("/images/**"),
                                new AntPathRequestMatcher("/js/**"),
                                new AntPathRequestMatcher("/h2-console/**"),
                                new AntPathRequestMatcher("/profile"),
                                new AntPathRequestMatcher("/api/categories/**", "GET"),
                                new AntPathRequestMatcher("/api/brands/**", "GET"),
                                new AntPathRequestMatcher("/api/vouchers/**", "GET")
                        ).permitAll()
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/members/**"),
                                new AntPathRequestMatcher("/api/vouchers/**")
                        ).hasAnyRole(Role.ADMIN.name(), Role.USER.name())
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/**")
                        ).hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .oauth2Login(login -> login
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}
