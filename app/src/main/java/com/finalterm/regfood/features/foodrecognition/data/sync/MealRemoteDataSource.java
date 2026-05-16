package com.finalterm.regfood.features.foodrecognition.data.sync;

import java.io.File;

public interface MealRemoteDataSource {
    interface UploadCallback {
        void onSuccess(String downloadUrl);
        void onError(Exception error);
    }

    interface SyncCallback {
        void onSuccess(String documentPath, String imageDownloadUrl);
        void onError(Exception error);
    }

    void uploadMealImage(File imageFile, String mealId, UploadCallback callback);

    void syncMealLog(MealSyncPayload payload, SyncCallback callback);
}
