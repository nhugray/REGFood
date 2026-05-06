package com.finalterm.regfood.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.finalterm.regfood.local.entity.UserProfileEntity;

@Dao
public interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(UserProfileEntity profile);

    @Query("SELECT * FROM user_profiles WHERE firebaseUid = :firebaseUid LIMIT 1")
    UserProfileEntity findByFirebaseUid(String firebaseUid);
}