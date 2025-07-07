package space.missingtheground.progressbars;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bar_database")
public class Bar {
    @PrimaryKey(autoGenerate = true)
    public long uid;

    public String title;
    public int progress;
    public int total;
    public int listPosition;
    public Long parent; // null if this is a main bar

    public int percentProgress() {
        if (total == 0) return 0;
        return 100 * progress / total;
    }
}
