package space.missingtheground.progressbars;

import java.util.List;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class BarViewModel extends AndroidViewModel {
    private BarRepository repository;

    private LiveData<List<Bar>> allBars;

    public BarViewModel(Application application) {
        super(application);
        repository = new BarRepository(application);
        allBars = repository.getAllBars();
    }

    LiveData<List<Bar>> getAllBars() {
        return allBars;
    }

    public void insert(Bar bar) {
        repository.insert(bar, null);
    }

    public void insert(Bar bar, Runnable callback) {
        repository.insert(bar, callback);
    }

    public void delete(Bar bar) {
        repository.delete(bar);
    }

    public void deleteById(long id) {
        repository.deleteById(id);
    }

    public void update(Bar bar) {
        repository.update(bar, null);
    }

    public void update(Bar bar, Runnable callback) {
        repository.update(bar, callback);
    }

    public void updateAll(List<Bar> bars) {
        repository.updateAll(bars);
    }
} 
