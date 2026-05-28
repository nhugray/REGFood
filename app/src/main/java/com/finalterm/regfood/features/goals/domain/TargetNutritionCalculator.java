package com.finalterm.regfood.features.goals.domain;

public final class TargetNutritionCalculator {
    public static final String GENDER_FEMALE = "Nữ";
    public static final String GOAL_LOSE = "Giảm cân";
    public static final String GOAL_GAIN = "Tăng cân";

    private TargetNutritionCalculator() {
    }

    public static TargetNutrition calculate(int age,
                                            double heightCm,
                                            double weightKg,
                                            String gender,
                                            String activityLevel,
                                            String goalType) {
        double calories = calculateTargetCalories(age, heightCm, weightKg, gender, activityLevel, goalType);
        double protein = calories > 0d ? calories * 0.25d / 4.0d : 0d;
        double carbs = calories > 0d ? calories * 0.45d / 4.0d : 0d;
        double fat = calories > 0d ? calories * 0.30d / 9.0d : 0d;
        return new TargetNutrition(calories, protein, carbs, fat);
    }

    public static double calculateTargetCalories(int age,
                                                 double heightCm,
                                                 double weightKg,
                                                 String gender,
                                                 String activityLevel,
                                                 String goalType) {
        if (age <= 0 || heightCm <= 0d || weightKg <= 0d) {
            return 0d;
        }

        double bmr = GENDER_FEMALE.equalsIgnoreCase(gender)
                ? 10d * weightKg + 6.25d * heightCm - 5d * age - 161d
                : 10d * weightKg + 6.25d * heightCm - 5d * age + 5d;

        double tdee = bmr * getActivityMultiplier(activityLevel);
        if (GOAL_LOSE.equals(goalType)) {
            return Math.max(0d, tdee - 350d);
        }
        if (GOAL_GAIN.equals(goalType)) {
            return tdee + 300d;
        }
        return tdee;
    }

    public static double getActivityMultiplier(String activityLevel) {
        if ("Vận động nhẹ".equals(activityLevel)) {
            return 1.375d;
        }
        if ("Vận động vừa".equals(activityLevel)) {
            return 1.55d;
        }
        if ("Vận động nhiều".equals(activityLevel)) {
            return 1.725d;
        }
        if ("Rất năng động".equals(activityLevel)) {
            return 1.9d;
        }
        return 1.2d;
    }

    public static final class TargetNutrition {
        public final double calories;
        public final double protein;
        public final double carbs;
        public final double fat;

        public TargetNutrition(double calories, double protein, double carbs, double fat) {
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
        }
    }
}
