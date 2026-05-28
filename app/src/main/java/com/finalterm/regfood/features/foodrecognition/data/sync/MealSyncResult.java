package com.finalterm.regfood.features.foodrecognition.data.sync;

public class MealSyncResult {
    public final boolean success;
    public final String mealDocumentPath;
    public final String imageDownloadUrl;
    public final String errorMessage;

    private MealSyncResult(boolean success, String mealDocumentPath, String imageDownloadUrl, String errorMessage) {
        this.success = success;
        this.mealDocumentPath = mealDocumentPath;
        this.imageDownloadUrl = imageDownloadUrl;
        this.errorMessage = errorMessage;
    }

    public static MealSyncResult success(String mealDocumentPath, String imageDownloadUrl) {
        return new MealSyncResult(true, mealDocumentPath, imageDownloadUrl, null);
    }

    public static MealSyncResult error(String message) {
        return new MealSyncResult(false, null, null, message);
    }
}
