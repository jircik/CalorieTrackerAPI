package com.jircik.calorietrackerapi.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jircik.calorietrackerapi.integration.dto.FoodDetailsResponse;
import com.jircik.calorietrackerapi.integration.dto.FoodSearchResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class FatSecretCacheConfig {

    @Bean
    public Cache<String, FoodDetailsResponse> foodDetailsCache() {

        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofHours(6))
                .build();
    }

    @Bean
    public Cache<String, FoodSearchResponse.Food> foodSearchCache() {

        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofHours(6))
                .build();
    }
}