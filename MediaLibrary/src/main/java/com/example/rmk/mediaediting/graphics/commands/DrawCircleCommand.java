package com.example.rmk.mediaediting.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.Paint;

/**
 * Created by RMK on 10/22/2015.
 */
public class DrawCircleCommand {
    private static final String ID = "com.rec.photoeditor.graphics.commands.BrightnessCommand";

    private int radius = 0;
    private float x = 0;
    private float y = 0;
    private int color = 0;

    public DrawCircleCommand() {
        radius = 10;
        x = 0; y = 0;
        color = Color.red(255);
    }

    public DrawCircleCommand(int radius, float x, float y, int color) {

        setCircle(radius, x, y, color);
    }

    public Bitmap process(Bitmap bitmap) {
//		Log.i("Image Processing Command", ID + " : " + brightness);
        float b = valueToMatrix(1);

        ColorMatrix cm = new ColorMatrix();
        cm.set(new float[] { 1, 0, 0, 0, b, 0, 1, 0, 0, b, 0, 0, 1, 0, b, 0, 0,
                0, 1, 0 });

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);

        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), bitmap.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, radius, paint);

        return result;
    }

    private float valueToMatrix(int val) {
        return ((float) val * 2);
    }

//    public int getBrightness() {
//        return brightness;
//    }

    /**
     * Brightness values between -100 and 100
     */
    public void setCircle(int radius, float x, float y, int color) {

        this.radius = radius;
        this.x = x; this.y = y;
        this.color = color;
    }

    public String getId() {
        return ID;
    }
}
