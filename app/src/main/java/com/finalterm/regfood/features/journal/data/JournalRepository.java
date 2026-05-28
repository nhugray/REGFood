package com.finalterm.regfood.features.journal.data;

import android.content.Context;

import com.finalterm.regfood.features.journal.domain.JournalSummary;
import com.finalterm.regfood.local.entity.MealLogEntity;
import com.finalterm.regfood.local.repository.MealRepository;
import com.finalterm.regfood.shared.session.UserSession;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JournalRepository {

    private final MealRepository mealRepository;

    public JournalRepository(Context context) {
        this.mealRepository = new MealRepository(context.getApplicationContext());
    }

    public JournalSummary loadTodaySummary() {
        long userId = UserSession.isGuest() ? 0L : Math.abs(UserSession.getCurrentEmail().hashCode());
        List<MealLogEntity> meals = mealRepository.getMealsByUser(userId);
        long startOfDay = startOfToday();
        double breakfastCalories = 0d;
        double lunchCalories = 0d;
        double dinnerCalories = 0d;
        double nightCalories = 0d;
        double protein = 0d;
        double carbs = 0d;
        double fat = 0d;
        int waterMl = 1500;
        Set<String> completedMealTypes = new HashSet<>();

        for (MealLogEntity meal : meals) {
            if (meal.eatenAt >= startOfDay) {
                double calories = meal.totalCalories;
                String normalizedMealType = normalizeMealType(meal.mealType);
                if ("breakfast".equals(normalizedMealType)) {
                    breakfastCalories += calories;
                    completedMealTypes.add("breakfast");
                } else if ("lunch".equals(normalizedMealType)) {
                    lunchCalories += calories;
                    completedMealTypes.add("lunch");
                } else if ("dinner".equals(normalizedMealType)) {
                    dinnerCalories += calories;
                    completedMealTypes.add("dinner");
                } else if ("night".equals(normalizedMealType)) {
                    nightCalories += calories;
                    completedMealTypes.add("night");
                }
                protein += meal.totalProtein;
                carbs += meal.totalCarbs;
                fat += meal.totalFat;
            }
        }

        double totalCalories = breakfastCalories + lunchCalories + dinnerCalories + nightCalories;
        int targetMeals = 4;
        int completedMeals = Math.min(completedMealTypes.size(), targetMeals);
        double ratio = targetMeals == 0 ? 0d : (double) completedMeals / targetMeals;
        String energyState = buildEnergyState(totalCalories);
        String nextMealHint = buildNextMealHint(completedMeals, totalCalories);

        return new JournalSummary(completedMealTypes.size(), completedMeals, breakfastCalories, lunchCalories,
                dinnerCalories, nightCalories, totalCalories, protein, carbs, fat,
                waterMl, targetMeals, ratio, energyState, nextMealHint);
    }

    private String normalizeMealType(String mealType) {
        if (mealType == null) {
            return "";
        }
        String value = mealType.trim().toLowerCase();
        if (value.contains("sáng") || value.contains("breakfast") || value.contains("morning")) {
            return "breakfast";
        }
        if (value.contains("trưa") || value.contains("lunch")) {
            return "lunch";
        }
        if (value.contains("tối") || value.contains("dinner")) {
            return "dinner";
        }
        if (value.contains("khuya") || value.contains("đêm") || value.contains("night")) {
            return "night";
        }
        return value;
    }

    private String buildEnergyState(double calories) {
        if (calories <= 400) {
            return "Nhẹ nhàng, còn nhiều dư địa cho bữa tiếp theo";
        }
        if (calories <= 900) {
            return "Ổn định, đang đi đúng nhịp trong ngày";
        }
        return "Năng lượng đã vào guồng, nên cân bằng cho bữa sau";
    }

    private String buildNextMealHint(int completedMeals, double calories) {
        if (completedMeals <= 0) {
            return "Bắt đầu ngày bằng một bữa sáng gọn và đủ protein";
        }
        if (completedMeals == 1) {
            return "Bữa tiếp theo nên có tinh bột chậm và rau xanh";
        }
        if (completedMeals == 2) {
            return calories > 1200 ? "Ưu tiên bữa tối nhẹ hơn để giữ nhịp calories" : "Bữa tối có thể bổ sung protein nạc";
        }
        return "Giữ bữa khuya hoặc bữa sau cùng thật nhẹ để hoàn thiện ngày ăn";
    }

    private long startOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
