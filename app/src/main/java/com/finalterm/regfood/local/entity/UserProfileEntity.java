package com.finalterm.regfood.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_profiles")
public class UserProfileEntity {

    @PrimaryKey(autoGenerate = true)
    public long userId;
    public String firebaseUid;
    public String displayName;
    public String email;
    public String gender;
    public int age;
    public float heightCm;
    public float weightKg;
    public String activityLevel;
    public String goalType;
    public double targetCalories;
    public double targetProtein;
    public double targetCarbs;
    public double targetFat;
    public long createdAt;
    public long updatedAt;

    public UserProfileEntity(long userId, String firebaseUid, String displayName, String email, String gender,
                             int age, float heightCm, float weightKg, String activityLevel,
                             String goalType, double targetCalories, double targetProtein,
                             double targetCarbs, double targetFat, long createdAt, long updatedAt) {
        this.userId = userId;
        this.firebaseUid = firebaseUid;
        this.displayName = displayName;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.activityLevel = activityLevel;
        this.goalType = goalType;
        this.targetCalories = targetCalories;
        this.targetProtein = targetProtein;
        this.targetCarbs = targetCarbs;
        this.targetFat = targetFat;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}