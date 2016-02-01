package com.han.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.han.http.HttpManager;
import com.han.utils.Constant;
import com.han.utils.Utilities;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPassword extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSubmit;
    private TextView tvBanner, btnBack;

    private ArrayList<NameValuePair> loginParam;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private String responseCode;
    private String responseDesc;

    private String strFullname, strEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initVariables();
        initUI();
        setFont();
    }
    private void initVariables(){
        mContext = this;
    }
    private void initUI(){
        etEmail = (EditText)findViewById(R.id.et_email);

        btnSubmit = (Button)findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail = etEmail.getText().toString();
                if(TextUtils.isEmpty(strEmail)){
                    Utilities.showToast(mContext, "Please input email address!");
                    return;
                }
                if(!isEmailValid(strEmail)){
                    Utilities.showToast(mContext, "Please input valid email address!");
                    return;
                }else{
                    new ForgotPasswordTask().execute();
                }

            }
        });
        btnBack = (TextView)findViewById(R.id.button2);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        tvBanner = (TextView)findViewById(R.id.tv_forgot_banner);
    }
    private void setFont(){

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "Calibri.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "Calibri-Bold.ttf");
        btnSubmit.setTypeface(custom_font);
        etEmail.setTypeface(custom_font);
        tvBanner.setTypeface(custom_font_bold);

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
    class ForgotPasswordTask extends AsyncTask<String, Integer, String> {
        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(resultCode == 1){
                Utilities.showToast(mContext, "'" + strEmail + "' " + responseDesc);
            }else if(resultCode == 2){
                Utilities.showToast(mContext, "Access denied!");
            }else if ((resultCode == 0)){
//                Utilitie.showToast(mContext, "Submitted Successfully!");
                Utilities.showOKDialog(mContext, "Submitted succesfully!");
            }
            this.cancel(true);

            super.onPostExecute(result);
        }


        @Override
        protected String doInBackground(String... param) {

            loginParam = new ArrayList<NameValuePair>();

            loginParam.add(new BasicNameValuePair("email", strEmail));


            JSONObject result =  HttpManager.getInstance().callPost(mContext, Constant.FORGOT_PASSWORD, loginParam, false);

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
                        resultCode = 1;
                    }
                    if (responseCode.equalsIgnoreCase("true")) {

                        resultCode = 0;
                    } else {
//                        Utils.showAlert(mContext, responseDesc, false);
//                        Utilitie.showToast(mContext, responseDesc);
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
//        getMenuInflater().inflate(R.menu.menu_forgot_password, menu);
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
