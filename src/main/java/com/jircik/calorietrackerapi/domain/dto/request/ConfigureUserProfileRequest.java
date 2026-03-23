package com.jircik.calorietrackerapi.domain.dto.request;

import com.jircik.calorietrackerapi.domain.entity.ActivityLevelEnum;
import com.jircik.calorietrackerapi.domain.entity.GenderEnum;

public record ConfigureUserProfileRequest(
        Integer age,
        Double heightInMeters,
        Double currentWeight,
        Double weightGoal,
        Double dailyCalorieIntakeGoal,
        GenderEnum gender,
        ActivityLevelEnum activityLevel
) {
}