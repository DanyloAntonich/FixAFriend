package com.example.rmk.mediaediting.videoEditing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphic_process.DataStorage;
import com.example.rmk.mediaediting.graphic_process.PostPhotoActivity;
import com.example.rmk.mediaediting.utils.APIManager;
import com.example.rmk.mediaediting.utils.Constant;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class PostVideoWindowActivity extends Activity {

    String THISTAG = "VideoEditorActivity________________________________________________________";
    private String HardHolder = "/data/data/com.mobvcasting.mjpegffmpeg/";

    private VideoView videoView;
    private ImageView recordButton;
    private EditText rEditTextComment, rEditTextTag;

    VideoPathSetting videoPathSetting;
    private DataStorage dataStorage;


    ImageView okButton;
    ImageView cancelButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post_video_window);

        initializeComponents();
    }

    @Override
    protected void onResume() {

        super.onResume();

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

        ////////////////////
        path = videoPathSetting.getImageAudioPlusVideoPath();
        //////////////////
        videoView = (VideoView) findViewById(R.id.image_view);
        if (videoPathSetting.getAudioMixOrNot() == 1) {
            videoView.setVideoPath(videoPathSetting.getImageAudioPlusVideoPath());
//            path = videoPathSetting.getImageAudioPlusVideoPath();
        }
        if (videoPathSetting.getAudioMixOrNot() == 0){
            videoView.setVideoPath(videoPathSetting.getNewVideoPath());
//            path = videoPathSetting.getNewVideoPath();
            renameFile();
        }
/////////////////////////////
        okButton = (ImageView) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        cancelButton = (ImageView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);
        recordButton = (ImageView)findViewById(R.id.coverImage);
        rEditTextComment = (EditText)findViewById(R.id.strComment);
        rEditTextTag = (EditText)findViewById(R.id.strTag);
        sendEmailShareArticle();
    }
    private void renameFile() {
        String currentFileName = "lastVideoFile.mp4";
//        currentFileName = currentFileName.substring(1);
        Log.i("Current file name", currentFileName);

        File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/com.mobvcasting.mjpegffmpeg/");
        File from      = new File(directory, currentFileName);
        File to        = new File(directory, "article.mp4");
//        if (to.exists()) {
//            to.delete();
//        }
        from.renameTo(to);
        Log.i("Directory is", directory.toString());
//        Log.i("Default path is", videoURI.toString());
        Log.i("From path is", from.toString());
        Log.i("To path is", to.toString());
    }

    private void DisplayVideoInVideoView(){
//        videoView.stopPlayback();
        videoView.setVideoPath(videoPathSetting.getImageAudioPlusVideoPath());
//        if (videoPathSetting.getAudioMixOrNot() == 1) {
//            videoView.setVideoPath(videoPathSetting.getImageAudioPlusVideoPath());
//        }
//        if (videoPathSetting.getAudioMixOrNot() == 0){
//            videoView.setVideoPath(videoPathSetting.getNewVideoPath());
//        }
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
    public void onRecordAudio(View v) {
        DisplayVideoInVideoView();
        videoView.start();
        recordButton.setVisibility(View.GONE);
        recordButton.setClickable(false);
    }

    private void sendEmailShareArticle() {
        LinearLayout facebook = (LinearLayout)findViewById(R.id.facebook_layout);
        facebook.setOnClickListener(facebookListener);
        LinearLayout twitter = (LinearLayout)findViewById(R.id.twitter_layout);
        twitter.setOnClickListener(twitterListener);
        LinearLayout instagram = (LinearLayout)findViewById(R.id.instagram_layout);
        instagram.setOnClickListener(instagramLinstener);
        LinearLayout email = (LinearLayout)findViewById(R.id.email_layout);
        email.setOnClickListener(emailListener);
        LinearLayout savetoroll = (LinearLayout)findViewById(R.id.save_toRoll_layout);
        savetoroll.setOnClickListener(savetorollListener);

    }
    private View.OnClickListener savetorollListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getBaseContext(), "Saved video to your camera roll successfully!", Toast.LENGTH_LONG).show();
        }
    };



    private View.OnClickListener twitterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sharing("", path, "2");
        }
    };
    private View.OnClickListener facebookListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sharing("", path, "2");
        }
    };

    private View.OnClickListener instagramLinstener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sharing("", path, "2");
        }
    };
    private View.OnClickListener emailListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sharing("", path, "2");
        }
    };
    private void sharing(String str, String mediaPath, String fileType){
        Intent share = new Intent(Intent.ACTION_SEND);
        if(fileType.equals("1")){
            share.setType("image/jpeg");

        }else {
            share.setType("video/quicktime");
        }
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share to"));
    }




    /// Upload part

    String strInvitationId, strFileType, strComment, path;
    String strTag = "";
    String responseCode, responseDesc;

    public void onPostVideo(View v){
        strInvitationId = dataStorage.getInvitationId();
        strFileType = "2";
        if (rEditTextComment.getText().toString().equals("") || rEditTextTag.getText().toString().equals("")){
            Toast.makeText(getBaseContext(), "Please input your description and tag correctly.", Toast.LENGTH_SHORT).show();
        }else {
            strComment = rEditTextComment.getText().toString();
            strTag = rEditTextTag.getText().toString();
            path = videoPathSetting.getImageAudioPlusVideoPath();
            new ImagePostTask().execute();
        }
    }
    public void showOKDialog(Context context, String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Fix-A-Friend");
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setResult(100);
                        dialog.cancel();
                        finish();

                    }
                });
//        builder1.setNegativeButton("No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    class ImagePostTask extends AsyncTask<String, Integer, String> {
        int resultCode = -1;
        protected void onPreExecute() {
//            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            if (resultCode == 0) {
                showOKDialog(PostVideoWindowActivity.this, "Posted successfully!");


            } else if (resultCode == 1) {
                showOKDialog(PostVideoWindowActivity.this, "Result is Null");

            } else if (resultCode == 2) {
                showOKDialog(PostVideoWindowActivity.this, responseDesc);

            }

            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {
            ArrayList<NameValuePair> arrParams = new ArrayList<NameValuePair>();
            arrParams.add(new BasicNameValuePair("id", strInvitationId));
            arrParams.add(new BasicNameValuePair("file_type", strFileType));
            arrParams.add(new BasicNameValuePair("description", strComment));
            arrParams.add(new BasicNameValuePair("tag", strTag));
            JSONObject result = null;
            try {
                result = APIManager.getInstance().postImage(PostVideoWindowActivity.this, Constant.POST_ARTICLE_ID + strInvitationId, arrParams, path, true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (result == null) {
                resultCode = 1;
            }else
            {
                try
                {
                    if (!result.isNull("success")) {
                        responseCode = result.getString("success");

                    }
                    if (!result.isNull("error")) {
                        responseDesc = result.getString("error");
                    }
                    if (responseCode.equalsIgnoreCase("true")) {

                        resultCode = 0;

                    } else {
                        resultCode = 2;
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            return null;
        }
    }












    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        public void onClick(View v) {




        }
    };
    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

            setResult(RESULT_CANCELED);
            finish();
        }
    };












































}
