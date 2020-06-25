package com.example.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

public class MainActivity extends Activity {
    BarsView barsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        barsView = new BarsView(this);
        barsView.setBackgroundColor(Color.LTGRAY);
        setContentView(barsView);
    }
}

