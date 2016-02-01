package com.han.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.han.http.HttpManager;
import com.han.sticky_adapter.HashBannerAdapter;
import com.han.sticky_adapter.HashBodyAdapter;
import com.han.login.R;
import com.han.main.CaptureActivity;
import com.han.main.MessageActivity;
import com.han.main.ProfileActivity;
import com.han.main.SearchActivity;
import com.han.model.CommentModel;
import com.han.model.FeedBannerModel;
import com.han.model.FeedBodyModel;
import com.han.model.FeedModel;
import com.han.utils.Constant;
import com.han.utils.GlobalVariable;
import com.han.utils.Utilities;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HashTagActivity extends AppCompatActivity {


    private TextView btnBackToFeed, btnRefresh;
    private TextView tvToSearch, tvToMessage, tvToCamera, tvToProfile, tvBanner;
    private TextView tvNoticeInvite, tvNoticeMessage, tvNoticeRequest;
    int offset , limit = 10, count = 0;
    public boolean isLast = false;

    public boolean isLikeUser;

    ArrayList<FeedModel> sectionList ;


    //Sticky Header....
    private RecyclerView list;
    private StickyHeadersItemDecoration top;
    private HashBodyAdapter personAdapter;
    private HashBannerAdapter headerAdapter;

    ////////////////////////////////////////////////

    private Context mContext;
    private ProgressDialog mProgressDialog;
    private String responseCode;
    private String responseDesc;
    private ArrayList<NameValuePair> parameters;
    private String strHashCode;
    GlobalVariable globalVariable ;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hash_tag);

        initVariables();
        initUI();
        setFont();
        loadHashData();

        if(globalVariable.getValue(preferences, Constant.N_INVITATION) > 0){
            tvNoticeInvite.setVisibility(View.VISIBLE);
            tvNoticeInvite.setText(String.valueOf(globalVariable.getValue(preferences, Constant.N_INVITATION)));

        }
        if(globalVariable.getValue(preferences, Constant.N_MESSAGE) > 0){
            tvNoticeMessage.setVisibility(View.VISIBLE);
            tvNoticeMessage.setText(String.valueOf(globalVariable.getValue(preferences, Constant.N_MESSAGE)));
        }
        if(globalVariable.getValue(preferences, Constant.N_REQUEST) > 0){
            tvNoticeRequest.setVisibility(View.VISIBLE);
            tvNoticeRequest.setText(String.valueOf(globalVariable.getValue(preferences, Constant.N_REQUEST)));
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
    private void loadHashData(){
        if (Utilities.haveNetworkConnection(HashTagActivity.this)) {

            try{
                LoadHashCodeTask task = new LoadHashCodeTask();
                task.execute();
//                setListAdapter();
            }catch (Exception e){
                e.printStackTrace();
            }

        } else {
            Toast.makeText(HashTagActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }

    }
    private void initVariables(){
        mContext = this;
        isLikeUser = false;
//        start = 0;
        offset = 0;
        strHashCode = getIntent().getStringExtra("hashcode");
        mProgressDialog = null;
        sectionList = new ArrayList<FeedModel>();
        globalVariable = new GlobalVariable();
        preferences = Utilities.getSharedPreferences(this);
    }
    private void initUI(){
        tvNoticeInvite = (TextView)findViewById(R.id.badge_hash_invitation);
        tvNoticeMessage = (TextView)findViewById(R.id.badge_hash_message_1);
        tvNoticeRequest = (TextView)findViewById(R.id.badge_hash_request);

        list = (RecyclerView) findViewById(R.id.list_hashtag);
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        btnBackToFeed = (TextView)findViewById(R.id.btn_back_to_feed);
        btnBackToFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnRefresh = (TextView)findViewById(R.id.btn_refresh_hashtag);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                refresh();
            }
        });

        tvToCamera = (TextView)findViewById(R.id.hashtag_camera);
        tvToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HashTagActivity.this, CaptureActivity.class);
                startActivity(intent);

            }
        });
        tvToMessage = (TextView)findViewById(R.id.hashtag_message);
        tvToMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HashTagActivity.this, MessageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvToProfile = (TextView)findViewById(R.id.hashtag_profile);
        tvToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HashTagActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvToSearch = (TextView)findViewById(R.id.hashtag_search);
        tvToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HashTagActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvBanner = (TextView)findViewById(R.id.tv_hashtag);
        tvBanner.setText("#" + strHashCode);

        list.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                int i = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                int j = linearLayoutManager.findLastVisibleItemPosition();

                if (j == sectionList.size() - 1) {
                    //Its at bottom ..
                    Log.e("ennnd", "deeeeeddd===============");
                    if (!isLast) {
                        Log.e("ennnd", "call__api__now");
//                        offset = limit;
                        count ++;
                        if (count == 3) {

                            LoadHashCodeTask task = new LoadHashCodeTask();
                            task.execute();
                            count = 0;

                        }
                    }
                }
            }
        });
    }
    private void setFont(){

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "Calibri.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "Calibri-Bold.ttf");
        tvBanner.setTypeface(custom_font_bold);

    }
    private void setListAdapter(){
        if(sectionList.size() > 0){
            personAdapter = new HashBodyAdapter(mContext, sectionList);
            headerAdapter = new HashBannerAdapter(sectionList);

            top = new StickyHeadersBuilder()
                    .setAdapter(personAdapter)
                    .setRecyclerView(list)
                    .setStickyHeadersAdapter(headerAdapter)
                    .build();


            list.setAdapter(personAdapter);
            list.addItemDecoration(top);

        }


    }
//    public void setHashCode(String str){
////        initVariables();
//        offset = 0;
//        strHashCode = str;
//        loadFeedData();
//    }
    private void refresh(){
//        list.removeAllViews();
        setContentView(R.layout.activity_hash_tag);
        initVariables();
        initUI();
        setFont();
        loadHashData();
    }
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////

    class LoadHashCodeTask extends AsyncTask<String, Integer, String> {

        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(offset > 1){
                personAdapter.notifyDataSetChanged();
            }else{
                setListAdapter();
            }

            this.cancel(true);
            super.onPostExecute(result);
        }


        @Override
        protected String doInBackground(String... param) {

            parameters = new ArrayList<NameValuePair>();
            String strUrl;
//            int length = strHashCode.length();
//            if (length == 0){
//
//                strUrl = Constant.ARTICLE_INDEX;
//            }else {

                strUrl =  Constant.ARTICLE_INDEX +"/" + strHashCode;
//            }
            parameters.add(new BasicNameValuePair("index", String.valueOf(offset)));

            Log.d("LoadFeedTask", "LoadFeedTask doInBackground");
            JSONObject result =  HttpManager.getInstance().callPost(mContext, strUrl, parameters, true);

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
                        JSONArray arrResult = result.getJSONArray("articles");
                        int n = arrResult.length();
                        if(n < Constant.MAX_ARTICLE_AMOUNT_PER_PAGE){

                            isLast = true;
                        }else{
                            offset ++;
                            isLast = false;
                        }

                        for(int i = 0; i < n; i ++){
                            FeedModel fm = new FeedModel();
                            FeedBannerModel bannerModel = new FeedBannerModel();
                            FeedBodyModel bodyModel = new FeedBodyModel();

                            JSONObject obj = arrResult.getJSONObject(i);

                            bannerModel.SetPhoto(obj.getString("owner_photo"));
                            bannerModel.SetUsername(obj.getString("owner_name"));
                            bannerModel.SetPostedDate(obj.getInt("posted_date"));
                            bannerModel.SetEditNumber(obj.getString("editamounts"));


                            bodyModel.SetFeedId(String.valueOf(i + sectionList.size()));
                            bodyModel.SetIslike(obj.getString("is_like"));
                            bodyModel.SetPosterName(obj.getString("poster_name"));
                            bodyModel.SetAttachURL(obj.getString("attach_url"));
                            bodyModel.SetOwnerEmail(obj.getString("owner_email"));
                            bodyModel.SetArticleId(obj.getString("article_id"));
                            bodyModel.SetPosterId(obj.getString("poster_id"));
                            bodyModel.SetLikeCount(obj.getString("like_count"));
                            bodyModel.SetDescriptioin(obj.getString("description"));
                            bodyModel.SetOwnerId(obj.getString("owner_id"));
                            bodyModel.SetAttatchType(obj.getString("attach_type"));
                            bodyModel.SetCommentCount(obj.getString("comment_count"));
                            bodyModel.SetTag(obj.getString("tag"));

                            JSONArray arrComment = obj.getJSONArray("comments");
                            ArrayList<CommentModel> arrCM = new ArrayList<CommentModel>();
                            for(int j = 0; j < arrComment.length(); j ++){
                                CommentModel cm = new CommentModel();
                                JSONObject objComment = arrComment.getJSONObject(j);
                                cm.SetComment(objComment.getString("comment"));
                                cm.SetPhotoURL(objComment.getString("photo"));
                                cm.SetUserId(objComment.getString("user_id"));
                                cm.SetName(objComment.getString("name"));

                                arrCM.add(cm);
                            }
                            bodyModel.SetArrCommentModel(arrCM);

                            fm.SetFeedBannerModel(bannerModel);
                            fm.SetFeedBodyModel(bodyModel);


                            sectionList.add(fm);

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

    ///////////////for test//////////////////////////
//    public ArrayList<UserModel> getInboxItem(){
//
//        ArrayList<UserModel> afm = new ArrayList<UserModel>();
//        for(int i = 0; i < 5; i ++){
//            UserModel um = new UserModel();
//            um.SetUseame("Nice");
//            um.SetMessage("Hello!!!");
//
//            afm.add(um);
//        }
//        return afm;
//    }

//    public void addFeedItem(int test_limit){
//
//
//        sectionList = new ArrayList<FeedModel>();
//        for(int i = 0; i < test_limit; i ++){
//
//            FeedModel fm = new FeedModel();
//
//            FeedBannerModel banner = new FeedBannerModel();
//
//            FeedBodyModel body = new FeedBodyModel();
//
//            body.SetFeedId(String.valueOf(sectionList.size()));
//            body.SetCommentBody("Nice!!!");
//            body.SetCommentCount("2");
//            body.SetIslike("true");
//            body.SetLikeCount("3");
//            body.SetMessage("Hello!");
//
//            banner.SetTime("3h");
//            banner.SetUsername("Me");
//
//            fm.SetFeedBodyModel(body);
//            fm.SetFeedBannerModel(banner);
//
//            sectionList.add(fm);
//
//        }
//        if(sectionList.size() > 10) {
//
//            personAdapter.notifyDataSetChanged();
//        }
//
//
//    }
//    // Reset All Values to its default values
//    public void ResetValues() {
//        offset = 0;
//        limit = 10;
//        count = 0;
//        isLast = true;
//        isFromScroll = false;
//        flag_loading = false;
//
////        try {
////            currentDateTime = URLEncoder.encode(currentDateTime, "UTF-8");
////        } catch (UnsupportedEncodingException e) {
////            // TODO Auto-generated catch block
////            e.printStackTrace();
////        }
//        feedtList.clear();
//        sectionList.clear();
//        if (feedAdapter != null) {
////            adapterEvent.clear();
//            feedAdapter.notifyDataSetChanged();
//        }
//    }


}
