package com.jircik.calorietrackerapi.service;

import com.jircik.calorietrackerapi.domain.dto.request.CreateUserRequest;
import com.jircik.calorietrackerapi.domain.dto.response.*;
import com.jircik.calorietrackerapi.domain.entity.Meal;
import com.jircik.calorietrackerapi.domain.entity.MealFood;
import com.jircik.calorietrackerapi.domain.entity.User;
import com.jircik.calorietrackerapi.exception.ResourceNotFoundException;
import com.jircik.calorietrackerapi.repository.MealFoodRepository;
import com.jircik.calorietrackerapi.repository.MealRepository;
import com.jircik.calorietrackerapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final MealFoodRepository mealFoodRepository;

    public UserResponse createUser(CreateUserRequest request) {
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .build();

        User saved =  userRepository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail()
        );
    }

    public UserResponse getUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    //metodo somente para teste, pois user não pode ver outros users
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                ))
                .toList();
    }

    public DailySummaryResponse getDailySummary (Long userId, LocalDate date) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found!"));

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<Meal> meals =
                mealRepository.findByUser_IdAndDatetimeBetweenOrderByDatetimeAsc(userId, start, end);

        List<Long> mealIds = meals.stream().map(Meal::getId).toList();

        List<MealFood> foods = mealFoodRepository.findByMeal_IdInOrderByCreatedAtAsc(mealIds);

        Double totalCalories = foods.stream()
                .mapToDouble(MealFood::getCalories)
                .sum();

        Double totalProtein = foods.stream()
                .mapToDouble(MealFood::getProtein)
                .sum();

        Double totalCarbs = foods.stream()
                .mapToDouble(MealFood::getCarbs)
                .sum();

        Double totalFat = foods.stream()
                .mapToDouble(MealFood::getFat)
                .sum();

        long mealCount = meals.size();
        long foodCount = foods.size();


        return new DailySummaryResponse(
                userId,
                date,
                totalCalories,
                totalProtein,
                totalCarbs,
                totalFat,
                mealCount,
                foodCount
        );
    }

    public MealsByDateResponse getMealsByDate(Long userId, LocalDate date) {

        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<MealFood> foods = mealFoodRepository
                .findByMeal_User_IdAndMeal_DatetimeBetween(userId, start, end);

        Map<Long, List<MealFood>> foodsByMeal =
                foods.stream().collect(Collectors.groupingBy(food -> food.getMeal().getId()));

        List<MealWithFoodsResponse> meals = foodsByMeal.entrySet()
                .stream()
                .map(entry -> {

                    Meal meal = entry.getValue().getFirst().getMeal();

                    List<MealFoodResponse> foodResponses = entry.getValue()
                            .stream()
                            .map(food -> new MealFoodResponse(
                                    food.getId(),
                                    food.getFoodName(),
                                    food.getQuantity(),
                                    food.getUnit(),
                                    food.getCalories(),
                                    food.getCarbs(),
                                    food.getProtein(),
                                    food.getFat()
                            ))
                            .toList();

                    return new MealWithFoodsResponse(
                            meal.getId(),
                            meal.getDatetime(),
                            foodResponses
                    );
                })
                .toList();

        return new MealsByDateResponse(
                userId,
                date,
                meals
        );
    }

}
