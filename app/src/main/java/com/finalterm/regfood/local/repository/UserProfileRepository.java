package com.finalterm.regfood.local.repository;

import android.content.Context;

import com.finalterm.regfood.local.AppDatabase;
import com.finalterm.regfood.local.RoomDatabaseProvider;
import com.finalterm.regfood.local.entity.UserProfileEntity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserProfileRepository {

    private static final String COLLECTION_USERS = "user_profiles";

    private final AppDatabase database;
    private final FirebaseFirestore firestore;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public interface ProfileCallback {
        void onLoaded(UserProfileEntity profile);
    }

    public interface ResultCallback {
        void onSuccess();
        void onError(Exception error);
    }

    public UserProfileRepository(Context context) {
        this.database = RoomDatabaseProvider.getInstance(context);
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void saveProfile(UserProfileEntity profile, ResultCallback callback) {
        executorService.execute(() -> {
            try {
                UserProfileEntity existing = database.userProfileDao().findByFirebaseUid(profile.firebaseUid);
                if (existing != null) {
                    profile.userId = existing.userId;
                }
                database.userProfileDao().insert(profile);
                saveProfileToCloud(profile, callback);
            } catch (Exception e) {
                if (callback != null) callback.onError(e);
            }
        });
    }

    public void getProfileByUid(String firebaseUid, ProfileCallback callback) {
        executorService.execute(() -> {
            UserProfileEntity profile = database.userProfileDao().findByFirebaseUid(firebaseUid);
            if (callback != null) callback.onLoaded(profile);
        });
    }

    public UserProfileEntity getProfileByUidSync(String firebaseUid) {
        return database.userProfileDao().findByFirebaseUid(firebaseUid);
    }

    public double getTargetCaloriesSync(String firebaseUid) {
        UserProfileEntity profile = database.userProfileDao().findByFirebaseUid(firebaseUid);
        return profile != null ? profile.targetCalories : 0d;
    }

    private void saveProfileToCloud(UserProfileEntity profile, ResultCallback callback) {
        if (profile.firebaseUid == null || profile.firebaseUid.isEmpty()) {
            if (callback != null) callback.onError(new IllegalArgumentException("firebaseUid is required"));
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("firebaseUid", profile.firebaseUid);
        data.put("displayName", profile.displayName);
        data.put("email", profile.email);
        data.put("gender", profile.gender);
        data.put("age", profile.age);
        data.put("heightCm", profile.heightCm);
        data.put("weightKg", profile.weightKg);
        data.put("activityLevel", profile.activityLevel);
        data.put("goalType", profile.goalType);
        data.put("targetCalories", profile.targetCalories);
        data.put("targetProtein", profile.targetProtein);
        data.put("targetCarbs", profile.targetCarbs);
        data.put("targetFat", profile.targetFat);
        data.put("createdAt", profile.createdAt);
        data.put("updatedAt", profile.updatedAt);

        firestore.collection(COLLECTION_USERS)
                .document(profile.firebaseUid)
                .set(data)
                .addOnSuccessListener(unused -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e);
                });
    }
}
