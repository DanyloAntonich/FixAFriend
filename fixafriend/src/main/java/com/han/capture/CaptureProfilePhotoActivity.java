package com.han.capture;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.han.login.R;
import com.han.utils.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CaptureProfilePhotoActivity extends AppCompatActivity implements View.OnClickListener{

    private  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator ;

    private Button btnChangeCamera, btnGrid, btnFlash, btnBack, btnCapture;
    private ImageView ivToRoll;
    private LinearLayout cameraPreview;

    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private Context myContext;

    private boolean cameraFront = false;

    private boolean isLightOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_profile_photo);

        initVariables();
        initUI();

    }
    View.OnClickListener switchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //get the number of cameras
            int camerasNumber = Camera.getNumberOfCameras();
            if (camerasNumber > 1) {
                //release the old camera instance
                //switch camera, from the front and the back and vice versa

                releaseCamera();
                chooseCamera();
            } else {
                Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };
    private void initVariables(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;
        isLightOn = false;

    }
    private void  initUI(){
        btnBack = (Button)findViewById(R.id.btn_back4);
        btnBack.setOnClickListener(this);
        btnCapture = (Button)findViewById(R.id.btn_capture);
        btnCapture.setOnClickListener(captrureListener);
        btnChangeCamera = (Button)findViewById(R.id.btn_change_camera);
        btnChangeCamera.setOnClickListener(switchCameraListener);
        btnFlash = (Button)findViewById(R.id.btn_flash);
        btnFlash.setOnClickListener(this);
        btnGrid = (Button)findViewById(R.id.btn_grid_picture);
        btnGrid.setOnClickListener(this);

        ivToRoll = (ImageView)findViewById(R.id.iv_to_roll);
        ivToRoll.setOnClickListener(this);

        cameraPreview = (LinearLayout)findViewById(R.id.camera_preview_1);

        mPreview = new CameraPreview(this, mCamera);

        cameraPreview.addView(mPreview);

    }
    private void flash(){
        if(!CaptureProfilePhotoActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
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

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
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
            mCamera = Camera.open(findBackFacingCamera());

            ///set camera parameters
            DisplayMetrics display = this.getResources().getDisplayMetrics();
            int width = display.widthPixels;
//            mCamera.setDisplayOrientation(90);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.set("orientation", "portrait");
//            parameters.setPictureSize(width, width);
            parameters.setPreviewSize(width, width);
//            mCamera.setParameters(parameters);

            int cameraId = -1;
            if(cameraFront){
                cameraId = findBackFacingCamera();
            }else {
                cameraId = findFrontFacingCamera();
            }

            setCameraDisplayOrientation(CaptureProfilePhotoActivity.this, cameraId, mCamera);
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
        }
    }
    public static void setCameraDisplayOrientation(CaptureProfilePhotoActivity activity,
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
    public void chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                setCameraDisplayOrientation(CaptureProfilePhotoActivity.this, cameraId, mCamera);
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                setCameraDisplayOrientation(CaptureProfilePhotoActivity.this, cameraId, mCamera);
                mPreview.refreshCamera(mCamera);
            }
        }
    }

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
////////////////////////////take photo
    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //make a new picture file
                String fileName = PATH + "user_photo.jpg";
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
                    fos.write(data);
                    fos.flush();
                    fos.close();

                    Toast toast = Toast.makeText(myContext, "Picture saved: " + pictureFile.getName(), Toast.LENGTH_LONG);
                    toast.show();

//                    Intent intent = new Intent(CaptureProfilePhotoActivity.this, CutPhotoActivity.class);
////                    intent.putExtra("url", PATH + "user_photo.jpg");
//                    startActivityForResult(intent, 100);

                    setResult(100);
                    finish();
                } catch (FileNotFoundException e) {
                    Toast.makeText(CaptureProfilePhotoActivity.this, "Picture Failed" + e.toString(),
                            Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                }

                //refresh camera to continue preview
                mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }

    View.OnClickListener captrureListener = new View.OnClickListener() {
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(resultCode){
            case 100:
                setResult(100);
                finish();
                break;
        }



    }
    @Override
    public void onClick(View v) {
        if(v == btnBack){
            setResult(101);
            finish();
        }else if(v == btnFlash){
            flash();
        }else if(v == ivToRoll){

        }else if(v == btnGrid){

        }
    }
}
