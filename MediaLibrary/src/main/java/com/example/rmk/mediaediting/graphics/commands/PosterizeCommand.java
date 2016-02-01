package com.example.rmk.mediaediting.graphics.commands;

import android.graphics.Bitmap;

/**
 * Created by RMK on 10/20/2015.
 */
public class PosterizeCommand {
    public Bitmap process(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];


        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

//        for(int x = 0; x < pixels.length; ++x) {
//            pixels[x] = (pixels[x] == fromColor) ? targetColor : pixels[x];
//        }


        /*
        int A, R, G, B;
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();

		int[] pix = new int[width * height];
	    bitmap.getPixels(pix, 0, width, 0, 0, width, height);
	    for (int y = 0; y < height; y++)
	    {
	        for (int x = 0; x < width; x++)
	        {
	        	int index = y * width + x;
	        	A = (pix[index] >> 24) & 0xff;
				R = ( pix[index] >> 16 ) & 0xff;
		    	G = ( pix[index] >> 8 ) & 0xff;
		    	B = pix[index] & 0xff;

	            R = 255 - R;
	            G = 255 - G;
	            B = 255 - B;

	            pix[index] = A<<24 | (R << 16) | (G << 8 ) | B;
	        }
	    }
         */


        Bitmap newImage = Bitmap.createBitmap(width, height, bitmap.getConfig());
        newImage.setPixels(pixels, 0, width, 0, 0, width, height);

        return newImage;
    }
}
