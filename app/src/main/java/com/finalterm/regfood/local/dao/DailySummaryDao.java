package com.finalterm.regfood.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.finalterm.regfood.local.entity.DailySummaryEntity;

@Dao
public interface DailySummaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(DailySummaryEntity summary);

    @Query("SELECT * FROM daily_summaries WHERE userId = :userId AND date = :date LIMIT 1")
    DailySummaryEntity findByDate(long userId, String date);
}