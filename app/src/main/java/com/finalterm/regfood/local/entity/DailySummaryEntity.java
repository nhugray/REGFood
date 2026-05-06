package com.finalterm.regfood.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "daily_summaries")
public class DailySummaryEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long userId;
    public String date;
    public double totalCaloriesEaten;
    public double totalCaloriesTarget;
    public double remainingCalories;
    public double totalProteinEaten;
    public double totalCarbsEaten;
    public double totalFatEaten;
    public long createdAt;
    public long updatedAt;

    public DailySummaryEntity(long userId, String date, double totalCaloriesEaten,
                              double totalCaloriesTarget, double remainingCalories,
                              double totalProteinEaten, double totalCarbsEaten,
                              double totalFatEaten, long createdAt, long updatedAt) {
        this.userId = userId;
        this.date = date;
        this.totalCaloriesEaten = totalCaloriesEaten;
        this.totalCaloriesTarget = totalCaloriesTarget;
        this.remainingCalories = remainingCalories;
        this.totalProteinEaten = totalProteinEaten;
        this.totalCarbsEaten = totalCarbsEaten;
        this.totalFatEaten = totalFatEaten;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}