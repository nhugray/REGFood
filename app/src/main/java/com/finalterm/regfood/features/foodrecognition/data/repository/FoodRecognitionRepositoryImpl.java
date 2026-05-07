package com.finalterm.regfood.features.foodrecognition.data.repository;

import android.util.Log;

import com.finalterm.regfood.features.foodrecognition.data.api.FoodRecognitionApiService;
import com.finalterm.regfood.features.foodrecognition.data.api.RetrofitClient;
import com.finalterm.regfood.features.foodrecognition.data.model.FoodPredictionResponse;
import com.finalterm.regfood.features.foodrecognition.domain.repository.FoodRecognitionRepository;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;

public class FoodRecognitionRepositoryImpl implements FoodRecognitionRepository {
    private static final String TAG = "FoodRecognitionRepo";
    private static final String IMAGE_PART_NAME = "image";
    private final FoodRecognitionApiService apiService;
    private Call<FoodPredictionResponse> currentCall;

    public FoodRecognitionRepositoryImpl() {
        this.apiService = RetrofitClient.getFoodRecognitionService();
    }

    @Override
    public void recognizeFood(File imageFile, RecognitionCallback callback) {
        if (imageFile == null || !imageFile.exists()) {
            callback.onError("File ảnh không tồn tại");
            return;
        }

        if (!isValidImageFile(imageFile)) {
            callback.onError("Định dạng file không hỗ trợ");
            return;
        }

        callback.onLoading(true);

        RequestBody fileBody = RequestBody.create(
                MediaType.parse("image/jpeg"),
                imageFile
        );

        MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                IMAGE_PART_NAME,
                imageFile.getName(),
                fileBody
        );

        currentCall = apiService.recognizeFood(imagePart);
        currentCall.enqueue(new Callback<FoodPredictionResponse>() {
            @Override
            public void onResponse(Call<FoodPredictionResponse> call, Response<FoodPredictionResponse> response) {
                callback.onLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    FoodPredictionResponse predictionResponse = response.body();
                    if (predictionResponse.success && predictionResponse.data != null) {
                        Log.d(TAG, "Food recognized: " + predictionResponse.data.food);
                        callback.onSuccess(predictionResponse);
                    } else {
                        String errorMsg = predictionResponse.error != null
                                ? predictionResponse.error
                                : "Lỗi không xác định từ server";
                        Log.e(TAG, "Server error: " + errorMsg);
                        callback.onError(errorMsg);
                    }
                } else {
                    String errorBody = null;
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    Log.e(TAG, "HTTP Error: " + response.code() + ", body=" + errorBody);
                    callback.onError("Lỗi server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<FoodPredictionResponse> call, Throwable t) {
                callback.onLoading(false);
                if (call.isCanceled()) {
                    callback.onError("Yêu cầu đã bị huỷ");
                    return;
                }
                Log.e(TAG, "Request failed", t);
                String errorMessage = t.getMessage() != null ? t.getMessage() : "Lỗi kết nối mạng";
                callback.onError("Lỗi kết nối: " + errorMessage);
            }
        });
    }

    @Override
    public void cancelRequest() {
        if (currentCall != null) {
            currentCall.cancel();
            currentCall = null;
        }
    }

    private boolean isValidImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                name.endsWith(".png") || name.endsWith(".webp");
    }
}
