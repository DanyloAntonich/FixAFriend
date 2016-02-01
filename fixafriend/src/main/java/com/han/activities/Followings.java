package com.han.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.han.adapter.FollowingAdapter;
import com.han.login.R;
import com.han.http.HttpManager;
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

public class Followings extends AppCompatActivity {
    private TextView btnBack;
    private ListView lvFollowings;
    private PullToRefreshListView mPullRefreshSearchListView;
    private TextView tvBanner;

    private FollowingAdapter mAdapter;
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
        setContentView(R.layout.activity_followings);

        initVariables();
        initUI();
        setFont();

        new FollowingTask().execute();
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

        btnBack = (TextView)findViewById(R.id.btn_followings_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPullRefreshSearchListView = (PullToRefreshListView)findViewById(R.id.lv_followings);

        mPullRefreshSearchListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                if(!isLast){
                    new FollowingTask().execute();
                }
                mPullRefreshSearchListView.onRefreshComplete();
            }
        });
        lvFollowings = mPullRefreshSearchListView.getRefreshableView();
        lvFollowings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Followings.this, ProfileFriendActivity.class);
                intent.putExtra("userid", arrUserModel.get(position - 1).GetId());
                startActivity(intent);

            }
        });
        tvBanner = (TextView)findViewById(R.id.tv_folowings_title);
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
    class FollowingTask extends AsyncTask<String, Integer, String> {

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
            JSONObject result =  HttpManager.getInstance().callPost(mContext, Constant.FOLLOW + "followings/" + String.valueOf(userId), parameters, true);

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
                        if(length < Constant.MAX_USER_AMOUNT_PER_PAGE){

                            isLast = true;
                        }else{
                            offset ++;
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
        mAdapter = new FollowingAdapter(mContext, arrUserModel);
        lvFollowings.setAdapter(mAdapter);
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
            um.SetName("Nice" + i);
            um.SetMessage("Hello!!!");
//            um.SetTime("1h");
            afm.add(um);
        }
        return afm;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_followings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
