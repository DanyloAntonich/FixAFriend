package com.han.activities;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.han.adapter.ActivityAdapter;
import com.han.http.HttpManager;
import com.han.login.R;
import com.han.main.CaptureActivity;
import com.han.main.FeedViewActivity;
import com.han.main.MessageActivity;
import com.han.main.SearchActivity;
import com.han.model.UserModel;
import com.han.utils.Constant;
import com.han.utils.GlobalVariable;
import com.han.utils.Utilities;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class Activity extends AppCompatActivity {

    private  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator ;

    private TextView btnBack;
    private ListView lvActivity;
    private PullToRefreshListView mPullRefreshSearchListView;
    private TextView tvToFeed, tvToMessage, tvToCamera, tvToSearch, tvBanner;
    private TextView tvNoticeFeed, tvNoticeMessage, tvNoticeInbox, tvNoticeRequest;
    private ActivityAdapter mAdapter;
    private ArrayList<UserModel> arrUserModel;

    private Context mContext;
    private ArrayList<NameValuePair> parameters;
    private ProgressDialog mProgressDialog = null;
    private String responseCode;
    private String responseDesc;
    private int offset = 0;
    public boolean isLast ;
    private int count;
    DownloadManager manager ;
    GlobalVariable globalVariable  ;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_);

        initVariables();
        initViewItems();
        setFont();


        new ActivityTask().execute();


        if(globalVariable.getValue(preferences, Constant.N_INVITATION) > 0){
            tvNoticeFeed.setVisibility(View.VISIBLE);
            tvNoticeFeed.setText(String.valueOf(globalVariable.getValue(preferences, Constant.N_INVITATION)));

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
    @Override
    protected void onPause() {
        super.onPause();
        ////
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
    public void initViewItems(){

        tvNoticeFeed = (TextView)findViewById(R.id.badge_activity_invitation);
        tvNoticeMessage = (TextView)findViewById(R.id.badge_activity_message);
        tvNoticeRequest = (TextView)findViewById(R.id.badge_activity_request);

        btnBack = (TextView)findViewById(R.id.btn_activity_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPullRefreshSearchListView = (PullToRefreshListView)findViewById(R.id.lv_activity);

        mPullRefreshSearchListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                if(!isLast){
                    new ActivityTask().execute();
                }
                mPullRefreshSearchListView.onRefreshComplete();

            }
        });
        lvActivity = mPullRefreshSearchListView.getRefreshableView();
        lvActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(arrUserModel.get(position - 1).GetMessage().contains("request")){
                    showRequestDialog(mContext, arrUserModel.get(position).GetName(), arrUserModel.get(position).GetArticleId());
                }else {
                    final Intent intent = new Intent(Activity.this, ProfileFriendActivity.class);
                    intent.putExtra("userid", arrUserModel.get(position - 1).GetId());
                    startActivity(intent);
                }

            }
        });
        tvToCamera = (TextView)findViewById(R.id.activity_camera);
        tvToSearch = (TextView)findViewById(R.id.activity_search);
        tvToMessage = (TextView)findViewById(R.id.activity_message);
        tvToFeed = (TextView)findViewById(R.id.activity_feed);

        tvToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity.this, CaptureActivity.class);
                startActivity(intent);
            }
        });

        tvToFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity.this, FeedViewActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tvToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tvToMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity.this, MessageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvBanner = (TextView)findViewById(R.id.tv_activity_title);

    }
    public void initVariables(){
        arrUserModel = new ArrayList<UserModel>();
        mContext = this;
        count = 0;
        isLast = false;
        offset = 0;
        globalVariable = new GlobalVariable();
        preferences = Utilities.getSharedPreferences(this);
        manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
    }
    private void setFont(){

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "Calibri.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "Calibri-Bold.ttf");
        tvBanner.setTypeface(custom_font_bold);

    }
    public void showRequestDialog(Context context, final String username, final String requestId){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Fix-A-Friend");
        builder1.setMessage(username + " wants you to send photo/video to edit. what do you want?");
        builder1.setCancelable(true);
        builder1.setNegativeButton("Reject",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new RejectTask().execute(requestId);
                        dialog.cancel();
                    }
                });
        builder1.setPositiveButton("Accept",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        globalVariable.decreaseValue(preferences, Constant.N_REQUEST);
                        Intent intent = new Intent(Activity.this, CaptureActivity.class);
                        startActivity(intent);
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    class RejectTask extends AsyncTask<String, Integer, String> {

        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {

            hideProgress();
           if(resultCode == 0){
               Utilities.showOKDialog(mContext, "You rejected photo/video request successfully!");
               globalVariable.decreaseValue(preferences, Constant.N_REQUEST);

           }else if (resultCode == 1){
               Utilities.showOKDialog(mContext, responseDesc);
           }else if (resultCode == 2){
               Utilities.showOKDialog(mContext, "Result is Null!");
           }
            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {

            parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("id", param[0]));

            JSONObject result =  HttpManager.getInstance().callPost(mContext, Constant.REJECT_REQUEST, parameters, true);

            if (result == null) {
                resultCode = -1;
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
    class ActivityTask extends AsyncTask<String, Integer, String> {
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
                }else{
                    if(arrUserModel.size() > 0) {
                        SetActivityList();
                    }
                }

            }else if (resultCode == 1){
                Utilities.showOKDialog(mContext, responseDesc);
            }else if (resultCode == 2){
                Utilities.showOKDialog(mContext, "Please try again later!");
            }

            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {

            parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("index", String.valueOf(offset)));

            JSONObject result =  HttpManager.getInstance().callPost(mContext, Constant.ACTIVITIES, parameters, true);

            if (result == null) {
                resultCode = 2;
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

                        JSONArray jsonArray= result.getJSONArray("activities");

                        int n = jsonArray.length();
                        offset ++;
                        if(n < Constant.MAX_USER_AMOUNT_PER_PAGE){

                            isLast = true;
                        }else{

                            isLast = false;
                        }
                        arrUserModel = new ArrayList<UserModel>();
                        for(int i = 0; i < n; i ++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                             UserModel userModel = new UserModel();
                            userModel.SetMessage(obj.getString("message"));
                            userModel.SetContent(obj.getString("content"));
                            userModel.SetTime(obj.getInt("dd"));
                            userModel.SetArticleId(obj.getString("article_id"));
                            userModel.SetArticleUrl(obj.getString("article"));
                            userModel.SetName(obj.getString("name"));
                            userModel.SetId(obj.getString("user_id"));
                            userModel.SetPhoto(obj.getString("photo"));
                            arrUserModel.add(userModel);
                        }

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
    public void SetActivityList(){
//        arrUserModel = getInboxItem();
        mAdapter = new ActivityAdapter(mContext, arrUserModel);
        lvActivity.setAdapter(mAdapter);
    }
    ///for test////////////////////////////////////////////////////////////////////////////////////////////////////////////////


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
//        getMenuInflater().inflate(R.menu.menu_, menu);
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
