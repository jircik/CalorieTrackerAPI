package com.jircik.calorietrackerapi.integration.fatsecret;

import com.jircik.calorietrackerapi.exception.IntegrationException;
import com.jircik.calorietrackerapi.integration.dto.TokenResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class FatSecretAuthClient {

    private final WebClient webClient;
    private final FatSecretProperties properties;

    private String accessToken;
    private Instant expiresAt;

    public FatSecretAuthClient(WebClient fatSecretAuthWebClient,
                               FatSecretProperties properties) {
        this.webClient = fatSecretAuthWebClient;
        this.properties = properties;
    }

    public String getValidToken() {
        if (accessToken == null || Instant.now().isAfter(expiresAt)) {
            requestNewToken();
        }
        return accessToken;
    }

    private void requestNewToken() {

        String credentials = properties.getClientId() + ":" + properties.getClientSecret();

        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        TokenResponse response = webClient.post()
                .uri("/connect/token")
                .header("Authorization", "Basic " + encodedCredentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=client_credentials&scope=basic")
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .map(body -> new IntegrationException("FatSecret auth failed: " + body))
                )
                .bodyToMono(TokenResponse.class)
                .block();

        assert response != null;
        this.accessToken = response.access_token();
        this.expiresAt = Instant.now().plusSeconds(response.expires_in() - 60);
    }
}