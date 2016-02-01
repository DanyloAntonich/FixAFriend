package com.example.rmk.mediaediting.graphic_process;

/**
 * Created by RMK on 11/12/2015.
 */
public class DrawTextInformation {
    private static DrawTextInformation instance = null;

    private String rText;
    private int rTextViewWidth, rTextViewHeight;
    private int rTextColor, rStrokeColor;
    private int rTextSaturation, rStrokeSaturation;
    private int rTextAlpha, rStrokeAlpha;
    private int rTextThickness, rStrokeThickness;
    private String rTextFontType;

    public static DrawTextInformation getInstance(){
        if (instance == null){
            instance = new DrawTextInformation();
        }
        return instance;
    }
    public void setrText(String s){ rText = s;}
    public void setrTextViewWidthHeight(int w, int h){
        rTextViewWidth = w; rTextViewHeight = h;
    }
    public void setrTextColor(int rgb){
        rTextColor = rgb;
    }
    public void setrStrokeColor(int rgb){
        rStrokeColor = rgb;
    }
    public void setrTextSaturation(int sat){
        rTextSaturation = sat;
    }
    public void setrStrokeSaturation(int sat){
        rStrokeSaturation = sat;
    }
    public void setrTextAlpha(int alpha){
        rTextAlpha = alpha;
    }
    public void setrStrokeAlpha(int alpha){
        rStrokeAlpha = alpha;
    }
    public void setrTextThickness(int thickness){
        rTextThickness = thickness;
    }
    public void setrStrokeThickness(int thickness){
        rStrokeThickness = thickness;
    }
    public void setrTextFontType(String string){
        rTextFontType = string;
    }

    public String getrText(){ return rText;}
    public int getrTextViewWidth() { return rTextViewWidth;}
    public int getrTextViewHeight() {return rTextViewHeight;}
    public int getrTextColor(){ return rTextColor;}
    public int getrStrokeColor(){return rStrokeColor;}
    public int getrTextSaturation(){return rTextSaturation;}
    public int getrStrokeSaturation(){return rStrokeSaturation;}
    public int getrTextAlpha(){return rTextAlpha;}
    public int getrStrokeAlpha(){return rStrokeAlpha;}
    public int getrTextThickness(){return rTextThickness;}
    public int getrStrokeThickness(){return rStrokeThickness;}
    public String getrTextFontType(){return rTextFontType;}

}
