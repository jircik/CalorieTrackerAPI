package com.jircik.calorietrackerapi.repository;

import com.jircik.calorietrackerapi.domain.entity.MealFood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealFoodRepository extends JpaRepository<MealFood, Long> {
    List<MealFood> findByMeal_Id(Long mealId);
    List<MealFood> findByMeal_IdIn(List<Long> mealIds);
}
