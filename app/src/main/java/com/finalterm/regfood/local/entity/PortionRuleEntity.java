package com.finalterm.regfood.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "portion_rules")
public class PortionRuleEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long foodId;
    public String portionName;
    public double multiplier;
    public String notes;
    public boolean isDefault;

    public PortionRuleEntity(long foodId, String portionName, double multiplier, String notes, boolean isDefault) {
        this.foodId = foodId;
        this.portionName = portionName;
        this.multiplier = multiplier;
        this.notes = notes;
        this.isDefault = isDefault;
    }
}