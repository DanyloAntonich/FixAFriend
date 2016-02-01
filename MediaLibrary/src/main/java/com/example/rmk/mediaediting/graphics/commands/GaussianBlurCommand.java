package com.example.rmk.mediaediting.graphics.commands;

import android.graphics.Bitmap;

import com.example.rmk.mediaediting.graphics.ConvolutionMatrix;

public class GaussianBlurCommand implements ImageProcessingCommand {

	private static final String ID = "com.rec.photoeditor.graphics.commands.GaussianBlurCommand";
	
	private double[][] GaussianBlurConfig = new double[][] { 
			{ 1, 2, 1 }, 
			{ 2, 4, 2 },
			{ 1, 2, 1 } };

	public GaussianBlurCommand() {
	}
	
	public Bitmap process(Bitmap bitmap) {
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(GaussianBlurConfig);
		convMatrix.Factor = 16;
		convMatrix.Offset = 0;
		return ConvolutionMatrix.computeConvolution(bitmap, convMatrix);
	}

	public final String getId() {
		return ID;
	}
}
