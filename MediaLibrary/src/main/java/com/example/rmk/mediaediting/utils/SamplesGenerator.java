package com.example.rmk.mediaediting.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.rmk.mediaediting.graphics.CommandsPreset;
import com.example.rmk.mediaediting.graphics.ImageProcessor;
import com.example.rmk.mediaediting.graphics.ImageProcessorListener;
import com.example.rmk.mediaediting.graphics.commands.ImageProcessingCommand;

import java.util.ArrayList;

public class SamplesGenerator implements ImageProcessorListener {
	ImageProcessor imgproc;
	ArrayList<ImageProcessingCommand> cp;
	
	int counter;
	private Context context;
	
	public SamplesGenerator(Context context) {
		imgproc = ImageProcessor.getInstance();		
		cp = CommandsPreset.Preset;
		counter = 0;
		this.context = context;
	}
	
	public void generate(){
		imgproc.setProcessListener(this);	
		runNext();
	}

	private void runNext() {
		ImageProcessingCommand command = cp.get(counter);
		imgproc.runCommand(command);
	}

	public void onProcessStart() {
		// processing startef
		Log.i("Sample Generator", "Processing Started: " + counter);
	}

	public void onProcessEnd(Bitmap result) {
		Log.i("Sample Generator", "Processing END: " + counter);
		saveBitmap(result);
		counter++;
		if (counter < cp.size()){
			runNext();
		}
	}

	private void saveBitmap(Bitmap bitmap) {
		SaveToStorageUtil.save(bitmap, "Sample_"+counter+".jpg", context);
	}
	
}
