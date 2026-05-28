package com.finalterm.regfood.features.foodrecognition.view.ui;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FoodNutritionCalculatorTest {

    @Test
    public void normalize_removesVietnameseAccentsAndExtraSeparators() {
        assertEquals("pho bo dac biet", FoodNutritionCalculator.normalize(" Phở-bò   đặc biệt! "));
        assertEquals("banh mi", FoodNutritionCalculator.normalize("Bánh mì"));
    }

    @Test
    public void getDisplayFoodName_mapsKnownAiLabelsToVietnameseNames() {
        assertEquals("Phở bò", FoodNutritionCalculator.getDisplayFoodName("pho_bo"));
        assertEquals("Cơm tấm", FoodNutritionCalculator.getDisplayFoodName("com tam"));
        assertEquals("Món ăn", FoodNutritionCalculator.getDisplayFoodName("  "));
    }

    @Test
    public void getBaseCalories_returnsConfiguredCaloriesOrFallback() {
        assertEquals(430d, FoodNutritionCalculator.getBaseCalories("Phở bò"), 0.001d);
        assertEquals(650d, FoodNutritionCalculator.getBaseCalories("com_tam"), 0.001d);
        assertEquals(350d, FoodNutritionCalculator.getBaseCalories("unknown food"), 0.001d);
        assertEquals(350d, FoodNutritionCalculator.getBaseCalories(null), 0.001d);
    }

    @Test
    public void getSelectedPortionMultiplier_mapsSpinnerPositions() {
        assertEquals(0.8d, FoodNutritionCalculator.getSelectedPortionMultiplier(0), 0.001d);
        assertEquals(1.0d, FoodNutritionCalculator.getSelectedPortionMultiplier(1), 0.001d);
        assertEquals(1.3d, FoodNutritionCalculator.getSelectedPortionMultiplier(2), 0.001d);
        assertEquals(1.0d, FoodNutritionCalculator.getSelectedPortionMultiplier(99), 0.001d);
    }

    @Test
    public void getToppingsCalories_sumsKnownToppingsAndIgnoresUnknownValues() {
        assertEquals(290d, FoodNutritionCalculator.getToppingsCalories("Trứng, thịt, quẩy"), 0.001d);
        assertEquals(0d, FoodNutritionCalculator.getToppingsCalories("rau thơm, nước dùng"), 0.001d);
        assertEquals(0d, FoodNutritionCalculator.getToppingsCalories(null), 0.001d);
    }
}
