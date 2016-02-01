package com.example.rmk.mediaediting.GallerySet;

import com.example.rmk.mediaediting.R;

import java.util.ArrayList;

/**
 * Created by RMK on 10/20/2015.
 */
public class EffectGalleryTool {
    public static final ArrayList<String> Names = new ArrayList<String>();

    static {
        Names.add("None");
        Names.add("Spot");
        Names.add("Hue");
        Names.add("Hightlight");
        Names.add("Bloom");
        Names.add("Gloom");
        Names.add("Posterize");
        Names.add("Pixelate");
    }
    public static final Integer[] ImageIds = new Integer[] {
            R.drawable.cleffectbase, R.drawable.clspoteffect, R.drawable.clhueeffect,
            R.drawable.clhighlightshadoweffect, R.drawable.clbloomeffect, R.drawable.clgloomeffect,
            R.drawable.clposterizeeffect, R.drawable.clpixellateeffect};
}
