package com.han.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.han.ImageDownloadHelper.UrlImageViewCallback;
import com.han.ImageDownloadHelper.UrlImageViewHelper;
import com.han.capture.CaptureProfilePhotoActivity;
import com.han.http.HttpManager;
import com.han.login.R;
import com.han.main.CaptureActivity;
import com.han.main.FeedViewActivity;
import com.han.main.MessageActivity;
import com.han.main.SearchActivity;
import com.han.utils.Constant;
import com.han.utils.GlobalVariable;
import com.han.utils.Utilities;
import com.han.widget.CircularImageView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + "user_photo.jpg";

    private TextView  btnCheck;
    private EditText etEmail, etUsername, etPassword, etAboutMe;
    private TextView btnBack,tvToFeed, tvToSearch, tvToMessage, tvToCamera, tvAboutme, tvBanner;
    private CircularImageView ivUser;
    private TextView tvNoticeInvite, tvNoticeMessage, tvNoticeRequest;
    private ArrayList<NameValuePair> loginParam;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private String responseCode;
    private String responseDesc;

    private String strEmail;
    private String strName;
    private String strAboutMe;
    private String strPassword;
    private String strPhotoUrl;
    private String finalPhotoPath;

    private String strNewPhotoPath;

    private boolean isImageSet = false;
    DisplayMetrics display ;

    GlobalVariable globalVariable ;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initVariables();
        initViewItems();
        setFont();

        ////show notification badge
        if(globalVariable.getValue(preferences, Constant.N_INVITATION) > 0){
            tvNoticeInvite.setVisibility(View.VISIBLE);
            tvNoticeInvite.setText(String.valueOf(globalVariable.getValue(preferences, Constant.N_INVITATION)));

        }
        if(globalVariable.getValue(preferences, Constant.N_MESSAGE) > 0){
            tvNoticeMessage.setVisibility(View.VISIBLE);
            tvNoticeMessage.setText(String.valueOf(globalVariable.getValue(preferences, Constant.N_MESSAGE)));
        }
        if(globalVariable.getValue(preferences, Constant.N_REQUEST) > 0){
            tvNoticeRequest.setVisibility(View.VISIBLE);
            tvNoticeRequest.setText(String.valueOf(globalVariable.getValue(preferences, Constant.N_REQUEST)));
        }else {
        }

    }
    private void initVariables(){
        mContext = this;
        finalPhotoPath = "";
        globalVariable = new GlobalVariable();
        preferences = Utilities.getSharedPreferences(mContext);
        strAboutMe = getIntent().getStringExtra("aboutme");
        strEmail = getIntent().getStringExtra("email");
        strName = getIntent().getStringExtra("username");
        strPassword = getIntent().getStringExtra("password");
        strPhotoUrl = getIntent().getStringExtra("photourl");
        display = mContext.getResources().getDisplayMetrics();

//        if(!strPhotoUrl.equals("")){
//            finalPhotoPath = Constant.DOWNLOAD_PATH + "user_photo.jpg";
//            MakeThumbnailWithURL makeThumbnailWithURL = new MakeThumbnailWithURL();
//            try {
//                Bitmap bi = makeThumbnailWithURL.DownloadFromUrl(strPhotoUrl, "user_photo.jpg");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
    private void initViewItems(){

        tvNoticeInvite = (TextView)findViewById(R.id.txt_invitation_cnt_edit_profile);
        tvNoticeMessage = (TextView)findViewById(R.id.txt_message_cnt_edit_profile);
        tvNoticeRequest = (TextView)findViewById(R.id.txt_request_cnt_edit_profile);

        ivUser = (CircularImageView)findViewById(R.id.iv_edit_profile);
        ivUser.setOnClickListener(this);
        btnBack = (TextView)findViewById(R.id.btn_edit_profile_back);
        btnBack.setOnClickListener(this);
        btnCheck = (TextView)findViewById(R.id.btn_edit_check_profile);
        btnCheck.setOnClickListener(this);

        etEmail = (EditText)findViewById(R.id.et_edit_email);
        etUsername = (EditText)findViewById(R.id.et_edit_username);
        etPassword = (EditText)findViewById(R.id.et_edit_password);
        etAboutMe = (EditText)findViewById(R.id.et_edit_aboutme);



        tvToCamera = (TextView)findViewById(R.id.edit_profile_camera);
        tvToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, CaptureActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvToFeed = (TextView)findViewById(R.id.edit_profile_feed);
        tvToFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, FeedViewActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvToSearch = (TextView)findViewById(R.id.edit_profile_search);
        tvToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvToMessage = (TextView)findViewById(R.id.edit_profile_message);
        tvToMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, MessageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvAboutme = (TextView)findViewById(R.id.tv_aboutme);
        tvBanner = (TextView)findViewById(R.id.textView9);

        etAboutMe.setText(strAboutMe);
        etEmail.setText(strEmail);
        etUsername.setText(strName);
        etPassword.setText(strPassword);
        if(strPhotoUrl.equals("")){
            ivUser.setBackgroundDrawable(getResources().getDrawable(R.drawable.user_web));
        }else {
            UrlImageViewHelper.setUrlDrawable(ivUser, strPhotoUrl, R.drawable.loading, new UrlImageViewCallback() {
                @Override
                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                    if (!loadedFromCache) {
                        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
                        scale.setDuration(300);
                        scale.setInterpolator(new OvershootInterpolator());
                        imageView.startAnimation(scale);
                    }
                }
            });
        }

    }
    private void setFont(){

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "Calibri.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "Calibri-Bold.ttf");
        etEmail.setTypeface(custom_font);
        etUsername.setTypeface(custom_font);
        etPassword.setTypeface(custom_font);
        etAboutMe.setTypeface(custom_font);
        tvBanner.setTypeface(custom_font_bold);
        tvAboutme.setTypeface(custom_font);
    }

    @Override
    public void onClick(View v) {
        if(v == btnCheck){
            strEmail = etEmail.getText().toString();
            strPassword = etPassword.getText().toString();
            strName = etUsername.getText().toString();
            strAboutMe = etAboutMe.getText().toString();

            strName = strName.replace(" ", "_");
            if(finalPhotoPath.equals("")){
                Utilities.showOKDialog(mContext, "Please choose your photo!");
                return;
            }
            if(strAboutMe.equals("")){
                Utilities.showOKDialog(mContext, "Please write something about you!");
                return;
            }else {
                new updateTask().execute();
            }


        }else if(v == btnBack){
            finish();
        }else if (v == ivUser){
            Intent intent = new Intent(EditProfileActivity.this, CaptureProfilePhotoActivity.class);
            startActivityForResult(intent, 100);
        }
    }

    private void setUserImageFromLocal(){

        File imgFile = new File(finalPhotoPath);

        if(imgFile.exists()){
            //get screen size

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

                File myDir = new File(Constant.CAMERA_PATH);
                if(!myDir.exists()){
                    myDir.mkdirs();
                }

                File file = new File(myDir, finalPhotoPath.substring(finalPhotoPath.lastIndexOf("/") + 1));
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
            ivUser.setImageBitmap(rotatedBitmap);
        }


//        Bitmap bitmap = BitmapFactory.decodeFile(PATH);
//        ivUser.setImageBitmap(bitmap);
    }
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(resultCode){
            case 100:
                finalPhotoPath = PATH;
                setUserImageFromLocal();
                break;
        }



    }

    class updateTask extends AsyncTask<String, Integer, String> {
        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(resultCode == 0){
                setResult(100);
                finish();
            }else if(resultCode == 1){
                Utilities.showOKDialog(mContext, responseDesc);
            }
            this.cancel(true);
            super.onPostExecute(result);
        }


        @Override
        protected String doInBackground(String... param) {

            loginParam = new ArrayList<NameValuePair>();
            loginParam.add(new BasicNameValuePair("email", strEmail));
            loginParam.add(new BasicNameValuePair("name", strName));
            loginParam.add(new BasicNameValuePair("password", strPassword));
            loginParam.add(new BasicNameValuePair("about_me", strAboutMe));

            JSONObject result = null;
            try {
                result = HttpManager.getInstance().uploadProfileImage(mContext, Constant.SAVE, loginParam, finalPhotoPath, true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (result == null) {
                resultCode = 2;
//                Utilitie.showToast(mContext, "Plase try again...");
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
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
