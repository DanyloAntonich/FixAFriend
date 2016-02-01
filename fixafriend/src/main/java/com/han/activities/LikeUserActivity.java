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

import com.han.adapter.LikeUserAdapter;
import com.han.login.R;
import com.han.http.HttpManager;
import com.han.main.CaptureActivity;
import com.han.main.MessageActivity;
import com.han.main.ProfileActivity;
import com.han.main.SearchActivity;
import com.han.model.UserModel;
import com.han.utils.Constant;
import com.han.utils.Utilities;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LikeUserActivity extends AppCompatActivity {
    private TextView btnBack;
    private ListView lvInbox;
    private TextView tvToSearch, tvToMessage, tvToCamera, tvToProfile;
    private TextView tvNoticeFeed, tvNoticeMessage, tvNoticeInbox, tvNoticeRequest;
    private PullToRefreshListView mPullRefreshSearchListView;
    private TextView tvBanner;

    private LikeUserAdapter mAdapter;
    private ArrayList<UserModel> arrUserModel;

    private Context mContext;
    private ProgressDialog mProgressDialog = null;
    private String responseCode;
    private String responseDesc;
    private String articleId = "";
    private int offset = 0;
    private boolean isLast ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_user);

        initVariables();
        initUI();
        setFont();
//        SetSearchList();
        new LikeUserTask().execute();
    }
    private void initUI(){
        btnBack = (TextView)findViewById(R.id.btn_likeusers_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPullRefreshSearchListView = (PullToRefreshListView)findViewById(R.id.lv_like_user);

        mPullRefreshSearchListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                if(!isLast){
                    new LikeUserTask().execute();
                }
                mPullRefreshSearchListView.onRefreshComplete();
            }
        });
        lvInbox = mPullRefreshSearchListView.getRefreshableView();
        lvInbox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LikeUserActivity.this, ProfileFriendActivity.class);
                intent.putExtra("userid", arrUserModel.get(position - 1).GetId());
                startActivity(intent);
            }
        });
        tvNoticeFeed = (TextView)findViewById(R.id.txt_invitation_cnt_likeuser);
        tvNoticeMessage = (TextView)findViewById(R.id.txt_message_cnt_likeuser);
        tvNoticeRequest = (TextView)findViewById(R.id.txt_request_cnt_likeuser);

        tvToCamera = (TextView)findViewById(R.id.likeuser_camera);
        tvToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LikeUserActivity.this, CaptureActivity.class);
                startActivity(intent);
            }
        });
        tvToMessage = (TextView)findViewById(R.id.likeuser_message);
        tvToMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LikeUserActivity.this, MessageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvToProfile = (TextView)findViewById(R.id.likeuser_profile);
        tvToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LikeUserActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvToSearch = (TextView)findViewById(R.id.likeuser_search);
        tvToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LikeUserActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvBanner = (TextView)findViewById(R.id.tv_liked_users);
    }
    @Override
    protected void onPause() {
        super.onPause();
        ////
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
    public void initVariables(){
        arrUserModel = new ArrayList<UserModel>();
        mContext = this;
        Intent intent = getIntent();
        articleId = intent.getStringExtra("article_id");
        offset = 0;
        isLast = false;
    }
    private void setFont(){

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "Calibri.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "Calibri-Bold.ttf");
        tvBanner.setTypeface(custom_font_bold);

    }
    class LikeUserTask extends AsyncTask<String, Integer, String> {

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
            };
            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {

            JSONObject result =  HttpManager.getInstance().callGet(mContext, Constant.ARTICLE_INDEX + "/" + articleId + "/" + String.valueOf(offset) + "/likes", null, true);

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

                        JSONArray jsonArray = result.getJSONArray("likes");

                        int n = jsonArray.length();
                        offset ++;
                        if(n < Constant.MAX_FEED_ARTICLE_PER_PAGE){
                            isLast = true;
                        }else {
                            isLast = false;
                        }
                        for(int i = 0; i < n; i ++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                            UserModel userModel = new UserModel();
                            userModel.SetPhoto(obj.getString("photo"));
                            userModel.SetId(obj.getString("user_id"));
                            userModel.SetName(obj.getString("name"));
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
        mAdapter = new LikeUserAdapter(mContext, arrUserModel);
        lvInbox.setAdapter(mAdapter);
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
    ///for test////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ArrayList<UserModel> getInboxItem(){

        ArrayList<UserModel> afm = new ArrayList<UserModel>();
        for(int i = 0; i < 5; i ++){
            UserModel um = new UserModel();
            um.SetName("Nice");
            um.SetMessage("Hello!!!");

            afm.add(um);
        }
        return afm;
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_like_user, menu);
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
