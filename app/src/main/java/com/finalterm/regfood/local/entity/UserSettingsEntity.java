package com.finalterm.regfood.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_settings")
public class UserSettingsEntity {

    @PrimaryKey(autoGenerate = false)
    public long userId;
    public String themeMode;
    public String language;
    public boolean notifyMealReminder;
    public boolean notifyWaterReminder;
    public boolean syncWithCloud;
    public long createdAt;
    public long updatedAt;

    public UserSettingsEntity(long userId, String themeMode, String language,
                              boolean notifyMealReminder, boolean notifyWaterReminder,
                              boolean syncWithCloud, long createdAt, long updatedAt) {
        this.userId = userId;
        this.themeMode = themeMode;
        this.language = language;
        this.notifyMealReminder = notifyMealReminder;
        this.notifyWaterReminder = notifyWaterReminder;
        this.syncWithCloud = syncWithCloud;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}