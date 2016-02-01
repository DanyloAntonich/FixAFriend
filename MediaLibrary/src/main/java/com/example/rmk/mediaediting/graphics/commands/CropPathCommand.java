package com.example.rmk.mediaediting.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by RMK on 10/31/2015.
 */
public class CropPathCommand {

    public CropPathCommand() {

    }

    public Bitmap process(Bitmap bitmap, Bitmap empty, Path path, Paint pathPaint) {
        Paint paint = new Paint(Paint.DEV_KERN_TEXT_FLAG);

        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), bitmap.getConfig());



        Canvas canvas = new Canvas(result);

        canvas.drawPath(path, pathPaint);
        return result;
    }

}
