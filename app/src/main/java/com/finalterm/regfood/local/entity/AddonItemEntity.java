package com.finalterm.regfood.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "addon_items")
public class AddonItemEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String addonCode;
    public String addonName;
    public double caloriesAdd;
    public double proteinAdd;
    public double carbsAdd;
    public double fatAdd;
    public double fiberAdd;
    public double sodiumAdd;
    public String description;
    public boolean isActive;

    public AddonItemEntity(String addonCode, String addonName, double caloriesAdd, double proteinAdd,
                           double carbsAdd, double fatAdd, double fiberAdd, double sodiumAdd,
                           String description, boolean isActive) {
        this.addonCode = addonCode;
        this.addonName = addonName;
        this.caloriesAdd = caloriesAdd;
        this.proteinAdd = proteinAdd;
        this.carbsAdd = carbsAdd;
        this.fatAdd = fatAdd;
        this.fiberAdd = fiberAdd;
        this.sodiumAdd = sodiumAdd;
        this.description = description;
        this.isActive = isActive;
    }
}