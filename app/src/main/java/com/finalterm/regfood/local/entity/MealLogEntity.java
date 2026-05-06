package com.finalterm.regfood.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meal_logs")
public class MealLogEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;
    public long foodId;
    public String foodNameSnapshot;
    public String aiLabel;
    public double confidence;
    public String portionName;
    public double portionMultiplier;
    public String addonSummary;
    public double totalCalories;
    public double totalProtein;
    public double totalCarbs;
    public double totalFat;
    public double totalFiber;
    public String mealType;
    public long eatenAt;
    public String imageLocalPath;
    public String imageRemoteUrl;
    public boolean isSynced;
    public long createdAt;

    public MealLogEntity(long userId, long foodId, String foodNameSnapshot, String aiLabel,
                         double confidence, String portionName, double portionMultiplier,
                         String addonSummary, double totalCalories, double totalProtein,
                         double totalCarbs, double totalFat, double totalFiber, String mealType,
                         long eatenAt, String imageLocalPath, String imageRemoteUrl,
                         boolean isSynced, long createdAt) {
        this.userId = userId;
        this.foodId = foodId;
        this.foodNameSnapshot = foodNameSnapshot;
        this.aiLabel = aiLabel;
        this.confidence = confidence;
        this.portionName = portionName;
        this.portionMultiplier = portionMultiplier;
        this.addonSummary = addonSummary;
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
        this.totalCarbs = totalCarbs;
        this.totalFat = totalFat;
        this.totalFiber = totalFiber;
        this.mealType = mealType;
        this.eatenAt = eatenAt;
        this.imageLocalPath = imageLocalPath;
        this.imageRemoteUrl = imageRemoteUrl;
        this.isSynced = isSynced;
        this.createdAt = createdAt;
    }
}