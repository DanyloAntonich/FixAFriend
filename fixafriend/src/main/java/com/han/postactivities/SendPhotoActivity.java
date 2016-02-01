package com.han.postactivities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.han.http.HttpManager;
import com.han.login.R;
import com.han.utils.Constant;
import com.han.utils.MakeThumbnailWithURL;
import com.han.utils.Utilities;
import com.han.utils.VideoPlay;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SendPhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()  ;

    private Button btnBack, btnSend;
    private RelativeLayout rlTimeLimite;
    private ImageView imageView;
    private VideoView videoView;
    private TextView tvTimelimit;
    private ImageView ivIsvideo;

    private Bitmap bitmap;
    private String url;
    private String timelimit;
    private boolean isVideo;

    private Context mContext;
    private ProgressDialog mProgressDialog = null;
    private String responseCode;
    private String responseDesc;
    private String strAlert;

    private static int RESLUT_CODE = 100;
    DisplayMetrics  display ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_photo);

        initVarilables();
        initUI();

        showMedia();


    }
    private void initUI(){
        btnBack = (Button)findViewById(R.id.btn_back_sendphoto);
        btnBack.setOnClickListener(this);
        btnSend = (Button)findViewById(R.id.btn_send_photo);
        btnSend.setOnClickListener(this);
        ivIsvideo = (ImageView)findViewById(R.id.iv_isvideo14);
        ivIsvideo.setOnClickListener(this);
        registerForContextMenu(btnSend);
        rlTimeLimite = (RelativeLayout)findViewById(R.id.rl_time_limit);
        rlTimeLimite.setOnClickListener(this);
        tvTimelimit = (TextView)findViewById(R.id.tv_timelimit);
        imageView = (ImageView)findViewById(R.id.iv_send_photo);
        videoView = (VideoView)findViewById(R.id.vv_send_video);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ivIsvideo.setVisibility(View.VISIBLE);
            }
        });

    }
    private void initVarilables(){
        url = getIntent().getStringExtra("url");
        isVideo = getIntent().getBooleanExtra("isvideo", false);
        timelimit = "";
        strAlert = "";
        mContext = this;
        display = mContext.getResources().getDisplayMetrics();
    }
    private void showMedia(){
        imageView.setVisibility(View.VISIBLE);
        if(isVideo){
            /////////show thumbnail of video
//            videoView.setVisibility(View.VISIBLE);
            ivIsvideo.setVisibility(View.VISIBLE);
            MakeThumbnailWithURL makeThumbnailWithURL = new MakeThumbnailWithURL();
            Bitmap bitmap =  makeThumbnailWithURL.makeThumbnail(url);
            Bitmap scaleBitmap =  Bitmap.createScaledBitmap(bitmap, display.widthPixels, display.widthPixels, true);
//            Bitmap rotatedBitmap = rotateImage(scaleBitmap, 90);
            imageView.setImageBitmap(scaleBitmap);

        }else {

            File imgFile = new File(url);

            if(imgFile.exists()){

                //get BitmapFactory option
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                //get bitmap with local path
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),bmOptions);
//                Bitmap bitmap = BitmapFactory.decodeFile(url);
                // scale bitmap with screen size
                bitmap = Bitmap.createScaledBitmap(bitmap, display.widthPixels, display.widthPixels,true);
                //rotate bitmap
                Bitmap rotatedBitmap = rotateImage(bitmap, 90);
                ///store new bitmap
                try {

                    File myDir = new File(PATH);
                    if(!myDir.exists()){
                        myDir.mkdirs();
                    }

                    File file = new File(myDir, url.substring(url.lastIndexOf("/") + 1));
//                    Log.i(TAG, "" + file);
                    if (file.exists())
                        file.delete();
                    FileOutputStream out = new FileOutputStream(file);
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //set bitmap on imageview
                imageView.setImageBitmap(rotatedBitmap);
            }

            /*ExifInterface ei = null;
            try {
                ei = new ExifInterface(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotateImage(bitmap, 180);
                    break;
                // etc.
            }*/




//            bitmap = BitmapFactory.decodeFile(url);
//
//            imageView.setImageBitmap(bitmap);
        }
    }
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Please choose mode");
        menu.add(0, v.getId(), 0, "Random");
        menu.add(0, v.getId(), 0, "Choose friend");
        menu.add(0, v.getId(), 0, "Cancel");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle()=="Random"){
            uploadRandom();
        }
        else if(item.getTitle()=="Choose friend")
        {
            Intent intent = new Intent(SendPhotoActivity.this, ChooseFriendActivity.class);
            startActivityForResult(intent, 100);
        }
        else {
            return false;
        }
        return true;
    }
    private void uploadRandom(){
        new ImagePostTask().execute();
    }

    @Override
    public void onClick(View v) {
        if(v == btnBack){
            finish();
        }else if(v == btnSend){
            if(timelimit.equals("")){
                AlertDialog.Builder conductor = new AlertDialog.Builder(SendPhotoActivity.this);
                conductor.setTitle("Please choose time limit");
                AlertDialog alert = conductor.create();
                alert.show();
            }else{


                AlertDialog.Builder conductor = new AlertDialog.Builder(SendPhotoActivity.this);
                conductor.setTitle("Please choose mode");

                int resId = getResources().getIdentifier("choose_mode",
                        "array", getPackageName());
                conductor.setItems(resId, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int index) {
                        int resId1 = getResources().getIdentifier("choose_mode", "array",
                                getPackageName());
                        String strLimit = getResources().getStringArray(resId1)[index];

                        if (strLimit.equals("Random")) {
                            new ImagePostTask().execute();
                        } else if (strLimit.equals("Choose friend")) {
                            Intent intent = new Intent(SendPhotoActivity.this, ChooseFriendActivity.class);
                            intent.putExtra("timelimit", timelimit);
                            intent.putExtra("url", url);
                            startActivityForResult(intent, RESLUT_CODE);
                        }
                    }
                });


                AlertDialog alert = conductor.create();
                alert.show();

            }
        }else if(v == rlTimeLimite){

            AlertDialog.Builder conductor = new AlertDialog.Builder(SendPhotoActivity.this);
            conductor.setTitle("Please choose time limit");

            int resId = getResources().getIdentifier("time_limit",
                    "array", getPackageName());
            conductor.setItems(resId, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int index) {
                    int resId1 = getResources().getIdentifier("time_limit", "array",
                            getPackageName());
                    String strLimit = getResources().getStringArray(resId1)[index];
                    tvTimelimit.setText(strLimit);
                    timelimit = getTimelimit(strLimit);
                }
            });
            AlertDialog alert = conductor.create();
            alert.show();
        }else if(v == ivIsvideo){
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            ivIsvideo.setVisibility(View.GONE);
            VideoPlay videoPlay = new VideoPlay(SendPhotoActivity.this, videoView, url);
            videoPlay.playVideo();

        }
    }
    private String getTimelimit(String strlimit){
        int limit = 0;
        if (strlimit.equals("15 min")){
            limit = 15 * 60;
        }else if (strlimit.equals("30 min")){
            limit = 30 * 60;
        }else if (strlimit.equals("One hour")){
            limit = 60 * 60;
        }else if (strlimit.equals("6 hour")){
            limit = 6 * 60 * 60;
        }else if (strlimit.equals("12 hour")){
            limit = 12 * 60 * 60;
        }else if (strlimit.equals("24 hour")){
            limit = 24 * 60 * 60;
        }

        return String.valueOf(limit);
    }
    class ImagePostTask extends AsyncTask<String, Integer, String> {

        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(resultCode == 0){
                finishActivity();
            }else if (resultCode == 1) {
                Utilities.showToast(mContext, responseDesc);
            }

            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {
            String filetype = "";
            if(isVideo){
                filetype = "2";
                strAlert = "Video sent successfully";
            }else {
                filetype = "1";
                strAlert = "Photo sent successfully";
            }
            ArrayList<NameValuePair > arrParams = new ArrayList<NameValuePair>();
            arrParams.add(new BasicNameValuePair("receiver_email", ""));
            arrParams.add(new BasicNameValuePair("file_type", filetype));
            arrParams.add(new BasicNameValuePair("limit", timelimit));
            arrParams.add(new BasicNameValuePair("request_id", "-1"));
            JSONObject result = null;
            try {
                result = HttpManager.getInstance().postImage(mContext, Constant.SEND_ARTICLE, arrParams, url, true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (result == null) {
                resultCode = 2;
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
                        resultCode = 1;
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            return null;
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

        mProgressDialog = new ProgressDialog(mContext, R.style.ProgressDialogTheme);
        mProgressDialog.setCancelable(false);

        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        mProgressDialog.show();
    }
    private void finishActivity(){
        setResult(100);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){

            finishActivity();
        }
    }
}
