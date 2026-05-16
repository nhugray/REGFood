package com.finalterm.regfood.features.foodrecognition.data.sync;

import com.finalterm.regfood.local.entity.MealLogEntity;
import com.finalterm.regfood.local.repository.MealRepository;

import java.io.File;
import java.util.UUID;

public class MealSyncManager {

    private final MealRepository mealRepository;
    private final MealRemoteDataSource remoteDataSource;

    public MealSyncManager(MealRepository mealRepository, MealRemoteDataSource remoteDataSource) {
        this.mealRepository = mealRepository;
        this.remoteDataSource = remoteDataSource;
    }

    public void saveAndSync(File imageFile, MealLogEntity mealLog, String userUid, MealRemoteDataSource.SyncCallback callback) {
        if (mealLog == null) {
            callback.onError(new IllegalArgumentException("mealLog is null"));
            return;
        }

        String mealId = mealLog.id > 0 ? String.valueOf(mealLog.id) : UUID.randomUUID().toString();
        mealLog.isSynced = false;
        long insertedId = mealRepository.saveMeal(mealLog);
        String resolvedMealId = insertedId > 0 ? String.valueOf(insertedId) : mealId;
        mealLog.id = insertedId > 0 ? insertedId : mealLog.id;

        remoteDataSource.uploadMealImage(imageFile, resolvedMealId, new MealRemoteDataSource.UploadCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                mealLog.imageRemoteUrl = downloadUrl;
                mealLog.isSynced = true;
                mealRepository.saveMeal(mealLog);

                MealSyncPayload payload = new MealSyncPayload(
                        resolvedMealId,
                        userUid,
                        mealLog.foodNameSnapshot,
                        mealLog.aiLabel,
                        mealLog.confidence,
                        mealLog.portionName,
                        mealLog.portionMultiplier,
                        mealLog.addonSummary,
                        mealLog.totalCalories,
                        mealLog.mealType,
                        mealLog.eatenAt,
                        mealLog.imageLocalPath,
                        downloadUrl
                );

                remoteDataSource.syncMealLog(payload, new MealRemoteDataSource.SyncCallback() {
                    @Override
                    public void onSuccess(String documentPath, String imageDownloadUrl) {
                        mealRepository.markMealSynced(mealLog.id);
                        callback.onSuccess(documentPath, imageDownloadUrl);
                    }

                    @Override
                    public void onError(Exception error) {
                        callback.onError(error);
                    }
                });
            }

            @Override
            public void onError(Exception error) {
                callback.onError(error);
            }
        });
    }
}
