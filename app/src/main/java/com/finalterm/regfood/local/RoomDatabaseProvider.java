package com.finalterm.regfood.local;

import android.content.Context;

import androidx.room.Room;

public final class RoomDatabaseProvider {

    private static volatile AppDatabase instance;

    private RoomDatabaseProvider() {
    }

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (RoomDatabaseProvider.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "regfood_local_db"
                    ).build();
                }
            }
        }
        return instance;
    }
}