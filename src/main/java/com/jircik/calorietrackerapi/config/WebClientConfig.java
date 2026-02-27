package com.jircik.calorietrackerapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient fatSecretAuthWebClient() {
        return WebClient.builder()
                .baseUrl("https://oauth.fatsecret.com")
                .build();
    }

    @Bean
    public WebClient fatSecretApiWebClient() {
        return WebClient.builder()
                .baseUrl("https://platform.fatsecret.com")
                .build();
    }
}
