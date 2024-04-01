package com.givemecon.config;

import com.givemecon.config.auth.util.ClientUrlProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ClientUrlProperties.class)
public class ClientConfig {
}
