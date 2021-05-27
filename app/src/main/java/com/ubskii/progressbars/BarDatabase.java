package com.ubskii.progressbars;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Bar.class}, version = 1, exportSchema = false)
public abstract class BarDatabase extends RoomDatabase {
    public abstract BarDao barDao();

    private static volatile BarDatabase Instance;
    private static final int NumberOfThreads = 4;
    static final ExecutorService databaseWriteExecutor =
        Executors.newFixedThreadPool(NumberOfThreads);

    static BarDatabase getDatabase(final Context context) {
        if (Instance == null) {
            synchronized (BarDatabase.class) {
                if (Instance == null) {
                    Instance = Room.databaseBuilder(context.getApplicationContext(), BarDatabase.class, "bar_database").build();
                }
            }
        }
        return Instance;
    }
}

