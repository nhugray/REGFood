package com.finalterm.regfood.features.foodrecognition.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class FoodPredictionResponse implements Serializable {
    @SerializedName("success")
    public boolean success;

    @SerializedName("data")
    public PredictionData data;

    @SerializedName("error")
    public String error;

    public static class PredictionData implements Serializable {
        @SerializedName("food")
        public String food;

        @SerializedName("confidence")
        public double confidence;

        @SerializedName("low_confidence")
        public boolean lowConfidence;

        @SerializedName("all_scores")
        public List<ScoreItem> allScores;

        @SerializedName("foodId")
        public String foodId;

        @SerializedName("name")
        public String name;

        @SerializedName("description")
        public String description;

        @SerializedName("imageUrl")
        public String imageUrl;

        @SerializedName("lowConfidence")
        public boolean isLowConfidence;

        @SerializedName("suggestions")
        public List<SuggestionItem> suggestions;
    }

    public static class ScoreItem implements Serializable {
        @SerializedName("label")
        public String label;

        @SerializedName("score")
        public double score;
    }

    public static class SuggestionItem implements Serializable {
        @SerializedName("foodId")
        public String foodId;

        @SerializedName("name")
        public String name;

        @SerializedName("confidence")
        public double confidence;
    }
}
