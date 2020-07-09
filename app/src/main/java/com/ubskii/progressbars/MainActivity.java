package com.ubskii.progressbars;

import java.util.List;
import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class MainActivity extends AppCompatActivity {
    public List<String> cursorToList(Cursor c) {
        List<String> data = new ArrayList<String>();
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                data.add(c.getString(c.getColumnIndex("title")) + " / " + Integer.toString((int) (100 * c.getFloat(c.getColumnIndex("fraction_done")))) + "%");
            }
        }
        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.listView);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name")
            .allowMainThreadQueries()  // FIXME
            .build();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cursorToList(db.barDao().getAll()));
        listView.setAdapter(adapter);
    }
}

