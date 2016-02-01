package com.han.activities;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.rmk.mediaediting.FFMpegUtil.FileMover;
import com.han.login.R;
import com.han.utils.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;


public class VideoViewActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private TextView  forward, back, pause_play;
    private TextView btnBack, increaseTimer, decreaseTimer;
    private SeekBar seekBarUp, seekBarBottom;
    private VideoView videoView;

    private ProgressDialog mProgressDialog;
    Timer timer;
    int currentTime;
    int totalTime ;
    String videoUrl;
    String finalVideoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        loadFFMPEGLibrary();

        initVariables();
        initUI();
        new ProcessVideo().execute();

//        VideoPlay videoPlay = new VideoPlay(this, videoView, videoUrl);
//        videoPlay.playVideo();
    }
    @Override
    protected void onPause() {
        super.onPause();
        ////
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    private String[] libraryAssets = {"ffmpeg"};
    private String FolderName = "/com.mobvcasting.mjpegffmpeg/";
    private String HardHolder = "/data/data/com.mobvcasting.mjpegffmpeg/";
    String THISTAG = "VideoViewActivity________________________________________________________";
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
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Process ffmpegProcess = null;

            try {

                if(!new File(finalVideoUrl).exists()) {
                    //                String[] ffmpegCommand ={HardHolder + "ffmpeg","-y","-i", videoPathSetting.getOriginalVideoPath(),"-strict","experimental", "-vcodec", "copy", videoPathSetting.getNewVideoPath()};
                    String[] ffmpegCommand ={HardHolder + "ffmpeg","-y","-i", videoUrl ,"-strict","experimental", "-vcodec", "copy", finalVideoUrl};

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
                    Log.v(THISTAG, "***Ending FFMPEG***");
                }


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
            super.onPostExecute(aVoid);
            hideProgress();
            playVideo();
            this.cancel(true);
        }


    }
    public synchronized void hideProgress ()
    {
        if (mProgressDialog != null)
        {
            try
            {
                mProgressDialog.dismiss();
            }
            catch (Exception ex)
            {}
            finally
            {
                mProgressDialog = null;
            }

        }
    }

    public void showProgress()
    {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            return;

        mProgressDialog = new ProgressDialog(VideoViewActivity.this, R.style.ProgressDialogTheme);
        mProgressDialog.setCancelable(false);

        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        mProgressDialog.show();
    }

    private void initVariables(){

        totalTime = 0;
        currentTime = 0;
        videoUrl = getIntent().getStringExtra("url");

        finalVideoUrl = Constant.DOWNLOAD_PATH + "/converted/" + videoUrl.substring(videoUrl.lastIndexOf("/") + 1);

        File savePath = new File(Constant.DOWNLOAD_PATH + "/converted");
        if(!savePath.exists()) {
            savePath.mkdirs();
        }

    }
    private void initUI(){

        back = (TextView)findViewById(R.id.back);
        back.setOnClickListener(this);
        pause_play = (TextView)findViewById(R.id.pause);
        pause_play.setOnClickListener(this);
        forward= (TextView)findViewById(R.id.forward);
        forward.setOnClickListener(this);
        btnBack = (TextView)findViewById(R.id.done);
        btnBack.setOnClickListener(this);
        increaseTimer = (TextView)findViewById(R.id.tv_increase_time);
        increaseTimer.setOnClickListener(this);
        decreaseTimer = (TextView)findViewById(R.id.tv_decrease_time);
        decreaseTimer.setOnClickListener(this);

        videoView = (VideoView)findViewById(R.id.videoView);
        seekBarBottom = (SeekBar)findViewById(R.id.seekbar_bottom);
        seekBarUp = (SeekBar)findViewById(R.id.seekbar_up);
        seekBarUp.setOnSeekBarChangeListener(this);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });

    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        increaseTimer.setText("0:" + (int)( progress / 1000));
        decreaseTimer.setText("0:" + (int)((seekBar.getMax() - seekBar.getProgress()) / 1000));

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    private void playVideo(){
        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    VideoViewActivity.this);
            mediacontroller.setAnchorView(videoView);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(finalVideoUrl);
            videoView.setMediaController(mediacontroller);
            videoView.setVideoURI(video);
//            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.asd);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                //////////start play
                videoView.start();
                totalTime = videoView.getDuration();

                seekBarUp.setMax(totalTime);

                ////////////start timer
                timer = new Timer();
                final Handler handler = new Handler();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            public void run() {
//                                relativeLayout.setVisibility(View.INVISIBLE);
                               seekBarUp.setProgress(videoView.getCurrentPosition());
                                if(seekBarUp.getProgress() == totalTime){
                                    timer.cancel();
                                }
                            }
                        });
                    }

                }, 0, 1000);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.back:
//                finish();
//                break;
//            case R.id.pause:
//                break;
//            case R.id.forward:
//                finish();
//                break;
            case R.id.done:
                finish();
                break;
        }
    }
}
