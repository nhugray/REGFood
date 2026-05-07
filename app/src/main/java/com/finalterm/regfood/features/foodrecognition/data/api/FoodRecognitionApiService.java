package com.finalterm.regfood.features.foodrecognition.data.api;

import com.finalterm.regfood.features.foodrecognition.data.model.FoodPredictionResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FoodRecognitionApiService {

    @GET("/")
    Call<ApiHealthResponse> getHealth();

    @POST("api/recognize")
    @Multipart
    Call<FoodPredictionResponse> recognizeFood(@Part MultipartBody.Part image);

    @POST("api/predict")
    @Multipart
    Call<FoodPredictionResponse> predictFood(@Part MultipartBody.Part image);

    class ApiHealthResponse {
        public String status;
        public boolean model_loaded;
        public int num_classes;
        public int image_size;
    }
}
