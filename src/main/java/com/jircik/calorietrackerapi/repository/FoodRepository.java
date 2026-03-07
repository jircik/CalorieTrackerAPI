package com.jircik.calorietrackerapi.repository;

import com.jircik.calorietrackerapi.domain.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {

    Optional<Food> findByNameIgnoreCase(String name);

}