package com.finalterm.regfood.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_items")
public class FoodItemEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String foodCode;
    public String foodName;
    public String normalizedName;
    public String aiLabel;
    public String category;
    public String defaultServingSize;
    public double baseCalories;
    public double baseProtein;
    public double baseCarbs;
    public double baseFat;
    public double baseFiber;
    public double baseSugar;
    public double baseSodium;
    public double baseCholesterol;
    public String unitName;
    public String description;
    public String imageUrl;
    public boolean isActive;
    public long createdAt;
    public long updatedAt;

    public FoodItemEntity(String foodCode, String foodName, String normalizedName, String aiLabel,
                          String category, String defaultServingSize, double baseCalories,
                          double baseProtein, double baseCarbs, double baseFat, double baseFiber,
                          double baseSugar, double baseSodium, double baseCholesterol,
                          String unitName, String description, String imageUrl,
                          boolean isActive, long createdAt, long updatedAt) {
        this.foodCode = foodCode;
        this.foodName = foodName;
        this.normalizedName = normalizedName;
        this.aiLabel = aiLabel;
        this.category = category;
        this.defaultServingSize = defaultServingSize;
        this.baseCalories = baseCalories;
        this.baseProtein = baseProtein;
        this.baseCarbs = baseCarbs;
        this.baseFat = baseFat;
        this.baseFiber = baseFiber;
        this.baseSugar = baseSugar;
        this.baseSodium = baseSodium;
        this.baseCholesterol = baseCholesterol;
        this.unitName = unitName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}