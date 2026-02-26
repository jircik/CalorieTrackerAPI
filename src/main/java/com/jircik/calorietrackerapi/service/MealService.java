package com.jircik.calorietrackerapi.service;

import com.jircik.calorietrackerapi.domain.dto.request.AddFoodToMealRequest;
import com.jircik.calorietrackerapi.domain.dto.response.MealFoodResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealSummaryResponse;
import com.jircik.calorietrackerapi.domain.entity.Meal;
import com.jircik.calorietrackerapi.domain.entity.MealFood;
import com.jircik.calorietrackerapi.domain.entity.User;
import com.jircik.calorietrackerapi.repository.MealFoodRepository;
import com.jircik.calorietrackerapi.repository.MealRepository;
import com.jircik.calorietrackerapi.repository.UserRepository;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
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

    public Meal createMeal(Long userId, LocalDateTime date) {
        Meal meal = new Meal();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found!"));
        meal.setUser(user);
        meal.setDatetime(date);

        return mealRepository.save(meal);
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

        return new MealSummaryResponse(
                meal.getId(),
                foods.stream().mapToDouble(MealFood::getCalories).sum(),
                foods.stream().mapToDouble(MealFood::getProtein).sum(),
                foods.stream().mapToDouble(MealFood::getCarbs).sum(),
                foods.stream().mapToDouble(MealFood::getFat).sum(),
                (double) foods.size()
        );
    }
}
