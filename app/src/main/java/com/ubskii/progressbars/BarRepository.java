package com.ubskii.progressbars;

import java.util.List;

import android.app.Application;
import androidx.lifecycle.LiveData;

class BarRepository {
    private BarDao barDao;
    private LiveData<List<Bar>> allBars;

    BarRepository(Application application) {
        BarDatabase db = BarDatabase.getDatabase(application);
        barDao = db.barDao();
        allBars = barDao.getAll();
    }

    LiveData<List<Bar>> getAllBars() {
        return allBars;
    }

    void insert(Bar bar) {
        BarDatabase.databaseWriteExecutor.execute(() -> {
            barDao.insert(bar);
        });
    }

    void delete(Bar bar) {
        BarDatabase.databaseWriteExecutor.execute(() -> {
            barDao.delete(bar);
        });
    }
}
