package space.missingtheground.progressbars;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bar_database")
public class Bar {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    public String title;
    public int progress;
    public int targetTotal;
    public int listPosition;
    public Integer parent; // null if this is a main bar

    public boolean isEditable() {
        return parent == null;
    }

    public int percentProgress() {
        return 100 * progress / targetTotal;
    }
}
