package com.finalterm.regfood.features.foodrecognition.view.ui;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class FoodNutritionCalculator {
    private static final Map<String, Double> BASE_CALORIES = new HashMap<>();
    private static final Map<String, Double> TOPPING_CALORIES = new HashMap<>();
    private static final Map<String, String> FOOD_DISPLAY_NAMES = new HashMap<>();

    static {
        registerFood("pho", "Phở bò", 430d);
        registerFood("pho bo", "Phở bò", 430d);
        registerFood("pho bo vien", "Phở bò", 430d);
        registerFood("pho ga", "Phở gà", 390d);
        registerFood("com tam", "Cơm tấm", 650d);
        registerFood("banh mi", "Bánh mì", 330d);
        registerFood("mi quang", "Mì Quảng", 500d);
        registerFood("bun cha ca", "Bún chả cá", 420d);

        registerTopping("trung", 90d);
        registerTopping("thit", 120d);
        registerTopping("quay", 80d);
        registerTopping("cha", 60d);
        registerTopping("xuc xich", 110d);
        registerTopping("nem", 70d);
        registerTopping("pate", 95d);
        registerTopping("pho mai", 75d);
    }

    private FoodNutritionCalculator() {
    }

    public static String getDisplayFoodName(String rawFoodName) {
        if (rawFoodName == null || rawFoodName.trim().isEmpty()) {
            return "Món ăn";
        }

        String key = normalize(rawFoodName);
        String displayName = FOOD_DISPLAY_NAMES.get(key);
        if (displayName != null) {
            return displayName;
        }

        return toTitleCase(rawFoodName);
    }

    public static double getBaseCalories(String foodName) {
        if (foodName == null) {
            return 350d;
        }
        String key = normalize(foodName);
        Double calories = BASE_CALORIES.get(key);
        return calories != null ? calories : 350d;
    }

    public static double getSelectedPortionMultiplier(int position) {
        switch (position) {
            case 0:
                return 0.8d;
            case 2:
                return 1.3d;
            case 1:
            default:
                return 1.0d;
        }
    }

    public static double getToppingsCalories(String toppingsInput) {
        if (toppingsInput == null || toppingsInput.trim().isEmpty()) {
            return 0d;
        }

        double total = 0d;
        String[] items = toppingsInput.toLowerCase(Locale.US).split(",");
        for (String item : items) {
            String key = normalize(item.trim());
            Double calories = TOPPING_CALORIES.get(key);
            if (calories != null) {
                total += calories;
            }
        }
        return total;
    }

    private static void registerFood(String normalizedKey, String displayName, double calories) {
        BASE_CALORIES.put(normalizedKey, calories);
        FOOD_DISPLAY_NAMES.put(normalizedKey, displayName);
    }

    private static void registerTopping(String normalizedKey, double calories) {
        TOPPING_CALORIES.put(normalizedKey, calories);
    }

    public static String normalize(String input) {
        if (input == null) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.US)
                .replace('đ', 'd');
        return normalized.replaceAll("[^a-z0-9]+", " ").trim().replaceAll("\\s+", " ");
    }

    private static String toTitleCase(String input) {
        String[] parts = input.toLowerCase(Locale.US).split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (builder.length() > 0) builder.append(' ');
            builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return builder.toString();
    }
}
