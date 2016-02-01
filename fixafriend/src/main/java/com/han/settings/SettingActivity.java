package com.han.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.han.http.HttpManager;
import com.han.login.R;
import com.han.login.LoginAcitivity;
import com.han.login.PolicyActivity;
import com.han.main.CaptureActivity;
import com.han.main.FeedViewActivity;
import com.han.main.MessageActivity;
import com.han.main.SearchActivity;
import com.han.utils.Constant;
import com.han.utils.Utilities;

import org.json.JSONObject;

import java.io.File;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView btnBack;
    private RelativeLayout rlSuggestion, rlShareApp, rlPolicy, rlRateUs, rlChangePassword, rlRemove, rlLogout;
    private TextView tvToFeed, tvToCamera, tvToSearch, tvToMessage;
    private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8;

    private Context mContext;
    private ProgressDialog mProgressDialog = null;
    private String responseCode;
    private String responseDesc;

    private SharedPreferences objSharedPref;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initVariable();
        initViewItems();
        setFont();
    }
    private void initVariable(){
        mContext = this;
        objSharedPref = Utilities.getSharedPreferences(mContext);
    }
    private void initViewItems(){
        btnBack = (TextView)findViewById(R.id.btn_setting_back);
        btnBack.setOnClickListener(this);
        rlSuggestion = (RelativeLayout)findViewById(R.id.ll_suggestion);
        rlSuggestion.setOnClickListener(this);
        rlShareApp = (RelativeLayout)findViewById(R.id.ll_shareapp);
        rlShareApp.setOnClickListener(this);
        rlPolicy = (RelativeLayout)findViewById(R.id.ll_policy);
        rlPolicy.setOnClickListener(this);
        rlRateUs = (RelativeLayout)findViewById(R.id.ll_rateus);
        rlRateUs.setOnClickListener(this);
        rlChangePassword = (RelativeLayout)findViewById(R.id.ll_change_password);
        rlChangePassword.setOnClickListener(this);
        rlRemove = (RelativeLayout)findViewById(R.id.ll_remove_file);
        rlRemove.setOnClickListener(this);
        rlLogout = (RelativeLayout)findViewById(R.id.ll_logout);
        rlLogout.setOnClickListener(this);

        tvToCamera = (TextView)findViewById(R.id.setting_camera);
        tvToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, CaptureActivity.class);
                startActivity(intent);
            }
        });
        tvToFeed = (TextView)findViewById(R.id.setting_feed);
        tvToFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, FeedViewActivity.class);
                startActivity(intent);
            }
        });
        tvToSearch = (TextView)findViewById(R.id.setting_search);
        tvToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        tvToMessage = (TextView)findViewById(R.id.setting_message);
        tvToMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, MessageActivity.class);
                startActivity(intent);
            }
        });
        tv1 = (TextView)findViewById(R.id.tv_setting_title);
        tv2 = (TextView)findViewById(R.id.tv_setting_suggestion);
        tv3 = (TextView)findViewById(R.id.tv_setting_rateus);
        tv4 = (TextView)findViewById(R.id.tv_setting_removefile);
        tv5 = (TextView)findViewById(R.id.tv_setting_change);
        tv6 = (TextView)findViewById(R.id.tv_setting_logout);
        tv7 = (TextView)findViewById(R.id.tv_setting_shareapp);
        tv8 = (TextView)findViewById(R.id.tv_setting_policy);
    }
    private void setFont(){

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "Calibri.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "Calibri-Bold.ttf");
        tv1.setTypeface(custom_font_bold);
        tv2.setTypeface(custom_font);
        tv3.setTypeface(custom_font);
        tv4.setTypeface(custom_font);
        tv5.setTypeface(custom_font);
        tv6.setTypeface(custom_font);
        tv7.setTypeface(custom_font);
        tv8.setTypeface(custom_font);
    }
    @Override
    public void onClick(View v) {
        if (v.equals(rlSuggestion)) {
            String[] recipients = {"fafuserservices@gmail.com"};
            String subject = "Suggestion / Feedback:";
            sendEmail(recipients, subject);
        } else if (v.equals(rlShareApp)) {
            AlertDialog.Builder conductor = new AlertDialog.Builder(SettingActivity.this);
            conductor.setTitle("Invite Friends " +
                    "Send invitation via");

            int resId = getResources().getIdentifier("share",
                    "array", getPackageName());
            conductor.setItems(resId, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int index) {
                    int resId1 = getResources().getIdentifier("share", "array",
                            getPackageName());
                    String subject = getResources().getStringArray(resId1)[index];
                    shareAppBy(subject);
                }
            });
            AlertDialog alert = conductor.create();
            alert.show();
        } else if (v.equals(rlChangePassword)) {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        } else if (v.equals(rlLogout)) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Are you sure to log out right now?");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            logout();

                        }
                    });
            builder1.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

        } else if (v.equals(rlPolicy)) {
            Intent intent = new Intent(this, PolicyActivity.class);
            startActivity(intent);
        } else if (v.equals(rlRateUs)) {

        } else if (v.equals(rlRemove)) {

            showDeleteDialog(mContext, "Do you want delete temporary files?");
        } else if (v.equals(btnBack)) {
            finish();

        } else {

        }
    }

    private void sendEmail(String[] recipients, String subject){
        Intent intent = new Intent(Intent.ACTION_SEND);
        if(intent == null){
            Utilities.showToast(mContext, "Not Available to send mail.");
        }else{
//            String[] recipients = {"fafuserservices@gmail.com"};
            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_CC, getDeviceName());
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent, "Send mail"));
        }

    }
    private void shareAppBy(String subject){
        if(subject.equals("Email")){
            sharing();
        }else if(subject.equals("Facebook")){
            sharing();
        }else if(subject.equals("Message")){
            sharing();
        }else if(subject.equals("Twitter")){
            sharing();
        }else if(subject.equals("Cancel")){

        }
    }
    private void sharing(){
        String shareBody = "Fix-A-Friend is a great photo & video sharing and editing app." +
                "Please download from Google Play Store and enjoy!" +
                " https://play.google.com/store/apps/details?id=************************";

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Fix-A-Friend");

        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    /** Returns the consumer friendly device name */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }
    private void logout(){

        new LogoutTask().execute();
        Intent logout = new Intent(SettingActivity.this, LoginAcitivity.class);
        startActivity(logout);
        setResult(100);

        //initialize sharedPreference
        SharedPreferences.Editor edit = objSharedPref.edit();

        edit.putString("access-token",  null);

        edit.putString("longitude", null);
        edit.putString("latitude", null);
        edit.putString("user_id", null);
        edit.putString("fb_id", null);
        edit.putString("user_name", null);
        edit.putString("user_photo", null);
        edit.putString("password", null);
        edit.putString("email", null);
        edit.putString("login_mode", null);

        edit.putString("fb_access_token", null);
        edit.putString("fb_id", null);
        edit.putString("fb_name", null);
        edit.putString("fb_photo", null);
        edit.putString("fb_email", null);
        edit.putString("fb_gender", null);

        edit.commit();
        finish();
    }
    public void showDeleteDialog(Context context, String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Fix-A-Friend");
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        delete();
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    private void delete(){
        File f = new File(Constant.DOWNLOAD_PATH);
        File file[] = f.listFiles();
        for(File df : file){
            df.delete();

        }
        File f1 = new File(Constant.DOWNLOAD_PATH + "/converted");
        File file1[] = f.listFiles();
        for(File df : file){
            df.delete();

        }

        Utilities.showToast(this, "Removed Temporary files successfully!");
//        List<File> getListFiles(File Constant.DOWNLOAD_PATH) {
//            ArrayList<File> inFiles = new ArrayList<File>();
//            File[] files = parentDir.listFiles();
//            for (File file : files) {
//                if (file.isDirectory()) {
//                    inFiles.addAll(getListFiles(file));
//                } else {
//                    inFiles.add(file);
//                }
//            }
////            return inFiles;
//        }
    }
    class LogoutTask extends AsyncTask<String, Integer, String> {

        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();

            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {
            String url = Constant.SEARCH;

            JSONObject result =  HttpManager.getInstance().callPost(mContext, Constant.LOG_OUT, null, true);

            if (result == null) {

                Utilities.showToast(mContext, "Plase try again...");
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

                        Utilities.showToast(SettingActivity.this, "Log out success");
//                        JSONObject obj = result.getJSONObject("users");
//                        userModel = new UserModel();
//                        userModel.SetEmail(strEmail);
//                        userModel.SetPassword(strPassword);
//                        userModel.SetLoginMode("email");
//                        userModel.SetAboutMe(obj.getString("about_me"));
//                        userModel.SetPhoto(obj.getString("photo"));
//                        userModel.SetUsername(obj.getString("name"));
//                        userModel.SetId(obj.getString("id"));
//                        userModel.SetPurchased(obj.getString("purchased"));
//                        userModel.SetAccessToken(obj.getString("access_token"));


                    } else {
//                        Utils.showAlert(mContext, responseDesc, false);
                        Utilities.showToast(mContext, responseDesc);
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


}
