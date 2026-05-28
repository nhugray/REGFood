package com.finalterm.regfood.features.goals.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TargetNutritionCalculatorTest {

    @Test
    public void calculateTargetCalories_returnsZeroForInvalidBodyData() {
        assertEquals(0d, TargetNutritionCalculator.calculateTargetCalories(0, 170d, 65d, "Nam", "Ít vận động", "Giữ cân"), 0.001d);
        assertEquals(0d, TargetNutritionCalculator.calculateTargetCalories(25, 0d, 65d, "Nam", "Ít vận động", "Giữ cân"), 0.001d);
        assertEquals(0d, TargetNutritionCalculator.calculateTargetCalories(25, 170d, -1d, "Nam", "Ít vận động", "Giữ cân"), 0.001d);
    }

    @Test
    public void calculateTargetCalories_appliesMaleMaintainFormula() {
        double calories = TargetNutritionCalculator.calculateTargetCalories(
                25,
                170d,
                65d,
                "Nam",
                "Ít vận động",
                "Giữ cân"
        );

        assertEquals(1911d, calories, 0.001d);
    }

    @Test
    public void calculateTargetCalories_appliesFemaleLoseFormula() {
        double calories = TargetNutritionCalculator.calculateTargetCalories(
                30,
                160d,
                55d,
                "Nữ",
                "Vận động nhẹ",
                "Giảm cân"
        );

        assertEquals(1353.625d, calories, 0.001d);
    }

    @Test
    public void calculateTargetCalories_appliesGainGoalSurplus() {
        double calories = TargetNutritionCalculator.calculateTargetCalories(
                25,
                170d,
                65d,
                "Nam",
                "Ít vận động",
                "Tăng cân"
        );

        assertEquals(2211d, calories, 0.001d);
    }

    @Test
    public void calculate_derivesMacrosFromCalories() {
        TargetNutritionCalculator.TargetNutrition target = TargetNutritionCalculator.calculate(
                25,
                170d,
                65d,
                "Nam",
                "Ít vận động",
                "Giữ cân"
        );

        assertEquals(1911d, target.calories, 0.001d);
        assertEquals(119.4375d, target.protein, 0.001d);
        assertEquals(214.9875d, target.carbs, 0.001d);
        assertEquals(63.7d, target.fat, 0.001d);
    }

    @Test
    public void getActivityMultiplier_returnsConfiguredValuesAndFallback() {
        assertEquals(1.2d, TargetNutritionCalculator.getActivityMultiplier("Ít vận động"), 0.001d);
        assertEquals(1.375d, TargetNutritionCalculator.getActivityMultiplier("Vận động nhẹ"), 0.001d);
        assertEquals(1.55d, TargetNutritionCalculator.getActivityMultiplier("Vận động vừa"), 0.001d);
        assertEquals(1.725d, TargetNutritionCalculator.getActivityMultiplier("Vận động nhiều"), 0.001d);
        assertEquals(1.9d, TargetNutritionCalculator.getActivityMultiplier("Rất năng động"), 0.001d);
        assertEquals(1.2d, TargetNutritionCalculator.getActivityMultiplier("không rõ"), 0.001d);
    }
}
