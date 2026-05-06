package com.finalterm.regfood.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_aliases")
public class FoodAliasEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long foodId;
    public String aliasName;
    public String aliasType;
    public long createdAt;

    public FoodAliasEntity(long foodId, String aliasName, String aliasType, long createdAt) {
        this.foodId = foodId;
        this.aliasName = aliasName;
        this.aliasType = aliasType;
        this.createdAt = createdAt;
    }
}