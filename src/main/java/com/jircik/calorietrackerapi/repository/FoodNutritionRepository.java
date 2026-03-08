package com.jircik.calorietrackerapi.repository;

import com.jircik.calorietrackerapi.domain.entity.FoodNutrition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodNutritionRepository extends JpaRepository<FoodNutrition, String> {
}