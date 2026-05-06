package com.finalterm.regfood.local.seed;

import android.content.Context;

import com.finalterm.regfood.local.AppDatabase;
import com.finalterm.regfood.local.RoomDatabaseProvider;
import com.finalterm.regfood.local.entity.FoodAliasEntity;
import com.finalterm.regfood.local.entity.FoodItemEntity;
import com.finalterm.regfood.local.entity.PortionRuleEntity;

import java.util.ArrayList;
import java.util.List;

public final class NutritionSeeder {

    private NutritionSeeder() {
    }

    public static void seedIfNeeded(Context context) {
        AppDatabase db = RoomDatabaseProvider.getInstance(context);
        if (db.foodDao().findByAiLabel("pho_bo") != null) {
            return;
        }

        long now = System.currentTimeMillis();
        FoodItemEntity phoBo = new FoodItemEntity(
                "FOOD_PHO_BO_001",
                "Phở bò",
                "pho bo",
                "pho_bo",
                "noodle_soup",
                "1 tô",
                450,
                24,
                55,
                12,
                2,
                4,
                1200,
                90,
                "tô",
                "Món chuẩn cho nhận diện pho_bo",
                null,
                true,
                now,
                now
        );
        long foodId = db.foodDao().insertFood(phoBo);

        List<FoodAliasEntity> aliases = new ArrayList<>();
        aliases.add(new FoodAliasEntity(foodId, "pho", "label", now));
        aliases.add(new FoodAliasEntity(foodId, "phở", "label", now));
        aliases.add(new FoodAliasEntity(foodId, "beef_pho", "label", now));
        db.foodDao().insertAliases(aliases);

        List<PortionRuleEntity> rules = new ArrayList<>();
        rules.add(new PortionRuleEntity(foodId, "small", 0.8, "Tô nhỏ", false));
        rules.add(new PortionRuleEntity(foodId, "medium", 1.0, "Tô vừa", true));
        rules.add(new PortionRuleEntity(foodId, "large", 1.3, "Tô lớn", false));
        db.foodDao().insertPortionRules(rules);
    }
}