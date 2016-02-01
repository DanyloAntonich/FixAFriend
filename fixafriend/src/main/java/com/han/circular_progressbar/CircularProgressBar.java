package com.han.circular_progressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by hic on 11/8/2015.
 */
public class CircularProgressBar extends View {

    private int mDuration = 100;
    private int mProgress = 10;
    private Paint mPaint = new Paint();
    private RectF mRectF = new RectF();
    private int mBackgroundColor = Color.LTGRAY;
    private int mPrimaryColor = Color.parseColor("#6DCAEC");
    private float mStrokeWidth = 10F;
    /**
     * è¿›åº¦æ?¡æ”¹å?˜ç›‘å?¬
     *
     * {@link #onChange( int duration, int progress, float rate)}
     */
    public interface OnProgressChangeListener {
        /**
         * è¿›åº¦æ”¹å?˜äº‹ä»¶ï¼Œå½“è¿›åº¦æ?¡è¿›åº¦æ”¹å?˜ï¼Œå°±ä¼šè°ƒç”¨è¯¥æ–¹æ³•
         * @param duration æ€»è¿›åº¦
         * @param progress å½“å‰?è¿›åº¦
         * @param rate å½“å‰?è¿›åº¦ä¸Žæ€»è¿›åº¦çš„å•† å?³ï¼šrate = (float)progress / duration
         */
        public void onChange( int duration, int progress, float rate);
    }
    private OnProgressChangeListener mOnChangeListener;
    /**
     * è®¾ç½®è¿›åº¦æ?¡æ”¹å?˜ç›‘å?¬
     * @param l
     */
    public void setOnProgressChangeListener(OnProgressChangeListener l)
    {
        mOnChangeListener = l;
    }
    public CircularProgressBar(Context context) {
        super(context);
    }
    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    /**
     * è®¾ç½®è¿›åº¦æ?¡çš„æœ€å¤§å€¼, è¯¥å€¼è¦? å¤§äºŽ 0
     * @param max
     */
    public void setMax( int max ) {
        if( max < 0 ) {
            max = 0;
        }
        mDuration = max;
    }
    /**
     * å¾—åˆ°è¿›åº¦æ?¡çš„æœ€å¤§å€¼
     * @return
     */
    public int getMax() {
        return mDuration;
    }
    /**
     * è®¾ç½®è¿›åº¦æ?¡çš„å½“å‰?çš„å€¼
     * @param progress
     */
    public void setProgress( int progress ) {
        if( progress > mDuration ) {
            progress = mDuration;
        }
        mProgress = progress;
        if( mOnChangeListener != null ) {
            mOnChangeListener.onChange(mDuration, progress, getRateOfProgress());
        }
        invalidate();
    }
    /**
     * å¾—åˆ°è¿›åº¦æ?¡å½“å‰?çš„å€¼
     * @return
     */
    public int getProgress() {
        return mProgress;
    }
    /**
     * è®¾ç½®è¿›åº¦æ?¡èƒŒæ™¯çš„é¢œè‰²
     */
    public void setBackgroundColor( int color ) {
        mBackgroundColor = color;
    }
    /**
     * è®¾ç½®è¿›åº¦æ?¡è¿›åº¦çš„é¢œè‰²
     */
    public void setPrimaryColor( int color ) {
        mPrimaryColor = color;
    }
    /**
     * è®¾ç½®çŽ¯å½¢çš„å®½åº¦
     * @param width
     */
    public void setCircleWidth(float width) {
        mStrokeWidth = width;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int halfWidth = getWidth() / 2;
        int halfHeight = getHeight() /2;
        int radius = halfWidth < halfHeight ? halfWidth : halfHeight;
        float halfStrokeWidth = mStrokeWidth / 2;
// è®¾ç½®ç”»ç¬”
        mPaint.setColor(mBackgroundColor);
        mPaint.setDither(true);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE); //è®¾ç½®å›¾å½¢ä¸ºç©ºå¿ƒ
// ç”»èƒŒæ™¯
        canvas.drawCircle(halfWidth, halfHeight, radius - halfStrokeWidth, mPaint);
// ç”»å½“å‰?è¿›åº¦çš„åœ†çŽ¯
        mPaint.setColor(mPrimaryColor); // æ”¹å?˜ç”»ç¬”é¢œè‰²
        mRectF.top = halfHeight - radius + halfStrokeWidth;
        mRectF.bottom = halfHeight + radius - halfStrokeWidth;
        mRectF.left = halfWidth - radius + halfStrokeWidth;
        mRectF.right = halfWidth + radius - halfStrokeWidth;
        canvas.drawArc(mRectF, -90, getRateOfProgress() * 360, false, mPaint);
        canvas.save();
    }
    /**
     * å¾—åˆ°å½“å‰?çš„è¿›åº¦çš„æ¯”çŽ‡
     * <p> ç”¨è¿›åº¦æ?¡å½“å‰?çš„å€¼ ä¸Ž è¿›åº¦æ?¡çš„æœ€å¤§å€¼æ±‚å•† </p>
     * @return
     */
    private float getRateOfProgress() {
        return (float)mProgress / mDuration;
    }
}
