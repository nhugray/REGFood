package com.finalterm.regfood.features.journal.data;

import android.content.Context;

import com.finalterm.regfood.features.journal.domain.JournalSummary;
import com.finalterm.regfood.local.entity.MealLogEntity;
import com.finalterm.regfood.local.repository.MealRepository;
import com.finalterm.regfood.shared.session.UserSession;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JournalRepository {

    private final MealRepository mealRepository;

    public JournalRepository(Context context) {
        this.mealRepository = new MealRepository(context.getApplicationContext());
    }

    public JournalSummary loadTodaySummary() {
        long userId = UserSession.isGuest() ? 0L : Math.abs(UserSession.getCurrentEmail().hashCode());
        List<MealLogEntity> meals = mealRepository.getMealsByUser(userId);
        long startOfDay = startOfToday();
        int mealCount = 0;
        double breakfastCalories = 0d;
        double lunchCalories = 0d;
        double dinnerCalories = 0d;
        double nightCalories = 0d;
        double protein = 0d;
        double carbs = 0d;
        double fat = 0d;
        int waterMl = 1500;

        for (MealLogEntity meal : meals) {
            if (meal.eatenAt >= startOfDay) {
                mealCount++;
                double calories = meal.totalCalories;
                if ("breakfast".equalsIgnoreCase(meal.mealType)) {
                    breakfastCalories += calories;
                } else if ("lunch".equalsIgnoreCase(meal.mealType)) {
                    lunchCalories += calories;
                } else if ("dinner".equalsIgnoreCase(meal.mealType)) {
                    dinnerCalories += calories;
                } else if ("night".equalsIgnoreCase(meal.mealType)) {
                    nightCalories += calories;
                }
                protein += meal.totalProtein;
                carbs += meal.totalCarbs;
                fat += meal.totalFat;
            }
        }

        double totalCalories = breakfastCalories + lunchCalories + dinnerCalories + nightCalories;
        int targetMeals = 4;
        int completedMeals = Math.min(countMealTypes(breakfastCalories, lunchCalories, dinnerCalories, nightCalories), targetMeals);
        double ratio = targetMeals == 0 ? 0d : (double) completedMeals / targetMeals;
        String energyState = buildEnergyState(totalCalories);
        String nextMealHint = buildNextMealHint(completedMeals, totalCalories);

        return new JournalSummary(mealCount, completedMeals, breakfastCalories, lunchCalories,
                dinnerCalories, nightCalories, totalCalories, protein, carbs, fat,
                waterMl, targetMeals, ratio, energyState, nextMealHint);
    }

    private int countMealTypes(double breakfastCalories, double lunchCalories, double dinnerCalories, double nightCalories) {
        int count = 0;
        if (breakfastCalories > 0) count++;
        if (lunchCalories > 0) count++;
        if (dinnerCalories > 0) count++;
        if (nightCalories > 0) count++;
        return count;
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
