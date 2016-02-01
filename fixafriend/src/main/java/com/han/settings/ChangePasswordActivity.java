package com.han.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.han.login.R;
import com.han.http.HttpManager;
import com.han.main.CaptureActivity;
import com.han.main.FeedViewActivity;
import com.han.main.MessageActivity;
import com.han.main.SearchActivity;
import com.han.utils.Constant;
import com.han.utils.Utilities;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private Button  btnSubmit;
    private EditText etOldPassword, etNewPassword;
    private TextView tvToFeed, tvToCamera, tvToSearch, tvToMessage, tvBanner, btnBack;

    private ArrayList<NameValuePair> loginParam;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private String responseCode;
    private String responseDesc;
    private SharedPreferences objSharedPref;

    private String strOldPassword;
    private String strNewPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initVariables();
        initViewItems();
        setFont();
    }
    private void initVariables(){
        mContext = this;
        objSharedPref = Utilities.getSharedPreferences(mContext);
    }
    private void initViewItems(){
        btnBack = (TextView)findViewById(R.id.btn_back5);
        btnBack.setOnClickListener(this);
        btnSubmit = (Button)findViewById(R.id.btn_submit_new);
        btnSubmit.setOnClickListener(this);
        etOldPassword = (EditText)findViewById(R.id.et_old_password);
        etNewPassword = (EditText)findViewById(R.id.et_new_password);

        tvToCamera = (TextView)findViewById(R.id.change_password_camera);
        tvToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePasswordActivity.this, CaptureActivity.class);
                startActivity(intent);
            }
        });
        tvToFeed = (TextView)findViewById(R.id.change_password_feed);
        tvToFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePasswordActivity.this, FeedViewActivity.class);
                startActivity(intent);
            }
        });
        tvToSearch = (TextView)findViewById(R.id.change_password_search);
        tvToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePasswordActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        tvToMessage = (TextView)findViewById(R.id.change_password_message);
        tvToMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePasswordActivity.this, MessageActivity.class);
                startActivity(intent);
            }
        });
        tvBanner =(TextView)findViewById(R.id.tv_change_title);
    }
    private void setFont(){

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "Calibri.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "Calibri-Bold.ttf");
        tvBanner.setTypeface(custom_font_bold);
        etOldPassword.setTypeface(custom_font);
        etNewPassword.setTypeface(custom_font);
        btnSubmit.setTypeface(custom_font);

    }

    @Override
    public void onClick(View v) {
        if(v == this.btnBack){
            finish();
        }else if (v == this.btnSubmit) {
            strOldPassword = etOldPassword.getText().toString();
            strNewPassword = etNewPassword.getText().toString();

            if (TextUtils.isEmpty(strOldPassword)) {
                Utilities.showOKDialog(mContext, "Please input old password!");
                return;

            }else if(!strOldPassword.equals(objSharedPref.getString("password", ""))) {
                Utilities.showOKDialog(mContext, "Please input current password correctly!");
            }else if (TextUtils.isEmpty(strNewPassword)) {
                Utilities.showOKDialog(mContext, "Please input new password!");
                return;

            } else {
                new ChangePasswordTask().execute();
            }

        }
    }
    class ChangePasswordTask extends AsyncTask<String, Integer, String> {

        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(resultCode == 0){
                Utilities.showOKDialog(mContext, "Changed password successfully!");
            }else if(resultCode == 1){
                Utilities.showOKDialog(mContext,responseDesc);
            }

            this.cancel(true);
            super.onPostExecute(result);
        }


        @Override
        protected String doInBackground(String... param) {

            loginParam = new ArrayList<NameValuePair>();
            loginParam.add(new BasicNameValuePair("old_password", strOldPassword));
            loginParam.add(new BasicNameValuePair("new_password", strNewPassword));

            JSONObject result =  HttpManager.getInstance().callPost(mContext, Constant.CHANG_PASSWORD, loginParam, true);

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

}
