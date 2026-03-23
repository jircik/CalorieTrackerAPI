package com.jircik.calorietrackerapi.domain.dto.response;

import com.jircik.calorietrackerapi.domain.entity.ActivityLevelEnum;
import com.jircik.calorietrackerapi.domain.entity.GenderEnum;

public record UserResponse(
   Long id,
   String name,
   String email,

   //Optional fields - if user hasn't configured the profile
   Integer age,
   Double heightInMeters,
   Double currentWeight,
   Double weightGoal,
   Double dailyCalorieIntakeGoal,
   GenderEnum gender,
   ActivityLevelEnum activityLevel

) {}
