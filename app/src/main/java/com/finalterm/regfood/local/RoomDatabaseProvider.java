package com.finalterm.regfood.local;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.finalterm.regfood.local.repository.FoodCatalogSeeder;

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
                    )
                    .fallbackToDestructiveMigration()
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@androidx.annotation.NonNull androidx.sqlite.db.SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            new FoodCatalogSeeder(context.getApplicationContext()).seedIfNeeded(null);
                        }
                    })
                    .build();
                }
            }
        }
        return instance;
    }
}