package com.example.rmk.mediaediting.graphic_process;

import android.graphics.drawable.GradientDrawable;

/**
 * Created by RMK on 11/12/2015.
 */
public class MakeDrawable extends GradientDrawable{
    public MakeDrawable(int pStartColor, int pCenterColor, int pEndColor, int pStrokeWidth, int pStrokeColor, float cornerRadius) {
        super(Orientation.BOTTOM_TOP,new int[]{pStartColor,pCenterColor,pEndColor});
        setStroke(pStrokeWidth,pStrokeColor);
        setShape(GradientDrawable.OVAL);
        setCornerRadius(cornerRadius);
    }
}
