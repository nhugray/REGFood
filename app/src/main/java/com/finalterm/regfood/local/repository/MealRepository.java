package com.finalterm.regfood.local.repository;

import android.content.Context;

import com.finalterm.regfood.local.AppDatabase;
import com.finalterm.regfood.local.RoomDatabaseProvider;
import com.finalterm.regfood.local.dao.FoodDao;
import com.finalterm.regfood.local.dao.MealLogDao;
import com.finalterm.regfood.local.entity.FoodItemEntity;
import com.finalterm.regfood.local.entity.MealLogEntity;

import java.util.List;

public class MealRepository {

    private final MealLogDao mealLogDao;
    private final FoodDao foodDao;

    public MealRepository(Context context) {
        AppDatabase db = RoomDatabaseProvider.getInstance(context);
        this.mealLogDao = db.mealLogDao();
        this.foodDao = db.foodDao();
    }

    public long saveMeal(MealLogEntity mealLog) {
        return mealLogDao.insert(mealLog);
    }

    public FoodItemEntity findFoodByAiLabel(String label) {
        FoodItemEntity food = foodDao.findByAiLabel(label);
        if (food == null) {
            food = foodDao.findByAlias(label);
        }
        return food;
    }

    public List<FoodItemEntity> getActiveFoods() {
        return foodDao.getActiveFoods();
    }

    public List<MealLogEntity> getMealsByUser(long userId) {
        return mealLogDao.getMealsByUser(userId);
    }

    public List<MealLogEntity> getUnsyncedMeals() {
        return mealLogDao.getUnsyncedMeals();
    }

    public void markMealSynced(long mealId) {
        mealLogDao.markSynced(mealId);
    }
}
