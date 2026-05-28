package com.finalterm.regfood.features.foodrecognition.data.sync;

public class MealSyncPayload {
    public final String mealId;
    public final String userUid;
    public final String foodName;
    public final String aiLabel;
    public final double confidence;
    public final String portionName;
    public final double portionMultiplier;
    public final String toppings;
    public final double totalCalories;
    public final String mealType;
    public final long eatenAt;
    public final String imageLocalPath;
    public final String imageDownloadUrl;

    public MealSyncPayload(String mealId, String userUid, String foodName, String aiLabel,
                           double confidence, String portionName, double portionMultiplier,
                           String toppings, double totalCalories, String mealType,
                           long eatenAt, String imageLocalPath, String imageDownloadUrl) {
        this.mealId = mealId;
        this.userUid = userUid;
        this.foodName = foodName;
        this.aiLabel = aiLabel;
        this.confidence = confidence;
        this.portionName = portionName;
        this.portionMultiplier = portionMultiplier;
        this.toppings = toppings;
        this.totalCalories = totalCalories;
        this.mealType = mealType;
        this.eatenAt = eatenAt;
        this.imageLocalPath = imageLocalPath;
        this.imageDownloadUrl = imageDownloadUrl;
    }
}
