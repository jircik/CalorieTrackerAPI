package com.jircik.calorietrackerapi.controller;


import com.jircik.calorietrackerapi.domain.dto.request.AddFoodToMealRequest;
import com.jircik.calorietrackerapi.domain.dto.request.CreateMealRequest;
import com.jircik.calorietrackerapi.domain.dto.request.UpdateMealFoodQuantityRequest;
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

        MealResponse newMeal = mealService.createMeal(
                request.userId(),
                request.dateTime(),
                request.mealType()
        );

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

        return ResponseEntity.ok(summary);
    }

    @DeleteMapping("/{mealId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMeal(@PathVariable Long mealId){
        mealService.DeleteMeal(mealId);
    }

    @DeleteMapping("/{mealId}/foods/{mealFoodId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMealFood(
            @PathVariable Long mealId,
            @PathVariable Long mealFoodId
            ){
        mealService.DeleteMealFood(mealId, mealFoodId);
    }

    @PatchMapping("/{mealId}/foods/{mealFoodId}")
    public ResponseEntity<MealFoodResponse> updateMealFoodQuantity(
            @PathVariable Long mealId,
            @PathVariable Long mealFoodId,
            @Valid @RequestBody UpdateMealFoodQuantityRequest request
            ){
        MealFoodResponse updated = mealService
                .updateMealFoodQuantity(mealId, mealFoodId, request.quantity());

        return ResponseEntity.ok(updated);
    }
}
