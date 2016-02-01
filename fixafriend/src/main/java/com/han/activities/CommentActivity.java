package com.han.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.han.ImageDownloadHelper.UrlImageViewCallback;
import com.han.ImageDownloadHelper.UrlImageViewHelper;
import com.han.adapter.CommentAdapter;
import com.han.http.HttpManager;
import com.han.login.R;
import com.han.model.CommentModel;
import com.han.model.UserModel;
import com.han.utils.Constant;
import com.han.utils.Utilities;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {
    private Button  btnRefresh;
    private ListView lvComment;
    private PullToRefreshListView mPullRefreshSearchListView;
    private EditText etMessage;
    private TextView btnBack,tvSend, tvBanner;
    private ImageView ivUser;

    private CommentAdapter mAdapter;
    private ArrayList<UserModel> arrUserModel;
    ArrayList<CommentModel> arrCM;

    private Context mContext;
    private ProgressDialog mProgressDialog = null;
    private ArrayList<NameValuePair> parameters;
    private String responseCode;
    private String responseDesc;
    private String articleId = "";
    private String goodValue = "";
    private String photoUrl = "";
    private int offset = 0;
    public boolean isLast ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initVariables();
        initUI();
        setFont();
//        SetChatList();
        new GetCommentTask().execute();
    }
    @Override
    protected void onPause() {
        super.onPause();
        ////
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
    private void initUI(){

        btnBack = (TextView)findViewById(R.id.btn_comment_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPullRefreshSearchListView = (PullToRefreshListView)findViewById(R.id.lv_comment);
        mPullRefreshSearchListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                if (!isLast) {
                    new GetCommentTask().execute();
                }
                mPullRefreshSearchListView.onRefreshComplete();

            }
        });
        lvComment = mPullRefreshSearchListView.getRefreshableView();

        etMessage = (EditText)findViewById(R.id.et_comment_message);
        tvSend = (TextView)findViewById(R.id.tv_comment_send);
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodValue = etMessage.getText().toString();
                if(goodValue.equals("")){
                    Utilities.showOKDialog(mContext, "Please input comments!");

                }else {
                    new AddCommentTask().execute();
                }


            }
        });
        tvBanner = (TextView)findViewById(R.id.tv_comment_title);
        ivUser = (ImageView)findViewById(R.id.iv_comment);

        if(!photoUrl.equals("")){
            UrlImageViewHelper.setUrlDrawable(ivUser, photoUrl, R.drawable.loading, new UrlImageViewCallback() {
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
    public void initVariables(){
        arrUserModel = new ArrayList<UserModel>();
        mContext = this;
        Intent intent = getIntent();
        articleId = intent.getStringExtra("article_id");
        SharedPreferences preferences = Utilities.getSharedPreferences(mContext);
        photoUrl = preferences.getString("user_photo", "");
//        photoUrl = intent.getStringExtra("photourl");
        isLast = false;
        offset = 0;

    }
    private void setFont(){

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "Calibri.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "Calibri-Bold.ttf");
        tvBanner.setTypeface(custom_font_bold);
        etMessage.setTypeface(custom_font);
    }
    class GetCommentTask extends AsyncTask<String, Integer, String> {
        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(resultCode == 0){
                if(offset > 1){
                    mAdapter.notifyDataSetChanged();
                    mPullRefreshSearchListView.onRefreshComplete();
                }else {
                    SetChatList();
                }
            }else if(resultCode == 1){
                Utilities.showOKDialog(mContext, Constant.ALERT1);
            }


            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {

            parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("id", articleId));
            parameters.add(new BasicNameValuePair("index", String.valueOf(offset)));

            JSONObject result =  HttpManager.getInstance().callPost(mContext, Constant.COMMENT + articleId + Constant.GET_COMMENT, parameters, true);

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
                        JSONArray arrComment = result.getJSONArray("comments");
                        arrCM = new ArrayList<CommentModel>();
                        int n = arrCM.size();
                        offset ++ ;
                        if(n < Constant.MAX_USER_AMOUNT_PER_PAGE){
                            isLast = true;
                        }
                        for(int j = 0; j < arrComment.length(); j ++){
                            CommentModel cm = new CommentModel();
                            JSONObject objComment = arrComment.getJSONObject(j);
                            cm.SetComment(objComment.getString("comment"));
                            cm.SetPhotoURL(objComment.getString("photo"));
                            cm.SetUserId(objComment.getString("user_id"));
                            cm.SetName(objComment.getString("name"));
                            cm.SetTime(objComment.getInt("created_date"));
                            arrCM.add(cm);
                            resultCode = 0;
                        }


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
    class AddCommentTask extends AsyncTask<String, Integer, String> {

        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(resultCode == 0){
                etMessage.setText("");

                mAdapter.notifyDataSetChanged();
            }else if(resultCode == 1){
                Utilities.showOKDialog(mContext, Constant.ALERT1);
            }else if(resultCode == 2){
                Utilities.showOKDialog(mContext, responseDesc);
            }

            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {

            parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("id", articleId));
            parameters.add(new BasicNameValuePair("comment", goodValue));

            JSONObject result =  HttpManager.getInstance().callPost(mContext, Constant.COMMENT + articleId + Constant.ADD_COMMENT, parameters, true);

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

                        CommentModel cm = new CommentModel();
                        SharedPreferences preferences = Utilities.getSharedPreferences(mContext);

                        cm.SetPhotoURL(photoUrl);
                        cm.SetName(preferences.getString("user_name", ""));
                        cm.SetTime(0);
                        cm.SetComment(goodValue);
                        cm.SetPhotoURL(preferences.getString("user_photo", ""));

                        arrCM.add(cm);
                        resultCode = 0;
                    } else {
                        resultCode = 2;
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
    public void SetChatList(){
//        arrUserModel = getChatItem();
        mAdapter = new CommentAdapter(mContext, arrCM);
        lvComment.setAdapter(mAdapter);
        lvComment.setSelection(arrCM.size() - 1);
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
