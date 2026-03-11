package com.jircik.calorietrackerapi.service;

import com.jircik.calorietrackerapi.domain.dto.request.CreateUserRequest;
import com.jircik.calorietrackerapi.domain.dto.response.DailySummaryResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealsByDateResponse;
import com.jircik.calorietrackerapi.domain.dto.response.UserResponse;
import com.jircik.calorietrackerapi.domain.entity.Meal;
import com.jircik.calorietrackerapi.domain.entity.MealFood;
import com.jircik.calorietrackerapi.domain.entity.User;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MealRepository mealRepository;

    @Mock
    private MealFoodRepository mealFoodRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("João")
                .email("joao@email.com")
                .build();
    }

    // createUser
    @Nested
    @DisplayName("createUser")
    class CreateUser {

        @Test
        @DisplayName("deve criar um usuário e retornar UserResponse")
        void shouldCreateUserSuccessfully() {
            CreateUserRequest request = new CreateUserRequest("João", "joao@email.com");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            UserResponse response = userService.createUser(request);

            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("João");
            assertThat(response.email()).isEqualTo("joao@email.com");
            verify(userRepository).save(any(User.class));
        }
    }

    // getUser
    @Nested
    @DisplayName("getUser")
    class GetUser {

        @Test
        @DisplayName("deve retornar UserResponse quando o usuário existir")
        void shouldReturnUserWhenFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            UserResponse response = userService.getUser(1L);

            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.name()).isEqualTo("João");
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o usuário não existir")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUser(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User not found");
        }
    }

    // getAllUsers
    @Nested
    @DisplayName("getAllUsers")
    class GetAllUsers {

        @Test
        @DisplayName("deve retornar lista vazia quando não há usuários")
        void shouldReturnEmptyList() {
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            List<UserResponse> result = userService.getAllUsers();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("deve retornar múltiplos usuários")
        void shouldReturnMultipleUsers() {
            User user2 = User.builder().id(2L).name("Maria").email("maria@email.com").build();
            when(userRepository.findAll()).thenReturn(List.of(testUser, user2));

            List<UserResponse> result = userService.getAllUsers();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).name()).isEqualTo("João");
            assertThat(result.get(1).name()).isEqualTo("Maria");
        }
    }

    // getDailySummary
    @Nested
    @DisplayName("getDailySummary")
    class GetDailySummary {

        private final LocalDate date = LocalDate.of(2026, 3, 10);

        @Test
        @DisplayName("deve calcular totais corretamente")
        void shouldCalculateTotalsCorrectly() {
            Meal meal1 = new Meal();
            meal1.setId(10L);
            Meal meal2 = new Meal();
            meal2.setId(20L);

            MealFood food1 = MealFood.builder()
                    .calories(200.0).protein(10.0).carbs(30.0).fat(5.0).build();
            MealFood food2 = MealFood.builder()
                    .calories(350.0).protein(25.0).carbs(40.0).fat(12.0).build();
            MealFood food3 = MealFood.builder()
                    .calories(150.0).protein(8.0).carbs(20.0).fat(3.0).build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(mealRepository.findByUser_IdAndDatetimeBetweenOrderByDatetimeAsc(
                    eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(meal1, meal2));
            when(mealFoodRepository.findByMeal_IdInOrderByCreatedAtAsc(List.of(10L, 20L)))
                    .thenReturn(List.of(food1, food2, food3));

            DailySummaryResponse response = userService.getDailySummary(1L, date);

            assertThat(response.userId()).isEqualTo(1L);
            assertThat(response.date()).isEqualTo(date);
            assertThat(response.totalCalories()).isEqualTo(700.0);
            assertThat(response.totalProtein()).isEqualTo(43.0);
            assertThat(response.totalCarbs()).isEqualTo(90.0);
            assertThat(response.totalFat()).isEqualTo(20.0);
            assertThat(response.mealCount()).isEqualTo(2L);
            assertThat(response.foodCount()).isEqualTo(3L);
        }

        @Test
        @DisplayName("deve lançar exceção quando o usuário não existir")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getDailySummary(99L, date))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("deve retornar zeros quando não há refeições no dia")
        void shouldReturnZerosWhenNoMeals() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(mealRepository.findByUser_IdAndDatetimeBetweenOrderByDatetimeAsc(
                    eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());
            when(mealFoodRepository.findByMeal_IdInOrderByCreatedAtAsc(Collections.emptyList()))
                    .thenReturn(Collections.emptyList());

            DailySummaryResponse response = userService.getDailySummary(1L, date);

            assertThat(response.totalCalories()).isEqualTo(0.0);
            assertThat(response.mealCount()).isEqualTo(0L);
            assertThat(response.foodCount()).isEqualTo(0L);
        }
    }

    // getMealsByDate
    @Nested
    @DisplayName("getMealsByDate")
    class GetMealsByDate {

        private final LocalDate date = LocalDate.of(2026, 3, 10);

        @Test
        @DisplayName("deve agrupar alimentos por refeição corretamente")
        void shouldGroupFoodsByMeal() {
            Meal meal = new Meal();
            meal.setId(10L);
            meal.setDatetime(date.atTime(12, 0));

            MealFood food1 = MealFood.builder()
                    .id(1L).meal(meal).foodName("Arroz").quantity(150.0).unit("g")
                    .calories(200.0).carbs(44.0).protein(4.0).fat(0.5).build();
            MealFood food2 = MealFood.builder()
                    .id(2L).meal(meal).foodName("Feijão").quantity(100.0).unit("g")
                    .calories(120.0).carbs(21.0).protein(8.0).fat(0.5).build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(mealFoodRepository.findByMeal_User_IdAndMeal_DatetimeBetween(
                    eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(food1, food2));

            MealsByDateResponse response = userService.getMealsByDate(1L, date);

            assertThat(response.userId()).isEqualTo(1L);
            assertThat(response.date()).isEqualTo(date);
            assertThat(response.meals()).hasSize(1);
            assertThat(response.meals().getFirst().foods()).hasSize(2);
        }

        @Test
        @DisplayName("deve lançar exceção quando o usuário não existir")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getMealsByDate(99L, date))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não há refeições")
        void shouldReturnEmptyMealsWhenNone() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(mealFoodRepository.findByMeal_User_IdAndMeal_DatetimeBetween(
                    eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            MealsByDateResponse response = userService.getMealsByDate(1L, date);

            assertThat(response.meals()).isEmpty();
        }
    }
}