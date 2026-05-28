package com.finalterm.regfood.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.finalterm.regfood.local.entity.FavoriteFoodEntity;

import java.util.List;

@Dao
public interface FavoriteFoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long upsert(FavoriteFoodEntity favoriteFood);

    @Delete
    void delete(FavoriteFoodEntity favoriteFood);

    @Query("SELECT * FROM favorite_foods WHERE userId = :userId ORDER BY createdAt DESC")
    List<FavoriteFoodEntity> getFavoritesByUser(long userId);

    @Query("SELECT * FROM favorite_foods WHERE userId = :userId AND foodId = :foodId LIMIT 1")
    FavoriteFoodEntity findByUserAndFood(long userId, long foodId);

    @Query("DELETE FROM favorite_foods WHERE userId = :userId AND foodId = :foodId")
    void deleteByUserAndFood(long userId, long foodId);
}
