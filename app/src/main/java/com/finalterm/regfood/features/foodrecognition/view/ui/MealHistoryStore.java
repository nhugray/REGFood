package com.finalterm.regfood.features.foodrecognition.view.ui;

import android.content.Context;
import android.text.TextUtils;

import com.finalterm.regfood.local.entity.MealLogEntity;
import com.finalterm.regfood.local.repository.MealRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public final class MealHistoryStore {
    private static final String MEAL_IMAGE_DIR = "meal_images";

    private MealHistoryStore() {
    }

    public static long saveConfirmedMeal(Context context,
                                         String foodName,
                                         String aiLabel,
                                         double confidence,
                                         String portionName,
                                         double portionMultiplier,
                                         String addonSummary,
                                         double totalCalories,
                                         String imageLocalPath) {
        MealRepository repository = new MealRepository(context.getApplicationContext());
        String storedImagePath = persistImageIfNeeded(context, imageLocalPath);
        MealLogEntity entity = new MealLogEntity(
                0L,
                0L,
                foodName,
                aiLabel,
                confidence,
                portionName,
                portionMultiplier,
                addonSummary,
                totalCalories,
                0d,
                0d,
                0d,
                0d,
                "unknown",
                System.currentTimeMillis(),
                storedImagePath,
                null,
                false,
                System.currentTimeMillis()
        );
        return repository.saveMeal(entity);
    }

    public static String persistImageIfNeeded(Context context, String imageLocalPath) {
        if (TextUtils.isEmpty(imageLocalPath)) {
            return null;
        }

        File source = new File(imageLocalPath);
        if (!source.exists()) {
            return imageLocalPath;
        }

        File dir = new File(context.getFilesDir(), MEAL_IMAGE_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            return imageLocalPath;
        }

        File target = new File(dir, "meal_" + System.currentTimeMillis() + "_" + source.getName());
        try (InputStream inputStream = context.getContentResolver().openInputStream(android.net.Uri.fromFile(source));
             FileOutputStream outputStream = new FileOutputStream(target)) {
            if (inputStream == null) {
                return imageLocalPath;
            }
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            return target.getAbsolutePath();
        } catch (Exception e) {
            return imageLocalPath;
        }
    }
}
