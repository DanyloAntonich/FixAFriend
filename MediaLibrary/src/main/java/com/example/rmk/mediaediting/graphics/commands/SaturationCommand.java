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
public class SaturationCommand {
    private static final String ID = "com.rec.photoeditor.graphics.commands.BrightnessCommand";

    private int saturation = 0;

    public SaturationCommand(int saturation) {
        setBrightness(saturation);
    }

    public Bitmap process(Bitmap bitmap) {
//		Log.i("Image Processing Command", ID + " : " + brightness);
        float b = valueToMatrix(saturation);
        ColorMatrix cm = new ColorMatrix();

        float x = 1 + ((b > 0) ? 3 * b / 100 : b / 100);
        float lumR = 0.3086f;
        float lumG = 0.6094f;
        float lumB = 0.0820f;

        float[] mat = new float[]{
                lumR * (1 - x) + x, lumG * (1 - x), lumB * (1 - x), 0, 0, lumR * (1 - x),
                lumG * (1 - x) + x, lumB * (1 - x), 0, 0, lumR * (1 - x), lumG * (1 - x),
                lumB * (1 - x) + x, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1
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

    private float valueToMatrix(int val) {
        return ((float) val * 2);
    }

    public int getBrightness() {
        return saturation;
    }

    /**
     * Brightness values between -100 and 100
     */
    public void setBrightness(int brightness) {
        if (brightness < -100) {
            brightness = -100;
        } else if (brightness > 100) {
            brightness = 100;
        }
        this.saturation = brightness;
    }

    public String getId() {
        return ID;
    }

}
