package com.finalterm.regfood.local;

import android.content.Context;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.finalterm.regfood.local.entity.FavoriteFoodEntity;
import com.finalterm.regfood.local.entity.FoodAliasEntity;
import com.finalterm.regfood.local.entity.FoodItemEntity;
import com.finalterm.regfood.local.entity.MealLogEntity;
import com.finalterm.regfood.local.entity.UserProfileEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AppDatabaseInstrumentedTest {
    private AppDatabase database;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void foodDao_insertAndQueryFoodByAiLabelAndAlias() {
        long now = System.currentTimeMillis();
        FoodItemEntity pho = createFood("FOOD_PHO", "Phở bò", "pho bo", "pho", 430d, true, now);

        long foodId = database.foodDao().insertFood(pho);
        database.foodDao().insertAliases(Arrays.asList(
                new FoodAliasEntity(foodId, "pho bo", "name", now),
                new FoodAliasEntity(foodId, "beef pho", "name", now)
        ));

        FoodItemEntity byAiLabel = database.foodDao().findByAiLabel("pho");
        FoodItemEntity byAlias = database.foodDao().findByAlias("beef pho");
        List<FoodItemEntity> activeFoods = database.foodDao().getActiveFoods();

        assertNotNull(byAiLabel);
        assertEquals("Phở bò", byAiLabel.foodName);
        assertNotNull(byAlias);
        assertEquals(foodId, byAlias.id);
        assertEquals(1, activeFoods.size());
        assertEquals("Phở bò", activeFoods.get(0).foodName);
    }

    @Test
    public void foodDao_getActiveFoods_excludesInactiveFoods() {
        long now = System.currentTimeMillis();
        database.foodDao().insertFoods(Arrays.asList(
                createFood("FOOD_ACTIVE", "Bánh mì", "banh mi", "banh_mi", 330d, true, now),
                createFood("FOOD_INACTIVE", "Món ẩn", "mon an", "hidden", 100d, false, now)
        ));

        List<FoodItemEntity> activeFoods = database.foodDao().getActiveFoods();

        assertEquals(1, activeFoods.size());
        assertEquals("Bánh mì", activeFoods.get(0).foodName);
    }

    @Test
    public void mealLogDao_insertQueryAndMarkSynced() {
        long now = System.currentTimeMillis();
        long userId = 42L;
        MealLogEntity breakfast = createMeal(userId, "Phở bò", 430d, "breakfast", now - 1_000L, false);
        MealLogEntity lunch = createMeal(userId, "Cơm tấm", 650d, "lunch", now, false);
        MealLogEntity anotherUserMeal = createMeal(7L, "Bánh mì", 330d, "dinner", now, false);

        long breakfastId = database.mealLogDao().insert(breakfast);
        long lunchId = database.mealLogDao().insert(lunch);
        database.mealLogDao().insert(anotherUserMeal);

        List<MealLogEntity> meals = database.mealLogDao().getMealsByUser(userId);
        List<MealLogEntity> unsyncedBefore = database.mealLogDao().getUnsyncedMeals();

        assertEquals(2, meals.size());
        assertEquals(lunchId, meals.get(0).id);
        assertEquals(breakfastId, meals.get(1).id);
        assertEquals(3, unsyncedBefore.size());

        database.mealLogDao().markSynced(lunchId);

        List<MealLogEntity> unsyncedAfter = database.mealLogDao().getUnsyncedMeals();
        assertEquals(2, unsyncedAfter.size());
        for (MealLogEntity meal : unsyncedAfter) {
            assertFalse(meal.id == lunchId);
        }
    }

    @Test
    public void favoriteFoodDao_upsertFindAndDeleteFavorite() {
        long now = System.currentTimeMillis();
        FavoriteFoodEntity favorite = new FavoriteFoodEntity(
                100L,
                200L,
                "Mì Quảng",
                "1 phần",
                "Món yêu thích",
                500d,
                0,
                "mi_quang",
                now
        );

        long favoriteId = database.favoriteFoodDao().upsert(favorite);
        FavoriteFoodEntity found = database.favoriteFoodDao().findByUserAndFood(100L, 200L);
        List<FavoriteFoodEntity> favorites = database.favoriteFoodDao().getFavoritesByUser(100L);

        assertNotNull(found);
        assertEquals(favoriteId, found.id);
        assertEquals(1, favorites.size());

        database.favoriteFoodDao().deleteByUserAndFood(100L, 200L);

        assertNull(database.favoriteFoodDao().findByUserAndFood(100L, 200L));
        assertTrue(database.favoriteFoodDao().getFavoritesByUser(100L).isEmpty());
    }

    @Test
    public void userProfileDao_insertAndFindByFirebaseUid() {
        long now = System.currentTimeMillis();
        UserProfileEntity profile = new UserProfileEntity(
                0L,
                "firebase-uid-1",
                "Nguyen Van A",
                "user@example.com",
                "Nam",
                25,
                170f,
                65f,
                "Vận động nhẹ",
                "Giữ cân",
                2100d,
                130d,
                230d,
                70d,
                now,
                now
        );

        long userId = database.userProfileDao().insert(profile);
        UserProfileEntity found = database.userProfileDao().findByFirebaseUid("firebase-uid-1");

        assertNotNull(found);
        assertEquals(userId, found.userId);
        assertEquals("user@example.com", found.email);
        assertEquals(2100d, found.targetCalories, 0.001d);
    }

    private FoodItemEntity createFood(String code,
                                      String name,
                                      String normalizedName,
                                      String aiLabel,
                                      double calories,
                                      boolean isActive,
                                      long now) {
        return new FoodItemEntity(
                code,
                name,
                normalizedName,
                aiLabel,
                "Món chính",
                "1 phần",
                calories,
                20d,
                50d,
                10d,
                3d,
                5d,
                500d,
                20d,
                "phần",
                "Món ăn test",
                null,
                isActive,
                now,
                now
        );
    }

    private MealLogEntity createMeal(long userId,
                                     String foodName,
                                     double calories,
                                     String mealType,
                                     long eatenAt,
                                     boolean isSynced) {
        return new MealLogEntity(
                userId,
                0L,
                foodName,
                foodName.toLowerCase(),
                1.0d,
                "Vừa (1.0x)",
                1.0d,
                "",
                calories,
                0d,
                0d,
                0d,
                0d,
                mealType,
                eatenAt,
                null,
                null,
                isSynced,
                eatenAt
        );
    }
}
