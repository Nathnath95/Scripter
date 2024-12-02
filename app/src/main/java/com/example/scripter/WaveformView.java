package com.example.scripter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class  WaveformView extends View {

    private Paint paint;
    private float[] waveformPoints;

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2f);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void updateWaveform(float[] waveform) {
        waveformPoints = waveform;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (waveformPoints == null || waveformPoints.length < 2) return;

        float width = getWidth();
        float height = getHeight();
        float centerY = height / 2;

        canvas.drawColor(Color.BLACK);

        for (int i = 0; i < waveformPoints.length - 1; i++) {
            float startX = (i * width) / waveformPoints.length;
            float stopX = ((i + 1) * width) / waveformPoints.length;

            canvas.drawLine(
                    startX, centerY + waveformPoints[i] * centerY,
                    stopX, centerY + waveformPoints[i + 1] * centerY,
                    paint
            );
        }
    }
}
