package com.example.rmk.mediaediting.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 * Created by RMK on 10/31/2015.
 */
public class EraserCommand {
    private Bitmap insertBitmap = null;

    public EraserCommand() {

    }

    public EraserCommand(Bitmap insertBitmap) {
        setBitmap(insertBitmap);
    }

    public Bitmap process(Bitmap resultbitmap, Bitmap originalBitmap) {

        Paint paint = new Paint(Paint.DEV_KERN_TEXT_FLAG);

        Paint mMaskPaint = new Paint();
        mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        Paint mImagePaint = new Paint();
        mImagePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));


        Bitmap result = Bitmap.createBitmap(resultbitmap.getWidth(),
                resultbitmap.getHeight(), resultbitmap.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(result, new Matrix(), paint);

        canvas.drawBitmap(insertBitmap, 0, 0, mMaskPaint);
        canvas.drawBitmap(originalBitmap, 0, 0, mImagePaint);

        return result;
    }
    public void setBitmap(Bitmap insertBitmap) {

        this.insertBitmap = insertBitmap;
    }
}
