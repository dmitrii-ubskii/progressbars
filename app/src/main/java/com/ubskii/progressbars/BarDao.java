package com.ubskii.progressbars;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface BarDao {
    @Query("SELECT * FROM bar")
    List<Bar> getAll();

    @Query("SELECT * FROM bar WHERE uid IN (:barIds)")
    List<Bar> loadAllByIds(int[] barIds);

    @Insert
    void insertAll(Bar... bars);

    @Delete
    void delete(Bar bar);
}

