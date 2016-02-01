package com.example.rmk.mediaediting.GallerySet;

import com.example.rmk.mediaediting.R;

import java.util.ArrayList;

/**
 * Created by RMK on 10/21/2015.
 */
public class BlurFocusGallleryTool {
    public static final ArrayList<String> Names = new ArrayList<String>();

    static {
        Names.add("Normal");
        Names.add("Circle");
        Names.add("Band");
    }
    public static final Integer[] ImageIds_BlurFocus = new Integer[] {
            R.drawable.btn_normal, R.drawable.btn_band, R.drawable.btn_circle};
}
