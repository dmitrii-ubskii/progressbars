package com.ubskii.progressbars;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    BarsView barsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        barsView = new BarsView(this);
        barsView.setBackgroundColor(Color.LTGRAY);
        setContentView(barsView);
    }
}

