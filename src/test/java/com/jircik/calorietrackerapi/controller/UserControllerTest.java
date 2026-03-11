package com.jircik.calorietrackerapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jircik.calorietrackerapi.domain.dto.request.CreateUserRequest;
import com.jircik.calorietrackerapi.domain.dto.response.DailySummaryResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealsByDateResponse;
import com.jircik.calorietrackerapi.domain.dto.response.UserResponse;
import com.jircik.calorietrackerapi.exception.ResourceNotFoundException;
import com.jircik.calorietrackerapi.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("POST /users — deve criar usuário e retornar 200")
    void createUser_shouldReturn200() throws Exception {
        CreateUserRequest request = new CreateUserRequest("João", "joao@email.com");
        UserResponse response = new UserResponse(1L, "João", "joao@email.com");

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("João"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    @DisplayName("GET /users/{id} — deve retornar usuário quando encontrado")
    void getUser_shouldReturn200WhenFound() throws Exception {
        UserResponse response = new UserResponse(1L, "João", "joao@email.com");
        when(userService.getUser(1L)).thenReturn(response);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("João"));
    }

    @Test
    @DisplayName("GET /users/{id} — deve retornar 404 quando não encontrado")
    void getUser_shouldReturn404WhenNotFound() throws Exception {
        when(userService.getUser(99L))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @DisplayName("GET /users — deve retornar lista de usuários")
    void getAllUsers_shouldReturnList() throws Exception {
        List<UserResponse> responses = List.of(
                new UserResponse(1L, "João", "joao@email.com"),
                new UserResponse(2L, "Maria", "maria@email.com")
        );
        when(userService.getAllUsers()).thenReturn(responses);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("João"))
                .andExpect(jsonPath("$[1].name").value("Maria"));
    }

    @Test
    @DisplayName("GET /users — deve retornar lista vazia")
    void getAllUsers_shouldReturnEmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /users/{userId}/daily-summary — deve retornar resumo diário")
    void getDailySummary_shouldReturn200() throws Exception {
        LocalDate date = LocalDate.of(2026, 3, 10);
        DailySummaryResponse response = new DailySummaryResponse(
                1L, date, 700.0, 43.0, 90.0, 20.0, 2L, 3L);

        when(userService.getDailySummary(eq(1L), eq(date))).thenReturn(response);

        mockMvc.perform(get("/users/1/daily-summary")
                        .param("date", "2026-03-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.totalCalories").value(700.0))
                .andExpect(jsonPath("$.mealCount").value(2));
    }

    @Test
    @DisplayName("GET /users/{userId}/meals — deve retornar refeições por data")
    void getMealsByDate_shouldReturn200() throws Exception {
        LocalDate date = LocalDate.of(2026, 3, 10);
        MealsByDateResponse response = new MealsByDateResponse(
                1L, date, Collections.emptyList());

        when(userService.getMealsByDate(eq(1L), eq(date))).thenReturn(response);

        mockMvc.perform(get("/users/1/meals")
                        .param("date", "2026-03-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.date").value("2026-03-10"));
    }
}
