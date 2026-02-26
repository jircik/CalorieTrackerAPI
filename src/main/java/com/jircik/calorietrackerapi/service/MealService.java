package com.jircik.calorietrackerapi.service;

import com.jircik.calorietrackerapi.domain.dto.request.AddFoodToMealRequest;
import com.jircik.calorietrackerapi.domain.dto.response.DailySummaryResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealFoodResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealSummaryResponse;
import com.jircik.calorietrackerapi.domain.entity.Meal;
import com.jircik.calorietrackerapi.domain.entity.MealFood;
import com.jircik.calorietrackerapi.domain.entity.User;
import com.jircik.calorietrackerapi.repository.MealFoodRepository;
import com.jircik.calorietrackerapi.repository.MealRepository;
import com.jircik.calorietrackerapi.repository.UserRepository;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class MealService {
    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    private final MealFoodRepository mealFoodRepository;

    public MealService(
            MealRepository mealRepository,
            UserRepository userRepository,
            MealFoodRepository mealFoodRepository) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
        this.mealFoodRepository = mealFoodRepository;
    }

    public MealResponse createMeal(Long userId, LocalDateTime date) {
        Meal meal = new Meal();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found!"));
        meal.setUser(user);
        meal.setDatetime(date);

        Meal created = mealRepository.save(meal);

        return new MealResponse(
                created.getId(),
                created.getUser().getId(),
                created.getDatetime(),
                created.getCreatedAt()
        );
    }

    public MealFoodResponse addFoodToMeal(Long mealId, AddFoodToMealRequest request) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("meal not found!"));
        MealFood mealFood = MealFood.builder()
                .meal(meal)
                .foodName(request.foodName())
                .quantity(request.quantity())
                .unit(request.unit())
                .calories(100.0)
                .carbs(20.0)
                .protein(10.0)
                .fat(5.0)
                .build();

        MealFood saved = mealFoodRepository.save(mealFood);

        return new MealFoodResponse(
                saved.getId(),
                saved.getFoodName(),
                saved.getQuantity(),
                saved.getUnit(),
                saved.getCalories(),
                saved.getCarbs(),
                saved.getProtein(),
                saved.getFat()
        );
    }

    public MealSummaryResponse getMealSummary(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("meal not found!"));
        List<MealFood> foods = mealFoodRepository.findByMeal_Id(mealId);

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

        Double totalFoods = (double) foods.size();

        return new MealSummaryResponse(
                mealId,
                totalCalories,
                totalProtein,
                totalCarbs,
                totalFat,
                totalFoods
        );
    }

    public DailySummaryResponse getDailySummary (Long userId, LocalDate date) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found!"));

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<Meal> meals = mealRepository.findByUser_IdAndDatetimeBetween(userId, start, end);

        List<Long> mealIds = meals.stream().map(Meal::getId).toList();

        List<MealFood> foods = mealFoodRepository.findByMeal_IdIn(mealIds);

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
}
