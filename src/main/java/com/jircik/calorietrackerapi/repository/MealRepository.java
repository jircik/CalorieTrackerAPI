package com.jircik.calorietrackerapi.repository;

import com.jircik.calorietrackerapi.domain.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<Meal, Long> {
}
