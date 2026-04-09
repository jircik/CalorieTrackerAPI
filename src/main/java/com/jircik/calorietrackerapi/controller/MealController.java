package com.jircik.calorietrackerapi.controller;


import com.jircik.calorietrackerapi.domain.dto.request.AddFoodToMealRequest;
import com.jircik.calorietrackerapi.domain.dto.request.CreateMealRequest;
import com.jircik.calorietrackerapi.domain.dto.request.UpdateMealFoodQuantityRequest;
import com.jircik.calorietrackerapi.domain.dto.response.MealFoodResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealSummaryResponse;
import com.jircik.calorietrackerapi.service.MealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Meals", description = "Meal and food tracking")
@RestController
@RequestMapping("/meals")
public class MealController {
    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @Operation(summary = "Create a meal", description = "Creates a new meal for a user with a specified date/time and meal type")
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

    @Operation(summary = "Add food to a meal", description = "Looks up nutritional data via FatSecret and adds the food with calculated macros to the meal")
    @PostMapping("/{mealId}/foods")
    public ResponseEntity<MealFoodResponse> addFoodToMeal(
            @PathVariable Long mealId,
            @Valid @RequestBody AddFoodToMealRequest request) {

        MealFoodResponse mealFood = mealService.addFoodToMeal(mealId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(mealFood);
    }

    @Operation(summary = "Get meal summary", description = "Returns total calories, protein, carbs, and fat for all foods in the meal")
    @GetMapping("/{mealId}/summary")
    public ResponseEntity<MealSummaryResponse> getMealSummary(
            @PathVariable Long mealId ){

        MealSummaryResponse summary = mealService.getMealSummary(mealId);

        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "Delete a meal", description = "Deletes the meal and all its associated foods (cascade delete)")
    @DeleteMapping("/{mealId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMeal(@PathVariable Long mealId){
        mealService.DeleteMeal(mealId);
    }

    @Operation(summary = "Remove food from meal", description = "Removes a specific food entry from the meal")
    @DeleteMapping("/{mealId}/foods/{mealFoodId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMealFood(
            @PathVariable Long mealId,
            @PathVariable Long mealFoodId
            ){
        mealService.DeleteMealFood(mealId, mealFoodId);
    }

    @Operation(summary = "Update food quantity", description = "Updates the quantity of a food and recalculates its macros using the FatSecret cache")
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
