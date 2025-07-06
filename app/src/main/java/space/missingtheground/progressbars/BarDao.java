package space.missingtheground.progressbars;

import java.util.List;

import android.database.Cursor;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface BarDao {
    @Query("SELECT * FROM bar_database ORDER BY listPosition ASC")
    LiveData<List<Bar>> getAll();

    @Insert
    long insert(Bar bars);

    @Delete
    void delete(Bar bar);

    @Update
    void update(Bar bar);

    @Transaction
    default void updateAll(List<Bar> bars) {
        for (Bar bar : bars) {
            update(bar);
        }
    }
}

