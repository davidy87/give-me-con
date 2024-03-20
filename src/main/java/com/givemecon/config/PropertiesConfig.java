package com.givemecon.config;

import com.givemecon.config.auth.util.ClientUrlUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ClientUrlUtils.class)
public class PropertiesConfig {
}
