package com.finalterm.regfood.features.foodrecognition.domain.repository;

import com.finalterm.regfood.features.foodrecognition.data.model.FoodPredictionResponse;
import java.io.File;

public interface FoodRecognitionRepository {
    void recognizeFood(File imageFile, RecognitionCallback callback);
    void cancelRequest();

    interface RecognitionCallback {
        void onSuccess(FoodPredictionResponse response);
        void onError(String errorMessage);
        void onLoading(boolean isLoading);
    }
}
