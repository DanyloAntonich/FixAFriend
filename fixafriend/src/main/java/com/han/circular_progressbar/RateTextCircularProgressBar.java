package com.han.circular_progressbar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by hic on 11/8/2015.
 */
public class RateTextCircularProgressBar extends FrameLayout implements CircularProgressBar.OnProgressChangeListener {
    private CircularProgressBar mCircularProgressBar;
    private TextView mRateText;
    public RateTextCircularProgressBar(Context context) {
        super(context);
        init();
    }
    public RateTextCircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mCircularProgressBar = new CircularProgressBar(getContext());
        this.addView(mCircularProgressBar);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        mCircularProgressBar.setLayoutParams(lp);
        mRateText = new TextView(getContext());
        this.addView(mRateText);
        mRateText.setLayoutParams(lp);
        mRateText.setGravity(Gravity.CENTER);
        mRateText.setTextColor(Color.BLACK);
        mRateText.setTextSize(20);
        mCircularProgressBar.setOnProgressChangeListener(this);
    }
    /**
     * è®¾ç½®æœ€å¤§å€¼
     */
    public void setMax( int max ) {
        mCircularProgressBar.setMax(max);
    }
    /**
     * è®¾ç½®è¿›åº¦
     * @param progress
     */
    public void setProgress(int progress) {
        mCircularProgressBar.setProgress(progress);
    }
    /**
     * å¾—åˆ° CircularProgressBar å¯¹è±¡ï¼Œç”¨æ?¥è®¾ç½®å…¶ä»–çš„ä¸€äº›å±žæ€§
     * @return
     */
    public CircularProgressBar getCircularProgressBar() {
        return mCircularProgressBar;
    }
    /**
     * è®¾ç½®ä¸­é—´è¿›åº¦ç™¾åˆ†æ¯”æ–‡å­—çš„å°ºå¯¸
     * @param size
     */
    public void setTextSize(float size) {
        mRateText.setTextSize(size);
    }
    /**
     * è®¾ç½®ä¸­é—´è¿›åº¦ç™¾åˆ†æ¯”æ–‡å­—çš„é¢œè‰²
     * @param color
     */
    public void setTextColor( int color) {
        mRateText.setTextColor(color);
    }

    @Override
    public void onChange(int duration, int progress, float rate) {
        mRateText.setText(String.valueOf( (int)(rate * 100 ) + "%"));
    }
}
