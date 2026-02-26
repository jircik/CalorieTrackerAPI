package com.jircik.calorietrackerapi.repository;

import com.jircik.calorietrackerapi.domain.entity.MealFood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealFoodRepository extends JpaRepository<MealFood, Long> {
}
