package com.jircik.calorietrackerapi.controller;

import com.jircik.calorietrackerapi.domain.dto.request.CreateUserRequest;
import com.jircik.calorietrackerapi.domain.dto.request.GetSummaryRequest;
import com.jircik.calorietrackerapi.domain.dto.response.DailySummaryResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealsByDateResponse;
import com.jircik.calorietrackerapi.domain.dto.response.SummaryResponse;
import com.jircik.calorietrackerapi.domain.dto.response.UserResponse;
import com.jircik.calorietrackerapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    // Deprecated Method, will redirect to new getPeriodSummary method
    @GetMapping("/{userId}/daily-summary")
    @Deprecated(since = "2026-03", forRemoval = true)
    public ResponseEntity<DailySummaryResponse> getDailySummaryLegacy(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        GetSummaryRequest request = new GetSummaryRequest(
                userId,
                date,
                date,
                "DAILY"
        );

        SummaryResponse newResponse = userService.getPeriodSummary(request);

        DailySummaryResponse legacyResponse = new DailySummaryResponse(
                newResponse.userId(),
                newResponse.startDate(),
                newResponse.totalCalories(),
                newResponse.totalProtein(),
                newResponse.totalCarbs(),
                newResponse.totalFat(),
                newResponse.mealCount(),
                newResponse.foodCount()
        );

        return ResponseEntity.ok(legacyResponse);
    }

    @GetMapping("/{userId}/summary")
    public ResponseEntity<SummaryResponse> getPeriodSummary(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String periodType) {
        GetSummaryRequest request = new GetSummaryRequest(
                userId,
                startDate,
                endDate,
                periodType
        );

        SummaryResponse response = userService.getPeriodSummary(request);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{userId}/meals")
    public MealsByDateResponse getMealsByDate(
            @PathVariable Long userId,
            @RequestParam LocalDate date
    ) {
        return userService.getMealsByDate(userId, date);
    }
}
