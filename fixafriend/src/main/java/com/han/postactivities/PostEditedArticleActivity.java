package com.han.postactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.han.http.HttpManager;
import com.han.login.R;
import com.han.utils.Constant;
import com.han.utils.Utilities;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class PostEditedArticleActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvBack, tvAddTag, tvPost;
    private EditText etComment;
    private LinearLayout llFacebook, llTwitter, llInstagram, llEmail, llToRoll;

    Context mContext;
    String path;
    ArrayList<String> arrTag;
    String strComment;
    String strInvitationId;
    String strFileType;
    String strTag;

    private ProgressDialog mProgressDialog = null;
    private String responseCode;
    private String responseDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edited_article);

        initVariables();
        initUI();
    }
    private void initVariables(){
        mContext = this;
        path = getIntent().getStringExtra("localpath");
        arrTag = new ArrayList<String>();
        strComment = "";
        strInvitationId = "";
        strTag = "";
    }
    private void initUI(){
        tvBack = (TextView)findViewById(R.id.cancel_button);
        tvAddTag = (TextView)findViewById(R.id.tv_post_add);
        tvPost = (TextView)findViewById(R.id.tv_post_posttofaf);

        etComment = (EditText)findViewById(R.id.et_post_comment);

        llEmail = (LinearLayout)findViewById(R.id.ll_post_email);
        llFacebook = (LinearLayout)findViewById(R.id.ll_post_facebook);
        llInstagram = (LinearLayout)findViewById(R.id.ll_post_instagram);
        llToRoll = (LinearLayout)findViewById(R.id.ll_post_savetoroll);
        llTwitter = (LinearLayout)findViewById(R.id.ll_post_twitter);
    }

    @Override
    public void onClick(View v) {
        if(v == tvBack){
            finish();
        }else if(v == tvAddTag){
            addTag();
        }else if(v == tvPost){
            post();
        }else if(v == llEmail){
            shareToEmai();
        }else if(v == llFacebook){
            shareToFacebook();
        }else if(v == llInstagram){
            shareToInstagram();
        }else if(v == llTwitter){
            shareToTwitter();
        }else if(v == llToRoll){
            saveToRoll();
        }

    }
    private void post(){
        //get comment
        strComment = etComment.getText().toString();
        //get tag
        for (String str : arrTag){
            strTag += (str + ",");
        }
        strTag = strTag.substring(0, strTag.lastIndexOf(",") - 1);
        //get filetype
        String strExtension = path.substring(path.length() - 4, path.length() - 1);
        if(strExtension.equals("jpg")){
            strFileType = "1";
        }else {
            strFileType = "2";
        }
        new ImagePostTask().execute();
    }
    private void shareToFacebook(){

    }
    private void shareToTwitter(){

    }
    private void shareToInstagram(){

    }
    private void shareToEmai(){

    }
    private void saveToRoll(){

    }
    private void addTag(){

    }
    class ImagePostTask extends AsyncTask<String, Integer, String> {

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
            ArrayList<NameValuePair> arrParams = new ArrayList<NameValuePair>();
            arrParams.add(new BasicNameValuePair("id", strInvitationId));
            arrParams.add(new BasicNameValuePair("file_type", strFileType));
            arrParams.add(new BasicNameValuePair("description", strComment));
            arrParams.add(new BasicNameValuePair("tag", strTag));
            JSONObject result = null;
            try {
                result = HttpManager.getInstance().postImage(mContext, Constant.POST_ARTICLE_ID + "/" + strInvitationId, arrParams, path, true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

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
