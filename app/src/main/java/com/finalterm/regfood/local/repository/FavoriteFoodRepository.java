package com.finalterm.regfood.local.repository;

import android.content.Context;

import com.finalterm.regfood.local.AppDatabase;
import com.finalterm.regfood.local.RoomDatabaseProvider;
import com.finalterm.regfood.local.dao.FavoriteFoodDao;
import com.finalterm.regfood.local.entity.FavoriteFoodEntity;

import java.util.List;

public class FavoriteFoodRepository {

    private final FavoriteFoodDao favoriteFoodDao;

    public FavoriteFoodRepository(Context context) {
        AppDatabase db = RoomDatabaseProvider.getInstance(context);
        this.favoriteFoodDao = db.favoriteFoodDao();
    }

    public long saveFavorite(FavoriteFoodEntity favoriteFood) {
        return favoriteFoodDao.upsert(favoriteFood);
    }

    public void removeFavorite(long userId, long foodId) {
        favoriteFoodDao.deleteByUserAndFood(userId, foodId);
    }

    public FavoriteFoodEntity findFavorite(long userId, long foodId) {
        return favoriteFoodDao.findByUserAndFood(userId, foodId);
    }

    public List<FavoriteFoodEntity> getFavoritesByUser(long userId) {
        return favoriteFoodDao.getFavoritesByUser(userId);
    }
}
