package com.jircik.calorietrackerapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CalorieTracker API")
                        .version("2.0")
                        .description("A REST API for tracking daily meals and nutritional macros. " +
                                "Integrates with the FatSecret API to search and retrieve nutritional data, " +
                                "calculating calories, protein, carbohydrates, and fat based on food quantities.")
                        .contact(new Contact()
                                .name("Arthur Jircik Cronemberger")
                                .url("https://github.com/jircik")));
    }
}
