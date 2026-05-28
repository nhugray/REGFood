package com.finalterm.regfood.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.finalterm.regfood.local.entity.FoodAliasEntity;
import com.finalterm.regfood.local.entity.FoodItemEntity;
import com.finalterm.regfood.local.entity.NutritionReferenceEntity;
import com.finalterm.regfood.local.entity.PortionRuleEntity;

import java.util.List;

@Dao
public interface FoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertFood(FoodItemEntity foodItem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFoods(List<FoodItemEntity> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAliases(List<FoodAliasEntity> aliases);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPortionRules(List<PortionRuleEntity> rules);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNutritionReferences(List<NutritionReferenceEntity> references);

    @Query("SELECT * FROM food_items WHERE aiLabel = :aiLabel LIMIT 1")
    FoodItemEntity findByAiLabel(String aiLabel);

    @Query("SELECT fi.* FROM food_items fi INNER JOIN food_aliases fa ON fi.id = fa.foodId WHERE fa.aliasName = :aliasName LIMIT 1")
    FoodItemEntity findByAlias(String aliasName);

    @Query("SELECT * FROM food_items WHERE isActive = 1 ORDER BY foodName COLLATE NOCASE")
    List<FoodItemEntity> getActiveFoods();

    @Query("SELECT * FROM portion_rules WHERE foodId = :foodId")
    List<PortionRuleEntity> getPortionRules(long foodId);

    @Query("SELECT * FROM nutrition_reference WHERE foodId = :foodId")
    List<NutritionReferenceEntity> getNutritionReferences(long foodId);
}
