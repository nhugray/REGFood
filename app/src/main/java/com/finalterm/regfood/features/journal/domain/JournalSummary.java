package com.finalterm.regfood.features.journal.domain;

public class JournalSummary {
    public final int mealCount;
    public final int completedMeals;
    public final double breakfastCalories;
    public final double lunchCalories;
    public final double dinnerCalories;
    public final double nightCalories;
    public final double totalCalories;
    public final double totalProtein;
    public final double totalCarbs;
    public final double totalFat;
    public final int waterMl;
    public final int targetMeals;
    public final double mealCompletionRatio;
    public final String energyState;
    public final String nextMealHint;

    public JournalSummary(int mealCount, int completedMeals, double breakfastCalories, double lunchCalories,
                          double dinnerCalories, double nightCalories, double totalCalories, double totalProtein,
                          double totalCarbs, double totalFat, int waterMl, int targetMeals,
                          double mealCompletionRatio, String energyState, String nextMealHint) {
        this.mealCount = mealCount;
        this.completedMeals = completedMeals;
        this.breakfastCalories = breakfastCalories;
        this.lunchCalories = lunchCalories;
        this.dinnerCalories = dinnerCalories;
        this.nightCalories = nightCalories;
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
        this.totalCarbs = totalCarbs;
        this.totalFat = totalFat;
        this.waterMl = waterMl;
        this.targetMeals = targetMeals;
        this.mealCompletionRatio = mealCompletionRatio;
        this.energyState = energyState;
        this.nextMealHint = nextMealHint;
    }
}
