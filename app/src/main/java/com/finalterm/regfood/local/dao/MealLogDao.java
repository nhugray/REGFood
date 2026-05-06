package com.finalterm.regfood.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.finalterm.regfood.local.entity.MealLogEntity;

import java.util.List;

@Dao
public interface MealLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MealLogEntity mealLog);

    @Query("SELECT * FROM meal_logs WHERE userId = :userId ORDER BY eatenAt DESC")
    List<MealLogEntity> getMealsByUser(long userId);

    @Query("SELECT * FROM meal_logs WHERE isSynced = 0")
    List<MealLogEntity> getUnsyncedMeals();

    @Query("UPDATE meal_logs SET isSynced = 1 WHERE id = :mealId")
    void markSynced(long mealId);
}