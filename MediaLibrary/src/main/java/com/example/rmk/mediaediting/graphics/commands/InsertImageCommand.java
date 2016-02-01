package com.example.rmk.mediaediting.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by RMK on 10/23/2015.
 */
public class InsertImageCommand {
    private static final String ID = "com.rec.photoeditor.graphics.commands.BrightnessCommand";

    private Bitmap insertBitmap = null;

    public InsertImageCommand() {

    }

    public InsertImageCommand(Bitmap insertBitmap) {
        setBitmap(insertBitmap);
    }

    public Bitmap process(Bitmap bitmap) {
//        float b = valueToMatrix(brightness);
        float width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        Paint paint = new Paint(Paint.DEV_KERN_TEXT_FLAG);

        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), bitmap.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        canvas.drawBitmap(insertBitmap, 0, 0, paint);

        return result;
    }
    public Bitmap process(Bitmap bitmap, int left, int top){
        float width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        Paint paint = new Paint(Paint.DEV_KERN_TEXT_FLAG);

        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), bitmap.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        canvas.drawBitmap(insertBitmap, left, top, paint);

        return result;
    }

    public Bitmap process(Bitmap background, Bitmap textBitmap, float left, float top, Paint textPaint)
    {
        Paint paint = new Paint(Paint.DEV_KERN_TEXT_FLAG);

        Bitmap result = Bitmap.createBitmap(background.getWidth(),
                background.getHeight(), background.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(background, new Matrix(), paint);
        canvas.drawBitmap(textBitmap, left, top, textPaint);
        return result;
    }



    public Bitmap getInsertBitmap() {
        return insertBitmap;
    }

    /**
     * Brightness values between -100 and 100
     */
    public void setBitmap(Bitmap insertBitmap) {

        this.insertBitmap = insertBitmap;
    }
}
