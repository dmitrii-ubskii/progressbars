package space.missingtheground.progressbars;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bar_database")
public class Bar {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "fraction_done")
    public float fractionDone;

    @ColumnInfo(name = "list_position")
    public int listPosition;
}
