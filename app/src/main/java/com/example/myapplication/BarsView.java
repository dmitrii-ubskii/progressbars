package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;


public class BarsView extends View {
    Paint paint = new Paint();

    public BarsView(Context context) {
        super(context);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(80);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int x = 30;
        int y = 30;
        int w = canvas.getWidth() - 60;
        int h = 150;

        paint.setColor(Color.CYAN);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        canvas.drawRect(x, y, x + w, y + h, paint);

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawRect(x, y, x + w, y + h, paint);

        String text = "100%";
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        float textY = y + (h - paint.ascent() - paint.descent()) / 2;
        canvas.drawText(text, x + w/2, textY, paint);
    }
}
