package com.jircik.calorietrackerapi.repository;

import com.jircik.calorietrackerapi.domain.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {

    List<Meal> findByUser_IdAndDatetimeBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

}
