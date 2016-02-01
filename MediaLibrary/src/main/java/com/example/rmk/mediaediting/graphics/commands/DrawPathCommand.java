package com.example.rmk.mediaediting.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by RMK on 10/30/2015.
 */
public class DrawPathCommand {
    private static final String ID = "com.rec.photoeditor.graphics.commands.BrightnessCommand";

    private String insertBitmap = null;

    public DrawPathCommand() {

    }

    public DrawPathCommand(String insertString) {
        setBitmap(insertString);
    }

    public Bitmap process(Bitmap bitmap, Path path, Paint paint){
        float width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        Paint paint1 = new Paint(Paint.DEV_KERN_TEXT_FLAG);

//        paint.setColor(Color.WHITE);
//        paint.setStyle(Paint.Style.FILL);

        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), bitmap.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmap, 0,0, paint1);

//        paint.setColor(Color.WHITE);
//        paint.setTextSize(2 * fontsize);
//        canvas.drawText(insertBitmap, left, top, paint);
        canvas.drawPath(path, paint);

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
