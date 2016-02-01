package com.han.postactivities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.han.adapter.ChooseFriendAdapter;
import com.han.http.HttpManager;
import com.han.login.R;
import com.han.model.UserModel;
import com.han.utils.Constant;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ChooseFriendActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnBack, btnSend;
    private Button btnFollowing, btnContact;
    private SearchView search;
    private ListView listView;
    private PullToRefreshListView mPullRefreshSearchListView;

    private ChooseFriendAdapter mAdapter;

    private boolean flag;
    private ArrayList<UserModel> arrUserModel;
    private ArrayList<NameValuePair> parameters;
    private Context mContext;
    private ProgressDialog mProgressDialog = null;
    private String responseCode;
    private String responseDesc;
    private String strSearchKey = "" ;

    private int offset = 0;
    private String strId;
    private boolean isFollow;
//    private boolean bSearchMode;
    private boolean isLast;
    private int lastNumber;

    private String timeLimit;

    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_friend);

        initVarilables();
        initUI();
        new SearchTask().execute();
    }
    private void initUI(){
        btnBack = (Button)findViewById(R.id.btn_back_cf);
        btnBack.setOnClickListener(this);
        btnSend = (Button)findViewById(R.id.btn_send_photo_cf);
        btnSend.setOnClickListener(this);
        btnFollowing = (Button)findViewById(R.id.btn_switch_following);
        btnFollowing.setOnClickListener(this);
        btnFollowing.setClickable(false);
        btnContact = (Button)findViewById(R.id.btn_switch_contact);
        btnContact.setOnClickListener(this);
        search = (SearchView)findViewById(R.id.searchview_cf);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(!query.equals("")){
                    strSearchKey = search.getQuery().toString();
                    search();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return false;
            }
        });

        mPullRefreshSearchListView = (PullToRefreshListView)findViewById(R.id.lv_cf);

        mPullRefreshSearchListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                if(!isLast){
                    new SearchTask().execute();
                }
                mPullRefreshSearchListView.onRefreshComplete();
            }
        });
        listView = mPullRefreshSearchListView.getRefreshableView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lastNumber != -1) {

                    arrUserModel.get(lastNumber).SetIsFollowing("0");
                }
                arrUserModel.get(position - 1).SetIsFollowing("1");

                mAdapter.notifyDataSetChanged();
                lastNumber = position - 1;
            }
        });
    }
    private void initVarilables(){
        isLast = false;
        flag = true;
        offset = 0;
//        bSearchMode = true;
        arrUserModel = new ArrayList<UserModel>();
        lastNumber = -1;
        mContext = this;
        timeLimit = getIntent().getStringExtra("timelimit");
        url = getIntent().getStringExtra("url");
    }
    @Override
    public void onClick(View v) {
        if(v == btnBack){
            finish();
        }else if(v == btnSend){
            if(flag){
                if(lastNumber == -1){

                    AlertDialog.Builder conductor = new AlertDialog.Builder(ChooseFriendActivity.this);
                    conductor.setTitle("Please choose friend");
                    AlertDialog alert = conductor.create();
                    alert.show();
                }else {

                    new PostTask().execute();

                }
            }

        }else if(v == btnFollowing){
            if(!flag){
                flag = true;
                offset = 0;
                btnContact.setClickable(true);
                btnFollowing.setClickable(false);

                btnFollowing.setTextColor(getResources().getColor(R.color.black));
                btnFollowing.setBackground(getResources().getDrawable(R.drawable.round_corner));

                btnContact.setTextColor(getResources().getColor(R.color.white));
                btnContact.setBackground(getResources().getDrawable(R.drawable.round_corner_choose_friend));

                listView.setVisibility(View.VISIBLE);
                arrUserModel = new ArrayList<UserModel>();
                new SearchTask().execute();
            }
        }else if(v == btnContact){
            if(flag){
                flag = false;
                offset = 0;
                btnContact.setClickable(false);
                btnFollowing.setClickable(true);
                btnFollowing.setTextColor(getResources().getColor(R.color.white));
                btnFollowing.setBackground(getResources().getDrawable(R.drawable.round_corner_choose_friend));

                btnContact.setTextColor(getResources().getColor(R.color.black));

                btnContact.setBackground(getResources().getDrawable(R.drawable.round_corner));

                listView.setVisibility(View.GONE);
//                svExplorer.setVisibility(View.GONE);
                arrUserModel = new ArrayList<UserModel>();
//                new SearchTask().execute();

            }
        }
    }

    private void search(){
        arrUserModel = new ArrayList<UserModel>();

        offset = 0;
        if(flag){

            lastNumber = -1;
            new SearchTask().execute();
        }else{
//            new SearchArticleTask().execute();
        }
    }
    class SearchTask extends AsyncTask<String, Integer, String> {

        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(resultCode == 0){
                if(arrUserModel.size() > 0){
                    if(offset <= 1 ){
                        SetSearchList();
                    }else {
                        mAdapter.notifyDataSetChanged();
                        mPullRefreshSearchListView.onRefreshComplete();
                    }

                }
            }


            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {
            String url = Constant.GET_FRIENDS;
            parameters = new ArrayList<NameValuePair>();
            if (strSearchKey.length() == 0){

                parameters.add(new BasicNameValuePair("index", String.valueOf(offset)));
            }else {

                parameters.add(new BasicNameValuePair("key", strSearchKey));
                parameters.add(new BasicNameValuePair("index", String.valueOf(offset)));
                url = url +"/" + strSearchKey;
            }

            JSONObject result =  HttpManager.getInstance().callPost(mContext, url, parameters, true);

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

                        JSONArray jsonArray = result.getJSONArray("friends");
                        int n = jsonArray.length();
                        offset ++;
                        if(n < Constant.MAX_USER_AMOUNT_PER_PAGE){

                            isLast = true;
                        }else {
                            isLast = false;
                        }
                        arrUserModel = new ArrayList<UserModel>();
                        for (int i = 0; i < n; i ++){
                            JSONObject object = jsonArray.getJSONObject(i);

                            UserModel userModel = new UserModel();

                            userModel.SetEmail(object.getString("email"));
                            userModel.SetPhoto(object.getString("photo"));
                            userModel.SetName(object.getString("name"));
                            userModel.SetId(object.getString("id"));
//                            userModel.SetIsFollowing(object.getString("is_following"));
                            userModel.SetIsFollowing("0");
                            arrUserModel.add(userModel);
                            resultCode = 0;
                        }

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
    class PostTask extends AsyncTask<String, Integer, String> {
        String filetype = "";
        int resultCode = -1;

        protected void onPreExecute() {
            showProgress();
            String extention = url.substring(url.length() - 3);
            if(extention.equals("jpg")){
                filetype = "1";
            }else if(extention.equals("mp4")){
                filetype = "2";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(resultCode == 0){
                finishActivity();
            }
            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {
            ArrayList<NameValuePair > arrParams = new ArrayList<NameValuePair>();
            arrParams.add(new BasicNameValuePair("receiver_email", arrUserModel.get(lastNumber).GetEmail()));
            arrParams.add(new BasicNameValuePair("file_type", filetype));
            arrParams.add(new BasicNameValuePair("limit", timeLimit));
            arrParams.add(new BasicNameValuePair("request_id", "-1"));
            JSONObject result = null;
            try {
                result = HttpManager.getInstance().postImage(mContext, Constant.SEND_ARTICLE, arrParams, url, true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

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

    public void SetSearchList(){

        mAdapter = new ChooseFriendAdapter(mContext, arrUserModel);
        listView.setAdapter(mAdapter);
    }
    private void finishActivity(){
        setResult(100);
        finish();
    }
}
