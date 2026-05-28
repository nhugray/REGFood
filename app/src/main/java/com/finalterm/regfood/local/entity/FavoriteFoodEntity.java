package com.finalterm.regfood.local.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "favorite_foods",
        indices = {
                @Index(value = {"userId", "foodId"}, unique = true)
        }
)
public class FavoriteFoodEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;
    public long foodId;
    public String foodNameSnapshot;
    public String subtitleSnapshot;
    public String descriptionSnapshot;
    public double caloriesSnapshot;
    public int imageResId;
    public String aiLabel;
    public long createdAt;

    public FavoriteFoodEntity(long userId, long foodId, String foodNameSnapshot, String subtitleSnapshot,
                              String descriptionSnapshot, double caloriesSnapshot, int imageResId,
                              String aiLabel, long createdAt) {
        this.userId = userId;
        this.foodId = foodId;
        this.foodNameSnapshot = foodNameSnapshot;
        this.subtitleSnapshot = subtitleSnapshot;
        this.descriptionSnapshot = descriptionSnapshot;
        this.caloriesSnapshot = caloriesSnapshot;
        this.imageResId = imageResId;
        this.aiLabel = aiLabel;
        this.createdAt = createdAt;
    }
}
