package com.han.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.han.capture.CameraPreview;
import com.han.login.R;
import com.han.postactivities.SendPhotoActivity;
import com.han.utils.Utilities;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CaptureActivity extends AppCompatActivity implements View.OnClickListener{

    private  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator ;

    private ProgressBar progressBar;
    private Button btnChangeCamera;

    private TextView tvPhoto, tvVideo;
    private TextView tvToPhoto, tvToVideo;
    private boolean isLightOn = false;
    private boolean isVideo = false;
    private Button btnLight;
    private Button btnStartCapturePhoto;
    private Button btnStartCaptureVideo;
    private Button btn_refresh, btn_check, backButton;

    private TimerTask timerTask = null;
    Timer timer;
    boolean timerState = false;
    private TableLayout tlPhoto;
    private RelativeLayout rlPhoto, rlVideo;

    /////////////////////////////////////////

    //////////////////////////
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;

    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    //////// for video capture
    private MediaRecorder mediaRecorder;
    boolean recording = false;
    final int[] i = {0};
    List<Camera.Size> videosizes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initTimer();
        initUI();
    }

    private void flash(){
        if(!CaptureActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
            Utilities.showToast(this, "You have no Flash.");
            return;
        }else {
            if(!isLightOn){
                if(mCamera == null){
                    createCamera();
                }
                Camera.Parameters p = mCamera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(p);
                mCamera.startPreview();
                isLightOn = true;
            }else {
                Camera.Parameters params = mCamera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(params);

                isLightOn = false;
            }
        }

    }

    public void chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                if(!isVideo){
                    mPicture = getPictureCallback();///////////////===============*
                }
                setCameraDisplayOrientation(CaptureActivity.this, cameraId, mCamera);
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                if(!isVideo){
                    mPicture = getPictureCallback();///////////////===============*
                }
                setCameraDisplayOrientation(CaptureActivity.this, cameraId, mCamera);
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    private void initTimer(){

        final Handler handler = new Handler();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if(recording){
                            timerState = true;
                            if (i[0] < 600) {
                                progressBar.setProgress(i[0]++);

                            } else {
                                timer.cancel();
                                mediaRecorder.stop();
                                releaseMediaRecorder();
                                recording = false;
                            }
                        }

                    }
                });
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 50);

    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(this)) {
            Toast toast = Toast.makeText(this, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        createCamera();

    }

    private void createCamera(){
        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                btnChangeCamera.setVisibility(View.GONE);
            }
            /////////////////////////////////////////////////////////////////
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                Log.d(TAG, "camera2 selected");
//                this.mCamera = new Camera2(getContext());
//            } else {
////                Log.d(TAG, "camera1 selected");
//                this.mCamera = new Camera(getContext());
//            }
            /////////////////////////////////////////////////////////////////
            mCamera = Camera.open(findBackFacingCamera());
            ////if fail to open camera then do below
            if(mCamera == null){
                Utilities.showOKDialog(this, "Your device does not support camera for this app!");
                return;
            }
            //if success to open camera
            ///set camera parameters
            DisplayMetrics display = this.getResources().getDisplayMetrics();
            int width = display.widthPixels;

//            mCamera.setDisplayOrientation(90);
            Camera.Parameters parameters = mCamera.getParameters();
            /////////set orientation.
            parameters.set("orientation", "portrait");
            parameters.setPreviewSize(width, width);
//            mCamera.setParameters(parameters);
//
            int cameraId = -1;
            if(cameraFront){
                cameraId = findBackFacingCamera();
            }else {
                cameraId = findFrontFacingCamera();
            }

            setCameraDisplayOrientation(CaptureActivity.this, cameraId, mCamera);
            ////////////////////////////////

            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);




        }
    }

    public static void setCameraDisplayOrientation(CaptureActivity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    View.OnClickListener switchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //get the number of cameras
	if (!recording) {
            int camerasNumber = Camera.getNumberOfCameras();
            if (camerasNumber > 1) {
                //release the old camera instance
                //switch camera, from the front and the back and vice versa

                releaseCamera();
                chooseCamera();
            } else {
                Toast toast = Toast.makeText(CaptureActivity.this, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
	}
    };


    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private boolean hasCamera(Context context) {
        //check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }
//////////////////take photo
    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //make a new picture file
//                File pictureFile = getOutputMediaFile();
                String fileName = PATH + "article.jpg";
                File pictureFile = new File(fileName);
                if (!pictureFile.getParentFile().exists()) {
                    pictureFile.getParentFile().mkdir();
                }
                if (pictureFile == null) {
                    return;
                }
                try {
                    //write the file
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bos.write(data);
                    bos.flush();
                    bos.close();
//                    Toast toast = Toast.makeText(CaptureActivity.this, "Picture saved: " + pictureFile.getName(), Toast.LENGTH_LONG);
//                    toast.show();

//                    Bitmap bitmap = BitmapFactory.decodeFile(PATH + "article.jpg");
                    Intent intent = new Intent(CaptureActivity.this, SendPhotoActivity.class);
                    intent.putExtra("url", PATH + "article.jpg");
                    intent.putExtra("isvideo", false);
                    startActivityForResult(intent, 100);

                } catch (FileNotFoundException e) {
                    Toast.makeText(CaptureActivity.this, "Picture Failed" + e.toString(),
                            Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                }
                //refresh camera to continue preview
                mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }

    View.OnClickListener captrurePhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCamera.takePicture(null, null, mPicture);

        }
    };

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.2;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            Log.d("Camera", "Checking size " + size.width + "w " + size.height
                    + "h");
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the
        // requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
//
    //////////////////Video capture//////
    View.OnLongClickListener captrureVideoListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (!recording) {

                if (!prepareMediaRecorder()) {
                    Toast.makeText(CaptureActivity.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                    finish();
                }

                // work on UiThread for better performance
               runOnUiThread(new Runnable() {
                    public void run() {
                        // If there are stories, add them to the table

                        try {
                            recording = true;

                            mediaRecorder.start();

                        } catch (final Exception ex) {
                            // Log.i("---","Exception in thread");
                        }
                    }
                });
            }
        return recording;
        }

    };
    View.OnTouchListener touchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                Log.d("TouchTest", "Touch down");
            } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                if(recording){
                    mediaRecorder.stop(); // stop the recording
                    releaseMediaRecorder(); // release the MediaRecorder object
                    Toast.makeText(CaptureActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
                    recording = false;
                    Log.d("TouchTest", "Touch up");
                }
            }
            return recording;
        }
    };
    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        ///////////set profile of mediarecorder
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
///set output video format
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);//
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setOutputFile(PATH + "article.mp4");
        mediaRecorder.setMaxDuration(300000); // Set max duration 30 sec.
        mediaRecorder.setMaxFileSize(10000000); // Set max file size 10MB
        //rotate video orientation
        mediaRecorder.setOrientationHint(90);

        ////set Video size to be captured
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int screenwidth = displayMetrics.widthPixels;
//        Camera.Size optimalVideoSize = getOptimalPreviewSize(videosizes, screenwidth, screenwidth);
//        int width = optimalVideoSize.width;
//        int height = optimalVideoSize.height;
//        mediaRecorder.setVideoSize(screenwidth, screenwidth);
        try {

            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    private void initUI(){

        tvPhoto = (TextView)findViewById(R.id.tv_to_photo_capture);
        tvVideo = (TextView)findViewById(R.id.tv_to_video_capture);


        btnStartCaptureVideo = (Button) findViewById(R.id.btn_start_capture_video);
//        btnStartCaptureVideo.setOnClickListener(this);
        btnStartCaptureVideo.setOnLongClickListener( captrureVideoListener);
        btnStartCaptureVideo.setOnTouchListener(touchListener);

        tvToPhoto = (TextView)findViewById(R.id.tv_to_photo);
        tvToPhoto.setOnClickListener(this);

        tvToVideo = (TextView)findViewById(R.id.tv_to_video);
        tvToVideo.setOnClickListener(this);

        btnChangeCamera = (Button) findViewById(R.id.btnChangeCamera);
        btnChangeCamera.setOnClickListener(switchCameraListener);

        btnLight = (Button) findViewById(R.id.btnLight);
        btnLight.setOnClickListener(this);

        backButton = (Button) findViewById(R.id.buttonBack);
        backButton.setOnClickListener(this);

        btn_refresh = (Button)findViewById(R.id.btn_refresh_video);
        btn_refresh.setOnClickListener(this);

        btn_check = (Button)findViewById(R.id.btn_check);
        btn_check.setOnClickListener(this);

        btnStartCapturePhoto = (Button) findViewById(R.id.btn_start_capture);
        btnStartCapturePhoto.setOnClickListener(captrurePhotoListener);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        progressBar.setMax(600);

        cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(this, mCamera);

        cameraPreview.addView(mPreview);

    }

    @Override
    public void onClick(View v) {
        if(v == btn_check){
            Intent intent  = new Intent(CaptureActivity.this, SendPhotoActivity.class);
            intent.putExtra("url", PATH + "article.mp4");
            intent.putExtra("isvideo", true);
            startActivity(intent);
        }else if(v == btn_refresh){
//            timer.cancel();
            // stop recording and release camera
//            mediaRecorder.stop(); // stop the recording
//            releaseMediaRecorder(); // release the MediaRecorder object
            recording = false;
            progressBar.setProgress(0);
            i[0] = 0;
        }else if(v == btnLight){
            flash();
        }else if(v == backButton){
            finish();
        }else if(v == tvToPhoto){
            isVideo = false;
            tvToPhoto.setClickable(false);
            tvToVideo.setClickable(true);
            btn_check.setVisibility(View.INVISIBLE);
            btn_refresh.setVisibility(View.INVISIBLE);
            btnStartCaptureVideo.setVisibility(View.GONE);
            btnStartCapturePhoto.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            tvPhoto.setBackgroundColor(getResources().getColor(R.color.white));
            tvVideo.setBackgroundColor(getResources().getColor(R.color.trans_bg));
        }else if(v == tvToVideo){
            isVideo = true;
            tvToPhoto.setClickable(true);
            tvToVideo.setClickable(false);
            btn_check.setVisibility(View.VISIBLE);
            btn_refresh.setVisibility(View.VISIBLE);
            btnStartCaptureVideo.setVisibility(View.VISIBLE);
            btnStartCapturePhoto.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            tvPhoto.setBackgroundColor(getResources().getColor(R.color.trans_bg));
            tvVideo.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){

            finish();
        }
    }

}
