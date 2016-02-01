package com.han.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.han.adapter.FollowerAdpater;
import com.han.http.HttpManager;
import com.han.login.R;
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

public class Followers extends AppCompatActivity {
    private TextView btnBack;
    private ListView lvFollowers;
    private PullToRefreshListView mPullRefreshSearchListView;
    private TextView tvBanner;

    private FollowerAdpater mAdapter;
    private ArrayList<UserModel> arrUserModel;

    private Context mContext;
    private ProgressDialog mProgressDialog = null;
    private String responseCode;
    private String responseDesc;
    private int userId = 0;
    private int selectedId = 0;
    private int offset = 0;
    public boolean isLast ;
    private int count;
    private ArrayList<NameValuePair> parameters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        initVariables();
        initUI();
        setFont();
        new FollowerTask().execute();
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
        btnBack = (TextView)findViewById(R.id.btn_follower_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPullRefreshSearchListView = (PullToRefreshListView)findViewById(R.id.lv_followers);

        mPullRefreshSearchListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                if(!isLast){
                    new FollowerTask().execute();
                }
                mPullRefreshSearchListView.onRefreshComplete();
            }
        });
        lvFollowers = mPullRefreshSearchListView.getRefreshableView();
        lvFollowers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Followers.this, ProfileFriendActivity.class);
                intent.putExtra("userid", arrUserModel.get(position - 1).GetId());
                startActivity(intent);

            }
        });
        tvBanner = (TextView)findViewById(R.id.tv_follower_title);

    }
    public void initVariables(){
        arrUserModel = new ArrayList<UserModel>();
        mContext = this;
        count = 0;
        isLast = false;
        offset = 0;
    }
    private void setFont(){

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "Calibri.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "Calibri-Bold.ttf");
        tvBanner.setTypeface(custom_font_bold);

    }
    class FollowerTask extends AsyncTask<String, Integer, String> {

        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(offset > 1){
                mAdapter.notifyDataSetChanged();
                mPullRefreshSearchListView.onRefreshComplete();
            }else{
                if(arrUserModel.size() > 0){
                    SetSearchList();
                }
            }

            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {
            parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("selected_id", String.valueOf(selectedId)));
            parameters.add(new BasicNameValuePair("index", String.valueOf(offset)));
            JSONObject result =  HttpManager.getInstance().callPost(mContext, Constant.FOLLOW + "followers/" + String.valueOf(userId), parameters, true);

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

                        JSONArray jsonArray = result.getJSONArray("users");
                        int length = jsonArray.length();
                        offset ++;
                        if(length < Constant.MAX_USER_AMOUNT_PER_PAGE){

                            isLast = true;
                        }else{

                            isLast = false;
                        }
                        for(int i = 0; i < length; i ++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                            UserModel userModel = new UserModel();
                            userModel.SetId(obj.getString("user_id"));
                            userModel.SetName(obj.getString("name"));
                            userModel.SetPhoto(obj.getString("photo"));
                            userModel.SetEmail(obj.getString("email"));
                            arrUserModel.add(userModel);
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
    public void SetSearchList(){
//        arrUserModel = getInboxItem();
        mAdapter = new FollowerAdpater(mContext, arrUserModel);
        lvFollowers.setAdapter(mAdapter);
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
