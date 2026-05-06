package com.finalterm.regfood.local.repository;

import android.content.Context;

import com.finalterm.regfood.local.AppDatabase;
import com.finalterm.regfood.local.RoomDatabaseProvider;
import com.finalterm.regfood.local.entity.UserProfileEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserProfileRepository {

    private final AppDatabase database;
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
    }

    public void saveProfile(UserProfileEntity profile, ResultCallback callback) {
        executorService.execute(() -> {
            try {
                UserProfileEntity existing = database.userProfileDao().findByFirebaseUid(profile.firebaseUid);
                if (existing != null) {
                    profile.userId = existing.userId;
                }
                database.userProfileDao().insert(profile);
                if (callback != null) callback.onSuccess();
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
}