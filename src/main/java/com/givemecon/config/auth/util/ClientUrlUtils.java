package com.givemecon.config.auth.util;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Setter
@ConfigurationProperties(prefix = "client")
@Component
public class ClientUrlUtils {

    private String baseUrl;

    private Map<String, String> paths;

    public String getLoginUrl() {
        return baseUrl + paths.get("login");
    }
}
