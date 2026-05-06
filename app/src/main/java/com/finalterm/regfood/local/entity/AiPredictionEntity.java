package com.finalterm.regfood.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ai_predictions")
public class AiPredictionEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String imageLocalPath;
    public String predictedLabel;
    public String predictedFoodName;
    public double confidence;
    public String topKJson;
    public long predictedAt;
    public boolean isConfirmed;
    public Long confirmedFoodId;
    public String confirmedPortion;
    public long createdAt;

    public AiPredictionEntity(String imageLocalPath, String predictedLabel, String predictedFoodName,
                              double confidence, String topKJson, long predictedAt,
                              boolean isConfirmed, Long confirmedFoodId, String confirmedPortion,
                              long createdAt) {
        this.imageLocalPath = imageLocalPath;
        this.predictedLabel = predictedLabel;
        this.predictedFoodName = predictedFoodName;
        this.confidence = confidence;
        this.topKJson = topKJson;
        this.predictedAt = predictedAt;
        this.isConfirmed = isConfirmed;
        this.confirmedFoodId = confirmedFoodId;
        this.confirmedPortion = confirmedPortion;
        this.createdAt = createdAt;
    }
}