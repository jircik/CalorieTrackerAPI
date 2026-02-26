package com.jircik.calorietrackerapi.service;

import com.jircik.calorietrackerapi.domain.dto.AddFoodToMealRequest;
import com.jircik.calorietrackerapi.domain.entity.Meal;
import com.jircik.calorietrackerapi.domain.entity.MealFood;
import com.jircik.calorietrackerapi.domain.entity.User;
import com.jircik.calorietrackerapi.repository.MealFoodRepository;
import com.jircik.calorietrackerapi.repository.MealRepository;
import com.jircik.calorietrackerapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MealService {
    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    private final MealFoodRepository mealFoodRepository;

    public MealService(MealRepository mealRepository, UserRepository userRepository, MealFoodRepository mealFoodRepository) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
        this.mealFoodRepository = mealFoodRepository;
    }

    public Meal createMeal(Long userId, LocalDateTime date) {
        Meal meal = new Meal();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("user not found!"));
        meal.setUser(user);
        meal.setDatetime(date);

        return mealRepository.save(meal);
    }

    public MealFood addMealFood(Long mealId, AddFoodToMealRequest request) {
        Meal meal = mealRepository.findById(mealId).orElseThrow(() -> new RuntimeException("meal not found!"));
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

        return mealFoodRepository.save(mealFood);
    }
}
