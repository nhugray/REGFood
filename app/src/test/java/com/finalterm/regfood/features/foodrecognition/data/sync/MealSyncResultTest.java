package com.finalterm.regfood.features.foodrecognition.data.sync;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MealSyncResultTest {

    @Test
    public void success_containsRemoteDocumentAndImageUrl() {
        MealSyncResult result = MealSyncResult.success(
                "users/uid-1/meal_logs/meal-1",
                "https://storage.example.com/meal.jpg"
        );

        assertTrue(result.success);
        assertEquals("users/uid-1/meal_logs/meal-1", result.mealDocumentPath);
        assertEquals("https://storage.example.com/meal.jpg", result.imageDownloadUrl);
        assertNull(result.errorMessage);
    }

    @Test
    public void error_containsFailureMessageOnly() {
        MealSyncResult result = MealSyncResult.error("Upload failed");

        assertFalse(result.success);
        assertNull(result.mealDocumentPath);
        assertNull(result.imageDownloadUrl);
        assertEquals("Upload failed", result.errorMessage);
    }
}
