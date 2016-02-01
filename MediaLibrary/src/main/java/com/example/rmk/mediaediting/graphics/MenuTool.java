package com.example.rmk.mediaediting.graphics;

import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphics.commands.EmptyCommand;
import com.example.rmk.mediaediting.graphics.commands.GaussianBlurCommand;
import com.example.rmk.mediaediting.graphics.commands.ImageProcessingCommand;

import java.util.ArrayList;

/**
 * Created by RMK on 10/18/2015.
 */
public class MenuTool {
    public static final ArrayList<String> Names = new ArrayList<String>();

    public static final String[] VideoEffectNames = new String[]{
        "Draw", "Emoticon", "Frame", "Sticker", "Text"
    };




    public static final Integer[] VideoEffectImageIds = new Integer[] {
            R.drawable.clvideodrawtoolwhite_icon, R.drawable.clvideoemoticontoolwhiteicon,R.drawable.clvideoframetoolwhiteicon,
            R.drawable.clvideostickertoolwhiteicon,R.drawable.clvideotexttoolwhiteicon};



    static {
        Names.add("Filter");
        Names.add("Adjustment");
        Names.add("Effect");
        Names.add("Blur");
        Names.add("Draw");
        Names.add("Splash");
        Names.add("Rotate");
        Names.add("Resize");
        Names.add("Crop");
        Names.add("Frame");
        Names.add("Emoticon");
        Names.add("Sticker");
        Names.add("Tonecurve");
        Names.add("Text");
    }
    public static final Integer[] ImageIds = new Integer[] {
            R.drawable.cffiltericon, R.drawable.clvideoadjustmenttool_white_icon, R.drawable.clvideoeffecttoolwhiteicon, R.drawable.clvideoblurtool_white_icon,
            R.drawable.clvideodrawtoolwhite_icon, R.drawable.clvideosplashtoolwhiteicon, R.drawable.clvideorotatetoolwhiteicon,
            R.drawable.clvideoresizetoolwhiteicon, R.drawable.clvideoclippingtoolwhite_icon, R.drawable.clvideoframetoolwhiteicon,
            R.drawable.clvideoemoticontoolwhiteicon, R.drawable.clvideostickertoolwhiteicon, R.drawable.clvideotonecurvetoolwhiteicon,
            R.drawable.clvideotexttoolwhiteicon};
}
