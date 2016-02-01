package com.han.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.han.http.HttpManager;
import com.han.model.UserModel;
import com.han.utils.Constant;
import com.han.utils.Utilities;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupAcitivity extends Activity implements View.OnClickListener{

    private EditText etFullname, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister ;
    private TextView tvPolicy, tvBanner,btnBack;

    private ArrayList<NameValuePair> loginParam;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private String responseCode;
    private String responseDesc;

    private String strFullname, strEmail;
    private String strPassword, strConfirm;
    private UserModel userModel;

    private String regId = "";

    private SharedPreferences objSharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_acitivity);

        initVariables();
        initUI();
        setFont();
    }
    private void initVariables(){
        mContext = this;
        objSharedPref = Utilities.getSharedPreferences(mContext);
        regId =objSharedPref.getString("PROPERTY_REG_ID", "");
    }
    private void initUI(){
        etFullname = (EditText)findViewById(R.id.et_fullname);
        etEmail = (EditText)findViewById(R.id.et_login);
        etPassword = (EditText)findViewById(R.id.et_password);
        etConfirmPassword = (EditText)findViewById(R.id.et_confirm_password);

        btnRegister = (Button)findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
        btnBack = (TextView)findViewById(R.id.btn_back1);
        btnBack.setOnClickListener(this);
        tvPolicy = (TextView)findViewById(R.id.tv_policy);
        tvPolicy.setOnClickListener(this);
        tvBanner = (TextView)findViewById(R.id.tv_signup_banner);
    }
    private void setFont(){
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "Calibri.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "Calibri-Bold.ttf");
        etFullname.setTypeface(custom_font);
        etEmail.setTypeface(custom_font);
        etPassword.setTypeface(custom_font);
        etConfirmPassword.setTypeface(custom_font);
        btnRegister.setTypeface(custom_font_bold);
        btnBack.setTypeface(custom_font);
        tvPolicy.setTypeface(custom_font);
        tvBanner.setTypeface(custom_font_bold);
    }
    @Override
    public void onClick(View v) {
        if(v == btnBack){
            finish();
        }else if(v == btnRegister){
            strFullname = etFullname.getText().toString();
            strEmail = etEmail.getText().toString();
            strPassword = etPassword.getText().toString();
            strConfirm = etConfirmPassword.getText().toString();
            if(TextUtils.isEmpty(strFullname)){
                Utilities.showToast(mContext, "Please input name!");
                return;
            }else if(TextUtils.isEmpty(strEmail)){
                Utilities.showToast(mContext, "Please input email address!");
                return;
            }else if(!isEmailValid(strEmail)){
                Utilities.showToast(mContext, "Please input valid email address!");
                return;
            }else if(TextUtils.isEmpty(strPassword)){
                Utilities.showToast(mContext, "Please input password!");
                return;
            }else if (TextUtils.isEmpty(strConfirm) || !strConfirm.equals(strPassword)) {
                Utilities.showToast(mContext, "Please comfirm password!");
                return;

            } else if(strConfirm.equals(strPassword)){
                new SignupTask().execute();
            }
        }else if(v == tvPolicy){
            final Intent intent = new Intent(SignupAcitivity.this, PolicyActivity.class);
            startActivity(intent);
        }
    }
    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
    class SignupTask extends AsyncTask<String, Integer, String> {

        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(resultCode == 1){
                Utilities.showToast(mContext, "'" + strEmail + "' " + responseDesc);
            }else if(resultCode == 0){
                finish();
            }

            this.cancel(true);

            super.onPostExecute(result);
        }


        @Override
        protected String doInBackground(String... param) {

            loginParam = new ArrayList<NameValuePair>();
            loginParam.add(new BasicNameValuePair("name", strFullname));
            loginParam.add(new BasicNameValuePair("email", strEmail));
            loginParam.add(new BasicNameValuePair("password", strPassword));
            loginParam.add(new BasicNameValuePair("device_id", regId));
            JSONObject result =  HttpManager.getInstance().callPost(mContext, Constant.SIGN_UP, loginParam, false);

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
                        JSONObject obj = result.getJSONObject("userinfo");

                        SharedPreferences.Editor edit = objSharedPref.edit();



                        edit.putString("longitude", String.valueOf(Constant.longitude));
                        edit.putString("latitude", String.valueOf(Constant.latitude));

                        edit.putString("user_id", obj.getString("id"));
                        edit.putString("user_name", obj.getString("name"));
                        edit.putString("user_photo", obj.getString("photo"));
                        edit.putString("password", strPassword);
                        edit.putString("email", obj.getString("email"));
                        edit.putString("access-token",  obj.getString("access_token"));
                        edit.putString("fb_id", obj.getString("fb_id"));
                        edit.putString("isRegistered", obj.getString("isRegistered"));
                        edit.putString("deviceType", obj.getString("deviceType"));
                        edit.putString("status", obj.getString("status"));
                        edit.putString("purchased", obj.getString("purchased"));
//                        edit.putBoolean(Utils.ISREMEMBER, isAgreeTerms);
                        edit.commit();
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
//        getMenuInflater().inflate(R.menu.menu_signup_acitivity, menu);
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
