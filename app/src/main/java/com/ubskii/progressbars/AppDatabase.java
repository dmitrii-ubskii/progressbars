package com.ubskii.progressbars;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Bar.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BarDao barDao();
}

