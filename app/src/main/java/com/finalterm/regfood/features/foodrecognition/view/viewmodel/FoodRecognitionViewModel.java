package com.finalterm.regfood.features.foodrecognition.view.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.finalterm.regfood.features.foodrecognition.data.model.FoodPredictionResponse;
import com.finalterm.regfood.features.foodrecognition.data.repository.FoodRecognitionRepositoryImpl;
import com.finalterm.regfood.features.foodrecognition.domain.repository.FoodRecognitionRepository;
import java.io.File;

public class FoodRecognitionViewModel extends ViewModel {
    private final FoodRecognitionRepository repository;
    
    private final MutableLiveData<FoodPredictionResponse> predictionResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    public FoodRecognitionViewModel() {
        this.repository = new FoodRecognitionRepositoryImpl();
    }

    public LiveData<FoodPredictionResponse> getPredictionResult() {
        return predictionResult;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    public void recognizeFood(File imageFile) {
        if (isLoading.getValue() == Boolean.TRUE) {
            return;
        }

        statusMessage.setValue("Đang xử lý ảnh...");
        repository.recognizeFood(imageFile, new FoodRecognitionRepository.RecognitionCallback() {
            @Override
            public void onSuccess(FoodPredictionResponse response) {
                predictionResult.setValue(response);
                statusMessage.setValue("Nhận diện thành công");
            }

            @Override
            public void onError(String errorMessage) {
                FoodRecognitionViewModel.this.errorMessage.setValue(errorMessage);
                statusMessage.setValue("Lỗi: " + errorMessage);
                predictionResult.setValue(null);
            }

            @Override
            public void onLoading(boolean loading) {
                isLoading.setValue(loading);
                if (loading) {
                    errorMessage.setValue(null);
                }
            }
        });
    }

    public void clearResults() {
        predictionResult.setValue(null);
        errorMessage.setValue(null);
        statusMessage.setValue(null);
    }

    public void cancel() {
        repository.cancelRequest();
        isLoading.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cancel();
    }
}
