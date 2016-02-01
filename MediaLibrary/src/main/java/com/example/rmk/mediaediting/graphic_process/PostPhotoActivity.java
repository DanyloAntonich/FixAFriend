package com.example.rmk.mediaediting.graphic_process;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Layout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.utils.APIManager;
import com.example.rmk.mediaediting.utils.Constant;
import com.example.rmk.mediaediting.videoEditing.VideoPathSetting;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

public class PostPhotoActivity extends Activity {



    String THISTAG = "VideoEditorActivity________________________________________________________";
    private String HardHolder = "/data/data/com.mobvcasting.mjpegffmpeg/";

    private ImageView imageView;
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
        setContentView(R.layout.activity_post_photo);
        initializeComponents();
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
        imageView = (ImageView) findViewById(R.id.image_view);
        imageView.setImageBitmap(dataStorage.getBitmap());

        okButton = (ImageView) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        cancelButton = (ImageView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);
        recordButton = (ImageView)findViewById(R.id.coverImage);
        rEditTextComment = (EditText)findViewById(R.id.strComment);
        rEditTextTag = (EditText)findViewById(R.id.strTag);
//        LinearLayout postPhoto = (LinearLayout)findViewById(R.id.post_photo);
        path = dataStorage.getEditedImageFilePath();
        sendEmailShareArticle();
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
            saveProgressimageToSDCARD(dataStorage.getBitmap());
        }
    };
    private View.OnClickListener twitterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sharing("", path, "1");
        }
    };
    private View.OnClickListener facebookListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sharing("", path, "1");
//            shareItem("twitter", path);
        }
    };

    private View.OnClickListener instagramLinstener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sharing("", path, "1");
        }
    };
    private View.OnClickListener emailListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sharing("", path, "1");
//            shareItem("twitter", path);
        }
    };



    private String FolderName = "/DCIM/";
    private void saveProgressimageToSDCARD(Bitmap bitmap){
        File sdCardDirectory = Environment.getExternalStorageDirectory();

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "FixAFriendImage-"+ n +".jpg";

        File image = new File(sdCardDirectory, FolderName + fname);
        boolean success = false;

        // Encode the file as a JPEG image.
        FileOutputStream outStream;
        try {

            outStream = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
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
            Toast.makeText(getBaseContext(), "Saved photo to your camera roll successfully!", Toast.LENGTH_LONG).show();
//            Toast.makeText(getApplicationContext(), "Image saved with success",
//                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Error during image saving", Toast.LENGTH_LONG).show();
        }
    }
    private void sharing(String str, String mediaPath, String fileType){
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        if(fileType.equals("1")){
// Set the MIME type
            share.setType("image/jpeg");

        }else {
            share.setType("video/quicktime");
        }

// Create the URI from the media
        java.io.File media = new java.io.File(mediaPath);
        Uri uri = Uri.fromFile(media);

// Add the URI and the caption to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
//            share.putExtra(Intent.EXTRA_TEXT, caption);

// Broadcast the Intent.
        this.startActivity(Intent.createChooser(share, "Share to"));
    }


















    /// Upload part

    String strInvitationId, strFileType, strComment, strTag = "", path;
    String responseCode, responseDesc;

    public void onPostPhoto(View v){
        strInvitationId = dataStorage.getInvitationId();
        strFileType = "1";

        if (rEditTextComment.getText().toString().equals("")){
            Toast.makeText(getBaseContext(), "Please input your description correctly.", Toast.LENGTH_SHORT).show();
        }else {
            strComment = rEditTextComment.getText().toString();
            strTag = rEditTextTag.getText().toString();
            path = dataStorage.getEditedImageFilePath();

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
                        finish();
                        dialog.cancel();
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
                showOKDialog(PostPhotoActivity.this, "Posted successfully!");


            } else if (resultCode == 1) {
                showOKDialog(PostPhotoActivity.this, "Result is Null");

            } else if (resultCode == 2) {
                showOKDialog(PostPhotoActivity.this, responseDesc);

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
                result = APIManager.getInstance().postImage(getBaseContext(), Constant.POST_ARTICLE_ID + strInvitationId, arrParams, path, true);
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
