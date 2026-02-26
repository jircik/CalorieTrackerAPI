package com.jircik.calorietrackerapi.controller;


import com.jircik.calorietrackerapi.domain.dto.AddFoodToMealRequest;
import com.jircik.calorietrackerapi.domain.dto.CreateMealRequest;
import com.jircik.calorietrackerapi.domain.entity.Meal;
import com.jircik.calorietrackerapi.domain.entity.MealFood;
import com.jircik.calorietrackerapi.service.MealService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meals")
public class MealController {
    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    public ResponseEntity<Meal> createMeal(@RequestBody CreateMealRequest request) {
        Meal newMeal = mealService.createMeal(request.userId(), request.dateTime());
        return ResponseEntity.status(HttpStatus.CREATED).body(newMeal);
    }

    @PostMapping("/{mealId}/foods")
    public ResponseEntity<MealFood> addFoodToMeal(@PathVariable Long mealId, @RequestBody AddFoodToMealRequest request) {
        MealFood mealFood = mealService.addMealFood(mealId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(mealFood);
    }
}
