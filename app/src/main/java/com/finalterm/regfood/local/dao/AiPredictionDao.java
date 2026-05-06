package com.finalterm.regfood.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.finalterm.regfood.local.entity.AiPredictionEntity;

import java.util.List;

@Dao
public interface AiPredictionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(AiPredictionEntity prediction);

    @Query("SELECT * FROM ai_predictions ORDER BY createdAt DESC")
    List<AiPredictionEntity> getLatestPredictions();
}