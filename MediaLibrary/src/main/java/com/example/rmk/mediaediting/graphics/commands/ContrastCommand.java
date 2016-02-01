package com.example.rmk.mediaediting.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by RMK on 10/19/2015.
 */
public class ContrastCommand {

    public ContrastCommand(){
        contrast = 10;
    }

    private static final String ID = "com.rec.photoeditor.graphics.commands.BrightnessCommand";

    private static final double DELTA_INDEX[] = {
            0, 0.01, 0.02, 0.04, 0.05, 0.06, 0.07, 0.08, 0.1, 0.11, 0.12, 0.14, 0.15, 0.16, 0.17, 0.18,
            0.20, 0.21, 0.22, 0.24, 0.25, 0.27, 0.28, 0.30, 0.32, 0.34, 0.36, 0.38, 0.40, 0.42, 0.44,
            0.46, 0.48, 0.5, 0.53, 0.56, 0.59, 0.62, 0.65, 0.68, 0.71, 0.74, 0.77, 0.80, 0.83, 0.86, 0.89,
            0.92, 0.95, 0.98, 1.0, 1.06, 1.12, 1.18, 1.24, 1.30, 1.36, 1.42, 1.48, 1.54, 1.60, 1.66, 1.72,
            1.78, 1.84, 1.90, 1.96, 2.0, 2.12, 2.25, 2.37, 2.50, 2.62, 2.75, 2.87, 3.0, 3.2, 3.4, 3.6,
            3.8, 4.0, 4.3, 4.7, 4.9, 5.0, 5.5, 6.0, 6.5, 6.8, 7.0, 7.3, 7.5, 7.8, 8.0, 8.4, 8.7, 9.0, 9.4,
            9.6, 9.8, 10.0
    };

    private int contrast = 0;

    public ContrastCommand(int contrast) {
        setBrightness(contrast);
    }

    public Bitmap process(Bitmap bitmap) {
        ColorMatrix cm = new ColorMatrix();

        int b = (int) cleanValue(contrast, 100);

        float x;
        if (b < 0) {
            x = 127 + b / 100 * 127;
        } else {
            x = b % 1;
            if (x == 0) {
                x = (float) DELTA_INDEX[b];
            } else {
                x = (float) DELTA_INDEX[(b << 0)] * (1 - x)
                        + (float) DELTA_INDEX[(b << 0) + 1] * x;
            }
            x = x * 127 + 127;
        }

        float[] mat = new float[]{
                x / 127, 0, 0, 0, 0.5f * (127 - x), 0, x / 127, 0, 0, 0.5f * (127 - x), 0, 0,
                x / 127, 0, 0.5f * (127 - x), 0, 0, 0, 1, 0, 0, 0, 0, 0, 1
        };
        cm.set(mat);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));


        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), bitmap.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmap, new Matrix(), paint);

        return result;
    }

    private static float cleanValue(float p_val, float p_limit) {
        return Math.min(p_limit, Math.max(-p_limit, p_val));
    }

    public int getBrightness() {
        return contrast;
    }

    /**
     * Contrast values between -100 and 100
     */
    public void setBrightness(int brightness) {
        if (brightness < -100) {
            brightness = -100;
        } else if (brightness > 100) {
            brightness = 100;
        }
        this.contrast = brightness;
    }

    public String getId() {
        return ID;
    }
}
