package com.jircik.calorietrackerapi.controller;

import com.jircik.calorietrackerapi.domain.dto.response.DailySummaryResponse;
import com.jircik.calorietrackerapi.repository.UserRepository;
import com.jircik.calorietrackerapi.service.MealService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/users")
public class UserController {

    private final MealService mealService;

    public UserController(MealService mealService) {
        this.mealService = mealService;
    }

    @GetMapping("/{userId}/daily-summary")
    public ResponseEntity<DailySummaryResponse> getDailySummary(@PathVariable Long userId, @RequestParam LocalDate date) {

        DailySummaryResponse response = mealService.getDailySummary(userId, date);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }
}
