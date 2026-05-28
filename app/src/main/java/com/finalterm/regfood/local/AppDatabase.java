package com.finalterm.regfood.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.finalterm.regfood.local.dao.AiPredictionDao;
import com.finalterm.regfood.local.dao.DailySummaryDao;
import com.finalterm.regfood.local.dao.FavoriteFoodDao;
import com.finalterm.regfood.local.dao.FoodDao;
import com.finalterm.regfood.local.dao.MealLogDao;
import com.finalterm.regfood.local.dao.UserProfileDao;
import com.finalterm.regfood.local.dao.UserSettingsDao;
import com.finalterm.regfood.local.entity.AddonItemEntity;
import com.finalterm.regfood.local.entity.AiPredictionEntity;
import com.finalterm.regfood.local.entity.DailySummaryEntity;
import com.finalterm.regfood.local.entity.FavoriteFoodEntity;
import com.finalterm.regfood.local.entity.FoodAliasEntity;
import com.finalterm.regfood.local.entity.FoodItemEntity;
import com.finalterm.regfood.local.entity.MealLogEntity;
import com.finalterm.regfood.local.entity.NutritionReferenceEntity;
import com.finalterm.regfood.local.entity.PortionRuleEntity;
import com.finalterm.regfood.local.entity.UserProfileEntity;
import com.finalterm.regfood.local.entity.UserSettingsEntity;

@Database(
        entities = {
                UserProfileEntity.class,
                FoodItemEntity.class,
                FoodAliasEntity.class,
                PortionRuleEntity.class,
                AddonItemEntity.class,
                NutritionReferenceEntity.class,
                MealLogEntity.class,
                AiPredictionEntity.class,
                DailySummaryEntity.class,
                UserSettingsEntity.class,
                FavoriteFoodEntity.class
        },
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserProfileDao userProfileDao();
    public abstract FoodDao foodDao();
    public abstract MealLogDao mealLogDao();
    public abstract FavoriteFoodDao favoriteFoodDao();
    public abstract AiPredictionDao aiPredictionDao();
    public abstract DailySummaryDao dailySummaryDao();
    public abstract UserSettingsDao userSettingsDao();
}