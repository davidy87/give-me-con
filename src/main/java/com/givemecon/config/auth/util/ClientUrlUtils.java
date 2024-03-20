package com.givemecon.config.auth.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@RequiredArgsConstructor
@ConfigurationProperties(prefix = "client")
public class ClientUrlUtils {

    private static final String LOGIN_PATH_NAME = "login";

    private final String baseUrl;

    private final Map<String, String> paths;

    public String getLoginUrl() {
        return baseUrl + paths.get(LOGIN_PATH_NAME);
    }
}
