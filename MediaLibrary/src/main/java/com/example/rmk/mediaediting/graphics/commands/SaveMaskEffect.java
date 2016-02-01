package com.example.rmk.mediaediting.graphics.commands;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by RMK on 10/31/2015.
 */
public class SaveMaskEffect {
    private static final String ID = "com.rec.photoeditor.graphics.commands.BrightnessCommand";

    private Bitmap insertBitmap = null;

    public SaveMaskEffect() {

    }

    public SaveMaskEffect(Bitmap insertBitmap) {
        setBitmap(insertBitmap);
    }

    public Bitmap process(Path maskPath, Bitmap bitmap, Bitmap originalImage, Paint mMaskPaint, Paint mImagePaint, float left, float top) {
        float width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        Paint mPaint = new Paint();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(10);

        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), bitmap.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmap, new Matrix(), mPaint);

//        canvas.drawBitmap(insertBitmap, left, top, mMaskPaint);
        canvas.drawPath(maskPath, mMaskPaint);
        canvas.drawBitmap(originalImage, 0, 0, mImagePaint);

        return result;
    }

    public Bitmap process(Bitmap bitmap, Bitmap originalImage, Paint mMaskPaint, Paint mImagePaint, float left, float top) {
        float width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        Paint mPaint = new Paint();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(10);

        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), bitmap.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmap, new Matrix(), mPaint);

        canvas.drawBitmap(insertBitmap, left, top, mMaskPaint);
        canvas.drawBitmap(originalImage, 0, 0, mImagePaint);

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
