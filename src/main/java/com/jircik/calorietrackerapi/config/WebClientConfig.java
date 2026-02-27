package com.jircik.calorietrackerapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

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

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(5));

        return WebClient.builder()
                .baseUrl("https://platform.fatsecret.com")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
