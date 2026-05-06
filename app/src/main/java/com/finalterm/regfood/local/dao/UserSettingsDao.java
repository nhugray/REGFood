package com.finalterm.regfood.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.finalterm.regfood.local.entity.UserSettingsEntity;

@Dao
public interface UserSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long upsert(UserSettingsEntity settings);

    @Query("SELECT * FROM user_settings WHERE userId = :userId LIMIT 1")
    UserSettingsEntity findByUserId(long userId);
}