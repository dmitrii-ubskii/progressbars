package com.ubskii.progressbars;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;


public class BarsView extends View {
    Paint paint = new Paint();

    Bar sampleBar;

    public BarsView(Context context) {
        super(context);
        sampleBar = new Bar();
        sampleBar.uid = 0;
        sampleBar.title = "Example bar name";
        sampleBar.fractionDone = 0.48f;
    }

    int margin = 30;
    int barHeight = 150;

    private void drawBar(Bar bar, Canvas canvas, Point start) {
        int x = margin + start.x;
        int y = margin + start.y;
        int w = getWidth() - x - margin;
        int h = barHeight;

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(80);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        canvas.drawText(bar.title, x, y - paint.ascent(), paint);

        float titleHeight = - paint.ascent() + paint.descent();
        y += titleHeight;

        paint.setColor(Color.CYAN);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        canvas.drawRect(x, y, x + w * bar.fractionDone, y + h, paint);

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawRect(x, y, x + w, y + h, paint);

        String text = Integer.toString(Math.round(100 * bar.fractionDone)) + "%";
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(60);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        float textY = y + (h - paint.ascent() - paint.descent()) / 2;
        canvas.drawText(text, x + w/2, textY, paint);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawBar(sampleBar, canvas, new Point(0, 0));
    }
}
