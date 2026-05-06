package com.finalterm.regfood.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "nutrition_reference")
public class NutritionReferenceEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long foodId;
    public String referenceType;
    public double referenceAmount;
    public double calories;
    public double protein;
    public double carbs;
    public double fat;
    public double fiber;
    public double sugar;
    public double sodium;

    public NutritionReferenceEntity(long foodId, String referenceType, double referenceAmount,
                                    double calories, double protein, double carbs,
                                    double fat, double fiber, double sugar, double sodium) {
        this.foodId = foodId;
        this.referenceType = referenceType;
        this.referenceAmount = referenceAmount;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = fiber;
        this.sugar = sugar;
        this.sodium = sodium;
    }
}