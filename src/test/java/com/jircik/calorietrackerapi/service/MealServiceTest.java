package com.jircik.calorietrackerapi.service;

import com.jircik.calorietrackerapi.domain.dto.request.AddFoodToMealRequest;
import com.jircik.calorietrackerapi.domain.dto.response.MealFoodResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealSummaryResponse;
import com.jircik.calorietrackerapi.domain.entity.Meal;
import com.jircik.calorietrackerapi.domain.entity.MealFood;
import com.jircik.calorietrackerapi.domain.entity.User;
import com.jircik.calorietrackerapi.domain.fatsecret.NutritionProvider;
import com.jircik.calorietrackerapi.domain.fatsecret.NutritionResult;
import com.jircik.calorietrackerapi.exception.ResourceNotFoundException;
import com.jircik.calorietrackerapi.repository.MealFoodRepository;
import com.jircik.calorietrackerapi.repository.MealRepository;
import com.jircik.calorietrackerapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MealFoodRepository mealFoodRepository;

    @Mock
    private NutritionProvider nutritionProvider;

    @InjectMocks
    private MealService mealService;

    private User testUser;
    private Meal testMeal;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).name("João").email("joao@email.com").build();

        testMeal = new Meal();
        testMeal.setId(10L);
        testMeal.setUser(testUser);
        testMeal.setDatetime(LocalDateTime.of(2026, 3, 10, 12, 0));
        testMeal.setCreatedAt(LocalDateTime.now());
    }

    // createMeal
    @Nested
    @DisplayName("createMeal")
    class CreateMeal {

        @Test
        @DisplayName("deve criar uma refeição com sucesso")
        void shouldCreateMealSuccessfully() {
            LocalDateTime dateTime = LocalDateTime.of(2026, 3, 10, 12, 0);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(mealRepository.save(any(Meal.class))).thenReturn(testMeal);

            MealResponse response = mealService.createMeal(1L, dateTime);

            assertThat(response.id()).isEqualTo(10L);
            assertThat(response.userId()).isEqualTo(1L);
            assertThat(response.dateTime()).isEqualTo(dateTime);
            verify(mealRepository).save(any(Meal.class));
        }

        @Test
        @DisplayName("deve lançar exceção quando usuário não existir")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> mealService.createMeal(99L, LocalDateTime.now()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User not found");
        }
    }

    // addFoodToMeal
    @Nested
    @DisplayName("addFoodToMeal")
    class AddFoodToMeal {

        private final AddFoodToMealRequest request =
                new AddFoodToMealRequest("Arroz", 150.0, "g");

        @Test
        @DisplayName("deve buscar por nome quando alimento não existe no banco")
        void shouldSearchByNameWhenFoodIsNew() {
            NutritionResult nutrition = new NutritionResult(
                    "123", "Arroz", 195.0, 43.0, 4.0, 0.4);

            MealFood savedFood = MealFood.builder()
                    .id(1L).meal(testMeal).foodName("Arroz").fatSecretFoodId("123")
                    .quantity(150.0).unit("g").calories(195.0).carbs(43.0)
                    .protein(4.0).fat(0.4).build();

            when(mealRepository.findById(10L)).thenReturn(Optional.of(testMeal));
            when(mealFoodRepository.findTopByFoodNameIgnoreCase("Arroz"))
                    .thenReturn(Optional.empty());
            when(nutritionProvider.getNutrition("Arroz", 150.0)).thenReturn(nutrition);
            when(mealFoodRepository.save(any(MealFood.class))).thenReturn(savedFood);

            MealFoodResponse response = mealService.addFoodToMeal(10L, request);

            assertThat(response.foodName()).isEqualTo("Arroz");
            assertThat(response.calories()).isEqualTo(195.0);
            verify(nutritionProvider).getNutrition("Arroz", 150.0);
            verify(nutritionProvider, never()).calculateNutritionByFoodId(any(), any());
        }

        @Test
        @DisplayName("deve buscar por foodId quando alimento já existe com fatSecretFoodId")
        void shouldSearchByIdWhenFoodExistsWithFatSecretId() {
            MealFood existing = MealFood.builder()
                    .foodName("Arroz").fatSecretFoodId("123").build();

            NutritionResult nutrition = new NutritionResult(
                    "123", "Arroz", 195.0, 43.0, 4.0, 0.4);

            MealFood savedFood = MealFood.builder()
                    .id(1L).meal(testMeal).foodName("Arroz").fatSecretFoodId("123")
                    .quantity(150.0).unit("g").calories(195.0).carbs(43.0)
                    .protein(4.0).fat(0.4).build();

            when(mealRepository.findById(10L)).thenReturn(Optional.of(testMeal));
            when(mealFoodRepository.findTopByFoodNameIgnoreCase("Arroz"))
                    .thenReturn(Optional.of(existing));
            when(nutritionProvider.calculateNutritionByFoodId("123", 150.0))
                    .thenReturn(nutrition);
            when(mealFoodRepository.save(any(MealFood.class))).thenReturn(savedFood);

            MealFoodResponse response = mealService.addFoodToMeal(10L, request);

            assertThat(response.calories()).isEqualTo(195.0);
            verify(nutritionProvider).calculateNutritionByFoodId("123", 150.0);
            verify(nutritionProvider, never()).getNutrition(any(), any());
        }

        @Test
        @DisplayName("deve buscar por nome quando alimento existe mas sem fatSecretFoodId")
        void shouldSearchByNameWhenExistingHasNoFatSecretId() {
            MealFood existing = MealFood.builder()
                    .foodName("Arroz").fatSecretFoodId(null).build();

            NutritionResult nutrition = new NutritionResult(
                    "123", "Arroz", 195.0, 43.0, 4.0, 0.4);

            MealFood savedFood = MealFood.builder()
                    .id(1L).meal(testMeal).foodName("Arroz").fatSecretFoodId("123")
                    .quantity(150.0).unit("g").calories(195.0).carbs(43.0)
                    .protein(4.0).fat(0.4).build();

            when(mealRepository.findById(10L)).thenReturn(Optional.of(testMeal));
            when(mealFoodRepository.findTopByFoodNameIgnoreCase("Arroz"))
                    .thenReturn(Optional.of(existing));
            when(nutritionProvider.getNutrition("Arroz", 150.0)).thenReturn(nutrition);
            when(mealFoodRepository.save(any(MealFood.class))).thenReturn(savedFood);

            MealFoodResponse response = mealService.addFoodToMeal(10L, request);

            verify(nutritionProvider).getNutrition("Arroz", 150.0);
        }

        @Test
        @DisplayName("deve lançar exceção quando refeição não existir")
        void shouldThrowWhenMealNotFound() {
            when(mealRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> mealService.addFoodToMeal(99L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Meal not found");
        }
    }

    // getMealSummary
    @Nested
    @DisplayName("getMealSummary")
    class GetMealSummary {

        @Test
        @DisplayName("deve calcular soma dos macros corretamente")
        void shouldCalculateMacroTotals() {
            MealFood food1 = MealFood.builder()
                    .calories(200.0).protein(10.0).carbs(30.0).fat(5.0).build();
            MealFood food2 = MealFood.builder()
                    .calories(350.0).protein(25.0).carbs(40.0).fat(12.0).build();

            when(mealRepository.findById(10L)).thenReturn(Optional.of(testMeal));
            when(mealFoodRepository.findByMeal_Id(10L)).thenReturn(List.of(food1, food2));

            MealSummaryResponse response = mealService.getMealSummary(10L);

            assertThat(response.mealId()).isEqualTo(10L);
            assertThat(response.totalCalories()).isEqualTo(550.0);
            assertThat(response.totalProtein()).isEqualTo(35.0);
            assertThat(response.totalCarbs()).isEqualTo(70.0);
            assertThat(response.totalFat()).isEqualTo(17.0);
            assertThat(response.foodCount()).isEqualTo(2.0);
        }

        @Test
        @DisplayName("deve retornar zeros quando não há alimentos")
        void shouldReturnZerosWhenNoFoods() {
            when(mealRepository.findById(10L)).thenReturn(Optional.of(testMeal));
            when(mealFoodRepository.findByMeal_Id(10L)).thenReturn(Collections.emptyList());

            MealSummaryResponse response = mealService.getMealSummary(10L);

            assertThat(response.totalCalories()).isEqualTo(0.0);
            assertThat(response.foodCount()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("deve lançar exceção quando refeição não existir")
        void shouldThrowWhenMealNotFound() {
            when(mealRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> mealService.getMealSummary(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Meal not found!");
        }
    }
}