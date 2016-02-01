package com.example.rmk.mediaediting;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.rmk.mediaediting.FFMpegUtil.FileMover;
import com.example.rmk.mediaediting.GallerySet.CropGalleryAdapter;
import com.example.rmk.mediaediting.graphic_process.DataStorage;
import com.example.rmk.mediaediting.graphics.ImageProcessor;
import com.example.rmk.mediaediting.graphics.MenuTool;
import com.example.rmk.mediaediting.videoEditing.VideoDrawActivity;
import com.example.rmk.mediaediting.videoEditing.VideoEmoticonActivity;
import com.example.rmk.mediaediting.videoEditing.VideoFrameActivity;
import com.example.rmk.mediaediting.videoEditing.VideoPathSetting;
import com.example.rmk.mediaediting.videoEditing.VideoRecordAudioActivity;
import com.example.rmk.mediaediting.videoEditing.VideoStickerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


public class VideoEditorActivity extends Activity {

    private String[] libraryAssets = {"ffmpeg"};
    //    private String[] libraryAssets = {"ffmpeg",
//            "libavcodec.so", "libavcodec.so.52", "libavcodec.so.52.99.1",
//            "libavcore.so", "libavcore.so.0", "libavcore.so.0.16.0",
//            "libavdevice.so", "libavdevice.so.52", "libavdevice.so.52.2.2",
//            "libavfilter.so", "libavfilter.so.1", "libavfilter.so.1.69.0",
//            "libavformat.so", "libavformat.so.52", "libavformat.so.52.88.0",
//            "libavutil.so", "libavutil.so.50", "libavutil.so.50.34.0",
//            "libswscale.so", "libswscale.so.0", "libswscale.so.0.12.0"
//    };
    VideoPathSetting videoPathSetting;
    ProcessVideo processVideo = null;

    private VideoView videoView;
    private ProgressBar progressBar;
    private ImageProcessor imageProcessor;
    private DataStorage dataStorage;
    private Gallery gallery;
    private ImageView coverImage;
    ///Test bit

    //test bit
    String dwnload_file_path = "http://159.203.64.134//faf//resource//article//j5CssyGTDEHlOxUn.mp4";
    //

    private boolean videoProcessFinishFlag = false;
    private String FFMPEGPATH = Environment.getExternalStorageDirectory().getPath() + "/com.mobvcasting.mjpegffmpeg/";
    String SDCardRootForResultImage = Environment.getExternalStorageDirectory().getPath() + "/com.mobvcasting.mjpegffmpeg/" + "frame%03d.jpg";
    private String FolderName = "/com.mobvcasting.mjpegffmpeg/";
    private String HardHolder = "/data/data/com.mobvcasting.mjpegffmpeg/";

    String THISTAG = "VideoEditorActivity________________________________________________________";
    private Bitmap bgImage;
    private boolean insertImageToVideoSuccess = false;
    RelativeLayout drawRegion;
    private ProgressDialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataStorage = DataStorage.getInstance();
        videoPathSetting = VideoPathSetting.getInstance();
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        if (dataStorage.getVideoEditStart() == 0){
            bgImage = BitmapFactory.decodeResource(getResources(), R.drawable.transparentbackground);
        }


        super.onCreate(savedInstanceState);
        if (!dataStorage.getDownloadOrNot()){
            loadFFMPEGLibrary();
        }

        dataStorage = DataStorage.getInstance();
        String ss = "http://159.203.64.134//faf//resource//article//j5CssyGTDEHlOxUn.mp4";
        if (!dataStorage.getDownloadOrNot()){
//            openVideo(ss);
        }
        setContentView(R.layout.activity_video_editor);
        initializeComponents();
    }

    private void initFont(){

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Calibri-Bold.ttf");
        TextView topbar_title = (TextView)findViewById(R.id.topbar_title);
        topbar_title.setTypeface(font);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        updateSizeInfo();
    }
    int drawW, drawH;
    private void updateSizeInfo() {
        /// Relativelayout region width, height

        RelativeLayout drawRegion = (RelativeLayout)findViewById(R.id.drawWindow);
        drawW = drawRegion.getWidth();
        drawH = drawRegion.getHeight();
//        getImageFromStorage();
        LinearLayout parentWindow = (LinearLayout)findViewById(R.id.parentWindow);

        parentWindow.setPadding((drawW - dataStorage.getVideoWidth()) / 2, (drawH - dataStorage.getVideoHeight()) / 2, (drawW - dataStorage.getVideoWidth()) / 2, (drawH - dataStorage.getVideoHeight()) / 2);
        parentWindow.invalidate();
    }

    private void initializeComponents() {
        dataStorage = DataStorage.getInstance();
        initFont();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screensizeWidth = size.x;
        int screensizeHeight = size.y;
        dataStorage.setScreenWidthAndHeight(screensizeWidth, screensizeHeight);


        drawRegion = (RelativeLayout) findViewById(R.id.drawWindow);
        drawRegion.getLayoutParams().width = dataStorage.getScreenWidth();

        videoView = (VideoView) findViewById(R.id.image_view);
        coverImage = (ImageView)findViewById(R.id.coverImage);

        LinearLayout okButton = (LinearLayout) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        LinearLayout cancelButton = (LinearLayout) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);

        gallery = (Gallery) findViewById(R.id.editor_gallery);
        gallery.setAdapter(new CropGalleryAdapter(this, MenuTool.VideoEffectNames, MenuTool.VideoEffectImageIds));
        gallery.setSpacing(30);
        gallery.setSelection(2);
        gallery.setOnItemClickListener(listener);

        progressBar = (ProgressBar)findViewById(R.id.progressBar_for_ffmpeg);
        progressBar.setVisibility(View.INVISIBLE);
//		Test bit
//        String ss = "http://159.203.64.134//faf//resource//article//KkayAU3VVzkni049.mp4";

//        String DOWNLOAD_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator ;
//        String data = DOWNLOAD_PATH + "vWY9ObxitalIEsKQ.mp4";

//        String data = Environment.getExternalStorageDirectory().getPath() + "/storage/emulated/0/DCIM/" + "vWY9ObxitalIEsKQ.mp4";


        //module combine

        String data = getIntent().getExtras().getString("path");
        String invitationId = getIntent().getExtras().getString("invitationId");
        dataStorage.setInvitationId(invitationId);

        videoPathSetting.setOriginalVideoPath(data);

        downLoadFinishSaveURL();
        DisplayVideoInVideoView();
        coverImagUpdate();
    }
    private void coverImagUpdate(){
        if (dataStorage.getVideoEditStart() != 0) {
            bgImage = dataStorage.getCoverImageBitmap();
        }
        coverImage.setImageBitmap(bgImage);
        coverImage.invalidate();
    }

    private void getImageFromStorage(){
        if (dataStorage.getVideoEditStart() != 0) {
            bgImage = dataStorage.getCoverImageBitmap();
        }
    }



//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        // TODO Auto-generated method stub
//        super.onWindowFocusChanged(hasFocus);
//        updateSizeInfo();
//    }
//    private void updateSizeInfo() {
//        /// Relativelayout region width, height
//        RelativeLayout drawRegion = (RelativeLayout) findViewById(R.id.drawWindow);
//        drawW = drawRegion.getWidth();
//        drawH = drawRegion.getHeight();
//        getImageFromStorage();
//        LinearLayout parentWindow = (LinearLayout)findViewById(R.id.parentWindow);
//        parentWindow.setPadding((drawW - dataStorage.getVideoWidth()) / 2, (drawH - dataStorage.getVideoHeight()) / 2, (drawW - dataStorage.getVideoWidth()) / 2, (drawH - dataStorage.getVideoHeight()) / 2);
//        parentWindow.invalidate();
//    }


    private void DisplayVideoInVideoView(){
        String originalPath = videoPathSetting.getOriginalVideoPath();
        videoView.setVideoPath(originalPath);
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                videoView.start();
            }
        });
    }

    private void downLoadFinishSaveURL(){
        // cover image setting at first time
        bgImage = BitmapFactory.decodeResource(getResources(), R.drawable.transparentbackground);
        bgImage = Bitmap.createScaledBitmap(bgImage, dataStorage.getScreenWidth(), dataStorage.getScreenWidth(), false);
        dataStorage.setVideoWidthAndHeight(dataStorage.getScreenWidth(), dataStorage.getScreenWidth());

        dataStorage.setCoverImageBitmap(bgImage);
        coverImage.setImageBitmap(bgImage);
        ///
        dataStorage.setFirstVideoFilePath(videoPathSetting.getOriginalVideoPath());
        Log.d(THISTAG, "OK");

        getVideoAllInformation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        coverImagUpdate();


//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) drawRegion.getLayoutParams();
// Changes the height and width to the specified *pixels*
//        params.height = 100;
//        params.width = 100;
//        drawRegion.invalidate();

        videoView.invalidate();
    }

    private void getVideoAllInformation(){
        processVideo = new ProcessVideo();
        processVideo.execute();
    }

    private void getAllInformationOrNot(){
        if (videoProcessFinishFlag){
            String thumbNail = Environment.getExternalStorageDirectory().getPath() + "/com.mobvcasting.mjpegffmpeg/" + "frame001.jpg";
            File f = new File(thumbNail);
            Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
            dataStorage.setOriginalVideoWidthAndHeight(bmp.getWidth(), bmp.getHeight());
        }
    }
    private void loadFFMPEGLibrary(){
        for (int i = 0; i < libraryAssets.length; i++) {
            try {
                InputStream ffmpegInputStream = this.getAssets().open(libraryAssets[i]);
                FileMover fm = new FileMover(ffmpegInputStream, HardHolder + libraryAssets[i]);
                fm.moveIt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(THISTAG, "library load finish");
        Process process = null;
        try {
            String[] args = {"/system/bin/chmod", "755", HardHolder + "ffmpeg"};
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
        File savePath = new File(Environment.getExternalStorageDirectory().getPath() + FolderName);
        savePath.mkdirs();
        Log.d(THISTAG, "setting successfully");
    }

    private class ProcessVideo extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {

//            Process ffmpegProcess = null;

            try {

                String[] ffmpegCommand ={HardHolder + "ffmpeg","-y","-i", videoPathSetting.getOriginalVideoPath(),"-strict","experimental", "-r", "1", "-q:v", "2", "-f", "image2", SDCardRootForResultImage};

                ffmpegProcess = new ProcessBuilder(ffmpegCommand).redirectErrorStream(true).start();

                OutputStream ffmpegOutStream = ffmpegProcess.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ffmpegProcess.getInputStream()));

                String line;

                Log.v(THISTAG,"***Starting FFMPEG***");
                while ((line = reader.readLine()) != null)
                {
                    Log.v(THISTAG, "***" + line + "***");
//                    getAllInformationOrNot();
                }
                Log.v(THISTAG,"***Ending FFMPEG***");
                videoProcessFinishFlag = true;
                getAllInformationOrNot();

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (ffmpegProcess != null) {
                ffmpegProcess.destroy();
            }
            return null;
        }

        protected void onPostExecute(Void... result) {
//            Toast toast = Toast.makeText(VideoEditorActivity.this, "Done Processing Video", Toast.LENGTH_LONG);
//            toast.show();
        }
    }

    Process ffmpegProcess = null;
    private class InsertImageFrontVideo extends AsyncTask<Void, Integer, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            try {
                String[] ffmpegCommand = {HardHolder + "ffmpeg","-y", "-i", videoPathSetting.getOriginalVideoPath(), "-strict", "experimental", "-i", videoPathSetting.getProcessImage(), "-filter_complex", "[0:v][1:v] overlay=25:25:enable='between(t,0,20)'","-acodec", "copy", "-ss", "00:00:00", videoPathSetting.getNewVideoPath()};
                ffmpegProcess = new ProcessBuilder(ffmpegCommand).redirectErrorStream(true).start();

                OutputStream ffmpegOutStream = ffmpegProcess.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ffmpegProcess.getInputStream()));

                String line;

                Log.v(THISTAG,"***Starting FFMPEG***");


                while ((line = reader.readLine()) != null) {
                    Log.v(THISTAG, "***" + line + "***");


//                    getAllInformationOrNot();
                }
                Log.v(THISTAG, "***Ending FFMPEG***");


//                startActivityForResult(new Intent(VideoEditorActivity.this, VideoRecordAudioActivity.class), 100);
//
//                insertImageToVideoSuccess = true;

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (ffmpegProcess != null) {
                ffmpegProcess.destroy();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.INVISIBLE);
            enterToNextActivity();
            this.cancel(true);
            super.onPostExecute(aVoid);
        }
    }

    private void enterToNextActivity(){

        ffmpegProcess.destroy();

        startActivityForResult(new Intent(VideoEditorActivity.this, VideoRecordAudioActivity.class), 100);

        insertImageToVideoSuccess = true;
    }

//    Dialog dialog = null;
//
//    private void showProgress(){
//        dialog = new Dialog(VideoEditorActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.myprogressdialog);
//        dialog.setTitle("Download Progress");
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        switch(resultCode){
            case 100:
                setResult(100);
                finish();            // to close this activity
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }







    String SDCardRootOfprocessImage = Environment.getExternalStorageDirectory().getPath() + "/com.mobvcasting.mjpegffmpeg/" + "maskImage.png";

    private void saveProgressimageToSDCARD(Bitmap bitmap){
        File sdCardDirectory = Environment.getExternalStorageDirectory();
        File image = new File(sdCardDirectory, FolderName + "maskImage.png");
        boolean success = false;

        // Encode the file as a PNG image.
        FileOutputStream outStream;
        try {

            outStream = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        /* 100 to keep full quality of the image */

            outStream.flush();
            outStream.close();
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (success) {
//            Toast.makeText(getApplicationContext(), "Image saved with success",
//                    Toast.LENGTH_LONG).show();
        } else {
//            Toast.makeText(getApplicationContext(),
//                    "Error during image saving", Toast.LENGTH_LONG).show();
        }
    }
































    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {
            dataStorage.setFFMpegPath(FFMPEGPATH);
//            Toast.makeText(getBaseContext(),"Processing: " + MenuTool.VideoEffectNames[position], Toast.LENGTH_SHORT).show();
            switch (MenuTool.VideoEffectNames[position]){
                case "Draw":
                    Intent i4 = new Intent(getBaseContext(), VideoDrawActivity.class);
                    startActivity(i4);
                    break;
                case "Frame":
                    Intent intent4 = new Intent(getBaseContext(), VideoFrameActivity.class);
                    startActivity(intent4);
                    break;
                case "Emoticon":
                    Intent intent5 = new Intent(getBaseContext(), VideoEmoticonActivity.class);
                    startActivity(intent5);
                    break;
                case "Sticker":
                    Intent intent6 = new Intent(getBaseContext(), VideoStickerActivity.class);
                    startActivity(intent6);
                    break;
                case "Text":
//                    Intent intent8 = new Intent(getBaseContext(), VideoTextActivity.class);
//                    startActivity(intent8);
                    break;
            }
        }
    };

    private void DisplayProgressbar(){
        progressBar = (ProgressBar)findViewById(R.id.progressBar_for_ffmpeg);
        progressBar.setVisibility(View.VISIBLE);
//        progressShowOrNot = progressShowOrNot==0 ? 1:0;
    }

    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
//            Toast.makeText(getBaseContext(), "Please wait a moment...", Toast.LENGTH_LONG).show();
            ///// Audio codec part
            Bitmap saveBgImage = Bitmap.createScaledBitmap(bgImage, dataStorage.getOriginalVideoWidth(), dataStorage.getOriginalVideoHeight(), false);
            saveProgressimageToSDCARD(saveBgImage);

            DisplayProgressbar();

            InsertImageFrontVideo imageFrontVideo= null;
            imageFrontVideo= new InsertImageFrontVideo();
            imageFrontVideo.execute();
        }
    };
    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            setResult(RESULT_CANCELED);
            dataStorage.setisModified(0);
            finish();
        }
    };
}
