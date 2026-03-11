package com.jircik.calorietrackerapi.exception;

import com.jircik.calorietrackerapi.domain.dto.exception.ErrorResponse;
import com.jircik.calorietrackerapi.domain.dto.exception.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/test-path");
    }

    @Test
    @DisplayName("handleNotFound — deve retornar 404 com mensagem correta")
    void handleNotFound_shouldReturn404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User not found");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).isEqualTo("User not found");
        assertThat(response.getBody().path()).isEqualTo("/test-path");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    @DisplayName("handleIntegrationException — deve retornar 503 com mensagem correta")
    void handleIntegrationException_shouldReturn503() {
        IntegrationException ex = new IntegrationException("FatSecret unavailable");

        ResponseEntity<ErrorResponse> response = handler.handleIntegrationException(ex, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(503);
        assertThat(response.getBody().message()).isEqualTo("FatSecret unavailable");
        assertThat(response.getBody().path()).isEqualTo("/test-path");
    }

    @Test
    @DisplayName("handleValidation — deve retornar 400 com lista de erros")
    void handleValidation_shouldReturn400WithErrors() {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "name", "Name is required"));
        bindingResult.addError(new FieldError("request", "email", "Email is required"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ValidationErrorResponse> response =
                handler.handleValidation(ex, mockRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().errors()).hasSize(2);
        assertThat(response.getBody().errors()).containsExactlyInAnyOrder(
                "Name is required", "Email is required");
        assertThat(response.getBody().path()).isEqualTo("/test-path");
    }
}