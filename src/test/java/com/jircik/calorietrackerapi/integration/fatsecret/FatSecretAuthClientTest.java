package com.jircik.calorietrackerapi.integration.fatsecret;

import com.jircik.calorietrackerapi.exception.IntegrationException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

@DisplayName("FatSecretAuthClient")
class FatSecretAuthClientTest {

    private MockWebServer mockWebServer;
    private FatSecretAuthClient authClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        FatSecretProperties properties = new FatSecretProperties();
        properties.setClientId("testClientId");
        properties.setClientSecret("testClientSecret");

        authClient = new FatSecretAuthClient(webClient, properties);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private String validTokenJson(int expiresIn) {
        return """
                {
                    "access_token": "test-token-abc123",
                    "token_type": "Bearer",
                    "expires_in": %d,
                    "scope": "basic"
                }
                """.formatted(expiresIn);
    }

    @Test
    @DisplayName("deve solicitar e retornar um novo token com credenciais corretas")
    void shouldRequestAndReturnNewToken() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody(validTokenJson(3600))
                .addHeader("Content-Type", "application/json"));

        String token = authClient.getValidToken();

        assertThat(token).isEqualTo("test-token-abc123");

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).isEqualTo("/connect/token");
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getHeader("Authorization")).startsWith("Basic ");
        assertThat(request.getBody().readUtf8()).isEqualTo("grant_type=client_credentials&scope=basic");
    }

    @Test
    @DisplayName("deve retornar token em cache quando ainda não expirou")
    void shouldReturnCachedTokenWhenNotExpired() {
        mockWebServer.enqueue(new MockResponse()
                .setBody(validTokenJson(3600))
                .addHeader("Content-Type", "application/json"));

        String firstCall = authClient.getValidToken();
        String secondCall = authClient.getValidToken();

        assertThat(firstCall).isEqualTo("test-token-abc123");
        assertThat(secondCall).isEqualTo("test-token-abc123");
        assertThat(mockWebServer.getRequestCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("deve solicitar novo token quando o anterior expirou")
    void shouldRefreshTokenWhenExpired() throws Exception {
        // First token with very short expiry
        mockWebServer.enqueue(new MockResponse()
                .setBody(validTokenJson(3600))
                .addHeader("Content-Type", "application/json"));

        String firstToken = authClient.getValidToken();
        assertThat(firstToken).isEqualTo("test-token-abc123");

        // Force expiration via reflection
        Field expiresAtField = FatSecretAuthClient.class.getDeclaredField("expiresAt");
        expiresAtField.setAccessible(true);
        expiresAtField.set(authClient, Instant.now().minusSeconds(10));

        // Enqueue a second token response
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        {
                            "access_token": "refreshed-token-xyz",
                            "token_type": "Bearer",
                            "expires_in": 3600,
                            "scope": "basic"
                        }
                        """)
                .addHeader("Content-Type", "application/json"));

        String refreshedToken = authClient.getValidToken();

        assertThat(refreshedToken).isEqualTo("refreshed-token-xyz");
        assertThat(mockWebServer.getRequestCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("deve lançar IntegrationException quando o servidor de auth retorna erro")
    void shouldThrowIntegrationExceptionOnAuthError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("Unauthorized: invalid credentials")
                .addHeader("Content-Type", "text/plain"));

        assertThatThrownBy(() -> authClient.getValidToken())
                .isInstanceOf(IntegrationException.class)
                .hasMessageContaining("FatSecret auth failed");
    }

    @Test
    @DisplayName("deve lançar IntegrationException quando o servidor retorna erro 500")
    void shouldThrowIntegrationExceptionOnServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
                .addHeader("Content-Type", "text/plain"));

        assertThatThrownBy(() -> authClient.getValidToken())
                .isInstanceOf(IntegrationException.class)
                .hasMessageContaining("FatSecret auth failed");
    }
}