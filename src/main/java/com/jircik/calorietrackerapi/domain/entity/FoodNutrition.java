package com.jircik.calorietrackerapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_nutrition")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodNutrition {

    @Id
    @Column(name = "fatsecret_food_id")
    private String fatSecretFoodId;

    @Column(nullable = false)
    private Double caloriesPer100g;

    @Column(nullable = false)
    private Double carbsPer100g;

    @Column(nullable = false)
    private Double proteinPer100g;

    @Column(nullable = false)
    private Double fatPer100g;
}