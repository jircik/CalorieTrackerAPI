package com.jircik.calorietrackerapi.controller;


import com.jircik.calorietrackerapi.domain.dto.request.AddFoodToMealRequest;
import com.jircik.calorietrackerapi.domain.dto.request.CreateMealRequest;
import com.jircik.calorietrackerapi.domain.dto.response.MealFoodResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealSummaryResponse;
import com.jircik.calorietrackerapi.service.MealService;
import jakarta.validation.Valid;
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
    public ResponseEntity<MealResponse> createMeal(
            @Valid @RequestBody CreateMealRequest request) {

        MealResponse newMeal = mealService.createMeal(request.userId(), request.dateTime());

        return ResponseEntity.status(HttpStatus.CREATED).body(newMeal);

    }

    @PostMapping("/{mealId}/foods")
    public ResponseEntity<MealFoodResponse> addFoodToMeal(
            @PathVariable Long mealId,
            @Valid @RequestBody AddFoodToMealRequest request) {

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
