package com.jircik.calorietrackerapi.controller;

import com.jircik.calorietrackerapi.domain.dto.request.CreateUserRequest;
import com.jircik.calorietrackerapi.domain.dto.response.DailySummaryResponse;
import com.jircik.calorietrackerapi.domain.dto.response.MealsByDateResponse;
import com.jircik.calorietrackerapi.domain.dto.response.UserResponse;
import com.jircik.calorietrackerapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/{userId}/daily-summary")
    public ResponseEntity<DailySummaryResponse> getDailySummary(@PathVariable Long userId, @RequestParam LocalDate date) {

        DailySummaryResponse response = userService.getDailySummary(userId, date);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @GetMapping("/{userId}/meals")
    public MealsByDateResponse getMealsByDate(
            @PathVariable Long userId,
            @RequestParam LocalDate date
    ) {
        return userService.getMealsByDate(userId, date);
    }
}
