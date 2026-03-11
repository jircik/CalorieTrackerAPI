package com.jircik.calorietrackerapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jircik.calorietrackerapi.domain.dto.request.AddFoodToMealRequest;
import com.jircik.calorietrackerapi.domain.dto.request.CreateMealRequest;
import com.jircik.calorietrackerapi.domain.dto.response.MealFoodResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealSummaryResponse;
import com.jircik.calorietrackerapi.exception.ResourceNotFoundException;
import com.jircik.calorietrackerapi.service.MealService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MealController.class)
public class MealControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private MealService mealService;

    @Test
    @DisplayName("POST /meals — deve criar refeição e retornar 201")
    void createMeal_shouldReturn201() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(2026, 3, 10, 12, 0);
        CreateMealRequest request = new CreateMealRequest(1L, dateTime);
        MealResponse response = new MealResponse(10L, 1L, dateTime, LocalDateTime.now());

        when(mealService.createMeal(eq(1L), eq(dateTime))).thenReturn(response);

        mockMvc.perform(post("/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @DisplayName("POST /meals — deve retornar 400 quando userId é nulo")
    void createMeal_shouldReturn400WhenUserIdNull() throws Exception {
        CreateMealRequest request = new CreateMealRequest(null, LocalDateTime.now());

        mockMvc.perform(post("/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /meals — deve retornar 400 quando dateTime é nulo")
    void createMeal_shouldReturn400WhenDateTimeNull() throws Exception {
        CreateMealRequest request = new CreateMealRequest(1L, null);

        mockMvc.perform(post("/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /meals/{mealId}/foods — deve adicionar alimento e retornar 201")
    void addFoodToMeal_shouldReturn201() throws Exception {
        AddFoodToMealRequest request = new AddFoodToMealRequest("Arroz", 150.0, "g");
        MealFoodResponse response = new MealFoodResponse(
                1L, "Arroz", 150.0, "g", 195.0, 43.0, 4.0, 0.4);

        when(mealService.addFoodToMeal(eq(10L), any(AddFoodToMealRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/meals/10/foods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.foodName").value("Arroz"))
                .andExpect(jsonPath("$.calories").value(195.0));
    }

    @Test
    @DisplayName("POST /meals/{mealId}/foods — deve retornar 400 quando foodName é vazio")
    void addFoodToMeal_shouldReturn400WhenFoodNameBlank() throws Exception {
        AddFoodToMealRequest request = new AddFoodToMealRequest("", 150.0, "g");

        mockMvc.perform(post("/meals/10/foods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /meals/{mealId}/foods — deve retornar 400 quando quantity é negativa")
    void addFoodToMeal_shouldReturn400WhenQuantityNegative() throws Exception {
        AddFoodToMealRequest request = new AddFoodToMealRequest("Arroz", -1.0, "g");

        mockMvc.perform(post("/meals/10/foods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /meals/{mealId}/summary — deve retornar resumo da refeição")
    void getMealSummary_shouldReturn200() throws Exception {
        MealSummaryResponse response = new MealSummaryResponse(
                10L, 550.0, 35.0, 70.0, 17.0, 2.0);

        when(mealService.getMealSummary(10L)).thenReturn(response);

        mockMvc.perform(get("/meals/10/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mealId").value(10))
                .andExpect(jsonPath("$.totalCalories").value(550.0))
                .andExpect(jsonPath("$.foodCount").value(2.0));
    }

    @Test
    @DisplayName("GET /meals/{mealId}/summary — deve retornar 404 quando refeição não existe")
    void getMealSummary_shouldReturn404WhenNotFound() throws Exception {
        when(mealService.getMealSummary(99L))
                .thenThrow(new ResourceNotFoundException("Meal not found!"));

        mockMvc.perform(get("/meals/99/summary"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Meal not found!"));
    }
}
