package com.example.rmk.mediaediting.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by RMK on 10/26/2015.
 */
public class InsertTextCommand {
    private static final String ID = "com.rec.photoeditor.graphics.commands.BrightnessCommand";

    private String insertBitmap = null;

    public InsertTextCommand() {

    }

    public InsertTextCommand(String insertString) {
        setBitmap(insertString);
    }

    public Bitmap process(Bitmap bitmap, int left, int top, int fontsize){
        float width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        Paint paint = new Paint(Paint.DEV_KERN_TEXT_FLAG);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), bitmap.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmap, 0,0, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(2 * fontsize);
        canvas.drawText(insertBitmap, left, top, paint);

        return result;
    }

    public String getInsertBitmap() {
        return insertBitmap;
    }

    /**
     * Brightness values between -100 and 100
     */
    public void setBitmap(String insertBitmap) {

        this.insertBitmap = insertBitmap;
    }
}
