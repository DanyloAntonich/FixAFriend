package com.example.rmk.mediaediting.videoEditing;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphic_process.DataStorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class VideoRecordAudioActivity extends Activity {

    String THISTAG = "VideoEditorActivity________________________________________________________";
    private String HardHolder = "/data/data/com.mobvcasting.mjpegffmpeg/";

    private VideoView videoView;
    private ImageView recordButton;

    private int drawW, drawH;

    VideoPathSetting videoPathSetting;
    private DataStorage dataStorage;


    ImageView okButton;
    ImageView cancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataStorage = DataStorage.getInstance();
        videoPathSetting = VideoPathSetting.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record_audio);
        initializeComponents();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        updateSizeInfo();
    }
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

    private void initFont(){

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Calibri-Bold.ttf");
        TextView topbar_title = (TextView)findViewById(R.id.topbar_title);
        topbar_title.setTypeface(font);
    }
    private void initializeComponents() {
        dataStorage = DataStorage.getInstance();
        initFont();
        videoView = (VideoView) findViewById(R.id.image_view);
        okButton = (ImageView) findViewById(R.id.ok_button);
//        okButton.setOnClickListener(okButtonListener);
        cancelButton = (ImageView) findViewById(R.id.cancel_button);
//        cancelButton.setOnClickListener(cancelButtonListener);
        recordButton = (ImageView)findViewById(R.id.coverImage);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_for_ffmpeg);
        progressBar.setVisibility(View.INVISIBLE);

        LinearLayout okbuttonLayout = (LinearLayout)findViewById(R.id.ok_button_layout);
        okbuttonLayout.setOnClickListener(okButtonListener);
        LinearLayout cancelButtonLayout = (LinearLayout)findViewById(R.id.cancel_button_layout);
        cancelButtonLayout.setOnClickListener(cancelButtonListener);
        DisplayVideoInVideoView();
    }
    private void DisplayVideoInVideoView(){
//        videoView.stopPlayback();
        videoView.setVideoPath(videoPathSetting.getNewVideoPath());
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
//                videoView.start();
            }
        });
    }


    MediaRecorder mediaRecorder = null;
    String mFileName;
    int textFlagOfRecord = 0;
    public void onRecordAudio(View v) {
        okButton.setImageResource(R.drawable.nextarrow_small_black);
//        okButton.setClickable(false);
//        DisplayVideoInVideoView();
        videoView.start();
        passed_time = System.currentTimeMillis();
        start_clock();
        recordAudio();
        recordButton.setVisibility(View.GONE);
        textFlagOfRecord = 1;
    }

    private int getVideoDuration(){
        MediaPlayer mp = MediaPlayer.create(this, Uri.parse(videoPathSetting.getNewVideoPath()));
        int duration = mp.getDuration();
        mp.release();
        dataStorage.setVideoTimeLength(duration);
        return duration;
    }

    private Timer timer = new Timer();
    private Handler handler = new Handler();
    double current_time, passed_time, delta_time;
    boolean recordAudioPermission = true;
    private void start_clock(){
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        current_time = System.currentTimeMillis();
                        delta_time = (current_time - passed_time);

                        if ((int)(delta_time)  > getVideoDuration() && recordAudioPermission)
                        {
                            recordAudioPermission = false;
                            audioRecordStop((int)delta_time);
                        }

                    }

                });
            }
        }, 0, 100);
    }

    private void audioRecordStop(int delta_time) {
        recordButton.setVisibility(View.VISIBLE);
        videoView.stopPlayback();
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
//        Toast.makeText(getBaseContext(), "Success record audio " +  "Time " + delta_time, Toast.LENGTH_LONG).show();
        textFlagOfRecord = 2;
        timer.cancel();
//        timer = new Timer();
        okButton.setImageResource(R.drawable.nextarrow_small);
        okButton.setClickable(true);
    }
    public void recordAudio()
    {
        // Verify that the device has a mic
        PackageManager pmanager = this.getPackageManager();
        if (!pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            Toast.makeText(this, "This device doesn't have a mic!", Toast.LENGTH_LONG).show();
            return;
        }
        // Start the recording

        mFileName = videoPathSetting.getAudioPath();
        mediaRecorder = new MediaRecorder();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(mFileName);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    Process ffmpegProcess = null;

    private class InsertAudioFrontVideo extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {


            try {
//                add audio on original audio;

//                String[] ffmpegCommand = {HardHolder + "ffmpeg","-y", "-i", videoPathSetting.getNewVideoPath(), "-strict", "experimental",
//                        "-i", videoPathSetting.getAudioPath(), "-strict","experimental",
//                        "-map", "0:0", "-map", "1:0",
//                        "-s", "320x240","-r", "30", "-b:a", "15496k", "-vcodec", "mpeg4","-ab", "48000", "-ac", "2", "-ar", "22050","-shortest", videoPathSetting.getImageAudioPlusVideoPath()};
                String[] ffmpegCommand = {HardHolder + "ffmpeg","-y", "-i", videoPathSetting.getNewVideoPath(), "-strict", "experimental",
                        "-i", videoPathSetting.getAudioPath(), "-strict","experimental",
                        "-map", "0:v", "-map", "1:a",
                        "-r", "30", "-b:a", "15496k", "-vcodec", "mpeg4","-ab", "48000", "-ac", "2", "-ar", "22050","-shortest", videoPathSetting.getImageAudioPlusVideoPath()};
                ffmpegProcess = new ProcessBuilder(ffmpegCommand).redirectErrorStream(true).start();

                OutputStream ffmpegOutStream = ffmpegProcess.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ffmpegProcess.getInputStream()));

                String line;

                Log.v(THISTAG, "***Starting FFMPEG***");
                while ((line = reader.readLine()) != null)
                {
//                    Log.v(THISTAG, "***" + line + "***");
//                    getAllInformationOrNot();

                }
                Log.v(THISTAG, "***Ending FFMPEG***");
//                enterToNextActivity();

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
        timer.cancel();
//        recordButton.setVisibility(View.INVISIBLE);
        DisplayProgressbar();

        videoPathSetting.setAudioMixOrNot(1);
        startActivityForResult(new Intent(VideoRecordAudioActivity.this, VideoPreviewActivity.class), 100);
    }

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

    ProgressBar progressBar = null;
    private void DisplayProgressbar(){
        progressBar = (ProgressBar)findViewById(R.id.progressBar_for_ffmpeg);
        progressBar.setVisibility(View.VISIBLE);
//        progressShowOrNot = progressShowOrNot==0 ? 1:0;
    }




























    protected void onDestroy() {
        Log.i("AudioRecord", "[ACTIVITY] onDestroy");
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
//            saveProgressimageToSDCARD(dataStorage.getCoverImageBitmap());
//
            if (textFlagOfRecord == 2) {
//                Toast.makeText(getBaseContext(), "Please wait a moment ...", Toast.LENGTH_LONG).show();
                DisplayProgressbar();
                InsertAudioFrontVideo processVideo = null;
                processVideo = new InsertAudioFrontVideo();
                processVideo.execute();
                textFlagOfRecord = 0;
            }
            else {
                textFlagOfRecord = 0;
                startActivityForResult(new Intent(VideoRecordAudioActivity.this, VideoPreviewActivity.class), 100);
            }

        }
    };
    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            setResult(RESULT_CANCELED);
            finish();
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_record_audio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
