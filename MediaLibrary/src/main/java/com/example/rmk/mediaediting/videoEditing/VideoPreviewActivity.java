package com.example.rmk.mediaediting.videoEditing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphic_process.DataStorage;

public class VideoPreviewActivity extends Activity {
    String THISTAG = "VideoEditorActivity________________________________________________________";
    private String HardHolder = "/data/data/com.mobvcasting.mjpegffmpeg/";

    private VideoView videoView;
    private ImageView recordButton;

    VideoPathSetting videoPathSetting;
    private DataStorage dataStorage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        initializeComponents();
    }

    int drawW, drawH;
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
        dataStorage = DataStorage.getInstance();
        videoPathSetting = VideoPathSetting.getInstance();

        videoView = (VideoView) findViewById(R.id.image_view);
        LinearLayout okButton = (LinearLayout) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        LinearLayout cancelButton = (LinearLayout) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);
        recordButton = (ImageView)findViewById(R.id.coverImage);
        recordButton.setOnClickListener(videoPreviewButtonListener);
        DisplayVideoInVideoView();
    }

    private View.OnClickListener videoPreviewButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            videoView.start();
            recordButton.setVisibility(View.GONE);
        }
    };
    private void DisplayVideoInVideoView(){
        videoView.stopPlayback();
        if (videoPathSetting.getAudioMixOrNot() == 1) {
            videoView.setVideoPath(videoPathSetting.getImageAudioPlusVideoPath());
        }
        if (videoPathSetting.getAudioMixOrNot() == 0){
            videoView.setVideoPath(videoPathSetting.getNewVideoPath());
        }

        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    public void onRecordAudio(View v) {
        DisplayVideoInVideoView();
        videoView.start();
        recordButton.setVisibility(View.GONE);
//        recordButton.setClickable(false);
    }





    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(VideoPreviewActivity.this, PostVideoWindowActivity.class);
            startActivityForResult(intent, 100);
        }
    };
    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            setResult(RESULT_CANCELED);
            finish();
        }
    };
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


}
