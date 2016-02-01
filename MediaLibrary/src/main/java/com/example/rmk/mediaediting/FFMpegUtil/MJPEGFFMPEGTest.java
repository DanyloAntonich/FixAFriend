package com.example.rmk.mediaediting.FFMpegUtil;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.rmk.mediaediting.R;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MJPEGFFMPEGTest extends Activity implements OnClickListener,
									SurfaceHolder.Callback, Camera.PreviewCallback {

	public static final String LOGTAG = "MJPEG_FFMPEG";

	private SurfaceHolder holder;
	private CamcorderProfile camcorderProfile;
	private Camera camera;

	byte[] previewCallbackBuffer;

	boolean recording = false;
	boolean previewRunning = false;

	File jpegFile;
	int fileCount = 0;

	FileOutputStream fos;
	BufferedOutputStream bos;
	Button recordButton;

	Camera.Parameters p;

	NumberFormat fileCountFormatter = new DecimalFormat("00000");
	String formattedFileCount;

	ProcessVideo processVideo;

	String[] libraryAssets = {"ffmpeg",
			"libavcodec.so", "libavcodec.so.52", "libavcodec.so.52.99.1",
			"libavcore.so", "libavcore.so.0", "libavcore.so.0.16.0",
			"libavdevice.so", "libavdevice.so.52", "libavdevice.so.52.2.2",
			"libavfilter.so", "libavfilter.so.1", "libavfilter.so.1.69.0",
			"libavformat.so", "libavformat.so.52", "libavformat.so.52.88.0",
			"libavutil.so", "libavutil.so.50", "libavutil.so.50.34.0",
			"libswscale.so", "libswscale.so.0", "libswscale.so.0.12.0"
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (int i = 0; i < libraryAssets.length; i++) {
			try {
				InputStream ffmpegInputStream = this.getAssets().open(libraryAssets[i]);
		        FileMover fm = new FileMover(ffmpegInputStream,"/data/data/com.mobvcasting.mjpegffmpeg/" + libraryAssets[i]);
		        fm.moveIt();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }

        Process process = null;

        try {
        	String[] args = {"/system/bin/chmod", "755", "/data/data/com.mobvcasting.mjpegffmpeg/ffmpeg"};
        	process = new ProcessBuilder(args).start();
        	try {
				process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	process.destroy();

		} catch (IOException e) {
			e.printStackTrace();
		}

		File savePath = new File(Environment.getExternalStorageDirectory().getPath() + "/com.mobvcasting.mjpegffmpeg/");
		savePath.mkdirs();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		setContentView(R.layout.main);

		recordButton = null;//(Button) this.findViewById(R.id.RecordButton);
		recordButton.setOnClickListener(this);

		camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);

		SurfaceView cameraView = (SurfaceView) findViewById(R.id.image_view);
		holder = cameraView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		cameraView.setClickable(true);
		cameraView.setOnClickListener(this);
    }

	public void onClick(View v) {
		if (recording)
		{
			recording = false;
			Log.v(LOGTAG, "Recording Stopped");

			// Convert to video
			//rmk test mark
			processVideo = new ProcessVideo();
			processVideo.execute();
		}
		else
		{
			recording = true;
			Log.v(LOGTAG, "Recording Started");
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(LOGTAG, "surfaceCreated");

		camera = Camera.open();

		/*
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			previewRunning = true;
		}
		catch (IOException e) {
			Log.e(LOGTAG,e.getMessage());
			e.printStackTrace();
		}
		*/
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.v(LOGTAG, "surfaceChanged");

		if (!recording) {
			if (previewRunning){
				camera.stopPreview();
			}

			try {
				p = camera.getParameters();

				p.setPreviewSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
			    p.setPreviewFrameRate(camcorderProfile.videoFrameRate);
				camera.setParameters(p);

				camera.setPreviewDisplay(holder);

				/*
				Log.v(LOGTAG,"Setting up preview callback buffer");
				previewCallbackBuffer = new byte[(camcorderProfile.videoFrameWidth * camcorderProfile.videoFrameHeight *
													ImageFormat.getBitsPerPixel(p.getPreviewFormat()) / 8)];
				Log.v(LOGTAG,"setPreviewCallbackWithBuffer");
				camera.addCallbackBuffer(previewCallbackBuffer);
				camera.setPreviewCallbackWithBuffer(this);
				*/

				camera.setPreviewCallback(this);

				Log.v(LOGTAG,"startPreview");
				camera.startPreview();
				previewRunning = true;
			}
			catch (IOException e) {
				Log.e(LOGTAG,e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v(LOGTAG, "surfaceDestroyed");
		if (recording) {
			recording = false;

			try {
				bos.flush();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		previewRunning = false;
		camera.release();
		finish();
	}

	public void onPreviewFrame(byte[] b, Camera c) {
		//Log.v(LOGTAG,"onPreviewFrame");
		if (recording) {

			// Assuming ImageFormat.NV21
			if (p.getPreviewFormat() == ImageFormat.NV21) {
				Log.v(LOGTAG,"Started Writing Frame");

				try {
					formattedFileCount = fileCountFormatter.format(fileCount);
					jpegFile = new File(Environment.getExternalStorageDirectory().getPath() + "/com.mobvcasting.mjpegffmpeg/frame_" + formattedFileCount + ".jpg");
					fileCount++;

					fos = new FileOutputStream(jpegFile);
					bos = new BufferedOutputStream(fos);

					YuvImage im = new YuvImage(b, ImageFormat.NV21, p.getPreviewSize().width, p.getPreviewSize().height, null);
					Rect r = new Rect(0,0,p.getPreviewSize().width,p.getPreviewSize().height);
					im.compressToJpeg(r, 9, bos);

					bos.flush();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				Log.v(LOGTAG,"Finished Writing Frame");
			} else {
				Log.v(LOGTAG,"NOT THE RIGHT FORMAT");
			}
		}
	}

    @Override
    public void onConfigurationChanged(Configuration conf)
    {
        super.onConfigurationChanged(conf);
    }

	private class ProcessVideo extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... params) {

	        Process ffmpegProcess = null;
			String folder = Environment.getExternalStorageDirectory().getPath();
			String imageFilePath = folder + "/com.mobvcasting.mjpegffmpeg/image.png";
			String outImageFilePath = folder + "/com.mobvcasting.mjpegffmpeg/outImage.png";

			String videoInputFile = folder + "/com.mobvcasting.mjpegffmpeg/video.mp4";
			String videoResultFile = folder + "/com.mobvcasting.mjpegffmpeg/result.mp4";

			String imageImputFile = folder + "/com.mobvcasting.mjpegffmpeg/test.png";
	        try {

				videoInputFile = folder + "/com.mobvcasting.mjpegffmpeg/videoinput.mp4";
				videoResultFile = folder + "/com.mobvcasting.mjpegffmpeg/videooutput.mp4";

//				videoInputFile = folder + "/com.mobvcasting.mjpegffmpeg/input.mp3";
//				videoResultFile = folder + "/com.mobvcasting.mjpegffmpeg/output.mp3";

//// ffmpeg ok
//				ffmpeg -y -i /sdcard/videokit/in.mp4 -strict experimental -vf transpose=1 -s 160x120 -r 30 -aspect 4:3 -ab 48000 -ac 2 -ar 22050 -b 2097k /sdcard/video_output/out.mp4
//				String[] ffmpegCommand ={"/data/data/com.mobvcasting.mjpegffmpeg/ffmpeg","-y","-i",videoInputFile,"-strict","experimental","-acodec","copy","-ss","00:00:00","-t","00:00:03.000",videoResultFile};
				/// No touch

				String[] ffmpegCommand = {"/data/data/com.mobvcasting.mjpegffmpeg/ffmpeg","-y", "-i", videoInputFile, "-strict", "experimental", "-i", imageImputFile, "-filter_complex", "[0:v][1:v] overlay=25:25:enable='between(t,0,20)'","-acodec", "copy", "-ss", "00:00:00", "-t", "00:00:03.000", videoResultFile};
//				String[] ffmpegCommand ={"/data/data/com.mobvcasting.mjpegffmpeg/ffmpeg","-y","-i",videoInputFile,"-strict","experimental","-acodec","copy","-ss","00:00:00","-t","00:00:03.000",videoResultFile};

				ffmpegProcess = new ProcessBuilder(ffmpegCommand).redirectErrorStream(true).start();
				
				OutputStream ffmpegOutStream = ffmpegProcess.getOutputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(ffmpegProcess.getInputStream()));

				String line;
				
				Log.v(LOGTAG,"***Starting FFMPEG***");
				while ((line = reader.readLine()) != null)
				{
					Log.v(LOGTAG,"***"+line+"***");
				}
				Log.v(LOGTAG,"***Ending FFMPEG***");
	
	    
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
	        
	        if (ffmpegProcess != null) {
	        	ffmpegProcess.destroy();        
	        }
	        
	        return null;
		}
		
	     protected void onPostExecute(Void... result) {
	    	 Toast toast = Toast.makeText(MJPEGFFMPEGTest.this, "Done Processing Video", Toast.LENGTH_LONG);
	    	 toast.show();
	     }
	}
 }