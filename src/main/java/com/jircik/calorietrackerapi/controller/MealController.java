package com.jircik.calorietrackerapi.controller;


import com.jircik.calorietrackerapi.domain.dto.request.AddFoodToMealRequest;
import com.jircik.calorietrackerapi.domain.dto.request.CreateMealRequest;
import com.jircik.calorietrackerapi.domain.dto.response.MealFoodResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealSummaryResponse;
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
    public ResponseEntity<Meal> createMeal(
            @RequestBody CreateMealRequest request) {
        Meal newMeal = mealService.createMeal(request.userId(), request.dateTime());
        return ResponseEntity.status(HttpStatus.CREATED).body(newMeal);
    }

    @PostMapping("/{mealId}/foods")
    public ResponseEntity<MealFoodResponse> addFoodToMeal(
            @PathVariable Long mealId,
            @RequestBody AddFoodToMealRequest request) {
        MealFoodResponse mealFood = mealService.addFoodToMeal(mealId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(mealFood);
    }

    @GetMapping("/{mealId}/summary")
    public ResponseEntity<MealSummaryResponse> getMealSummary(
            @PathVariable Long mealId ){
        MealSummaryResponse summary = mealService.getMealSummary(mealId);

        return ResponseEntity.status(HttpStatus.OK).body(summary);
    }
}
