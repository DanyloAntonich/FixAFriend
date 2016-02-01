package com.han.main;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import com.han.activities.ProfileFriendActivity;
import com.han.adapter.SearchAdapter;
import com.han.adapter.SearchGridAdapter;
import com.han.downloadmanager.DrawQueueManager;
import com.han.http.HttpManager;
import com.han.login.R;
import com.han.model.CommentModel;
import com.han.model.FeedBannerModel;
import com.han.model.FeedBodyModel;
import com.han.model.FeedModel;
import com.han.model.SearchGridViewModel;
import com.han.model.UserModel;
import com.han.utils.Constant;
import com.han.utils.GlobalVariable;
import com.han.utils.Utilities;
import com.han.widget.CircularImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity  {

    private  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator ;

    private SearchView search;

    private TextView btnUsers, btnExplorer;
    private RelativeLayout rlUsers, rlExplorer;
    private LinearLayout llSwitch;
    private Button btnSwich;
    private TextView tvNoticeInvite, tvNoticeMessage, tvNoticeRequest;
    private PullToRefreshListView mPullRefreshSearchListView;
//    private PullToRefreshScrollView mPullRefreshScrollView;
    private PullToRefreshGridView mPullRefreshGridView;
    private GridView mGridView;
    private ListView actualListView;
    private ScrollView scrollView;
//    private LinearLayout linearLayout;
    private TextView tvToFeed, tvToCamera, tvToMessage, tvToProfile;

    private SearchAdapter mAdapter;
    private SearchGridAdapter mGridAdapter;
    private ArrayList<UserModel> arrUserModel;
    private ArrayList<FeedModel> sectionList ;

    private LayoutInflater layoutInflater;

    private Context mContext;
    private ProgressDialog mProgressDialog = null;
    private String responseCode;
    private String responseDesc;
    private String strSearchKey = "" ;

    private int screenWidth = 0;

    private int offset = 0, count = 0;
    private boolean bSearchMode = true;
    private ArrayList<NameValuePair> parameters;



    private String strId;
    private boolean isFollow;

    public boolean isLast ;

    private  boolean switchClicked;
    private int padding = 0;

    DownloadManager manager ;

    DrawQueueManager drawQueueManager;


    GlobalVariable globalVariable ;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initVariables();
        initUI();
        startTimer();




//        startDrawTimer();
    }
    private void callGC(Intent intent) {
        // For stack clear
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // For calling GC
        System.runFinalization();
        System.exit(0);
    }
    @Override
    protected void onResume() {
        super.onResume();
        ////show notification badge
        if(globalVariable.getValue(preferences, Constant.N_INVITATION) != 0){
            tvNoticeInvite.setVisibility(View.VISIBLE);
            tvNoticeInvite.setText(String.valueOf(globalVariable.getValue(preferences, Constant.N_INVITATION)));

        }
        if(globalVariable.getValue(preferences, Constant.N_MESSAGE) != 0){
            tvNoticeMessage.setVisibility(View.VISIBLE);
            tvNoticeMessage.setText(String.valueOf(globalVariable.getValue(preferences, Constant.N_MESSAGE)));
        }

        if(globalVariable.getValue(preferences, Constant.N_REQUEST) != 0){
            tvNoticeRequest.setVisibility(View.VISIBLE);
            tvNoticeRequest.setText(String.valueOf(globalVariable.getValue(preferences, Constant.N_REQUEST)));
        }
    }

    public void initVariables(){

        sectionList = new ArrayList<FeedModel>();
        arrUserModel = new ArrayList<UserModel>();
        mContext = this;
        offset = 0;
        isLast = false;
        count = 0;
        manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        DisplayMetrics display = this.getResources().getDisplayMetrics();
        screenWidth = display.widthPixels;

        drawQueueManager = new DrawQueueManager();

        globalVariable = new GlobalVariable();
        preferences = Utilities.getSharedPreferences(this);
    }

    private void initUI(){

        tvNoticeInvite = (TextView)findViewById(R.id.txt_invitation_cnt_search);
        tvNoticeMessage = (TextView)findViewById(R.id.txt_message_cnt_search);
        tvNoticeRequest = (TextView)findViewById(R.id.txt_request_cnt_search);

        layoutInflater = getLayoutInflater();

        search = (SearchView)findViewById(R.id.searchview);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals("")) {
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
        search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
//                search.setQuery("", false);
                search.onActionViewCollapsed();
                search();
                return false;
            }
        });


//        linearLayout = (LinearLayout)findViewById(R.id.linear_gridview);

        llSwitch = (LinearLayout)findViewById(R.id.ll_search_padding);

        btnSwich = (Button)findViewById(R.id.btn_switch_button);
        btnSwich.setWidth((screenWidth / 2) - 40);

        rlUsers = (RelativeLayout)findViewById(R.id.rl_users);
        rlExplorer = (RelativeLayout)findViewById(R.id.rl_explorer);

        ///////////set relative layout param(width)
        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)rlUsers.getLayoutParams();
        params1.width =( screenWidth / 2) - 50;
        rlUsers.setLayoutParams(params1);

        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)rlExplorer.getLayoutParams();
        params2.width =( screenWidth / 2) - 50;
        rlExplorer.setLayoutParams(params2);

        ////////////set layout click listener
        rlUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bSearchMode) {
                    bSearchMode = true;
                    switchClicked = true;
                    initSwitch(bSearchMode);

                }

            }
        });
        rlExplorer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bSearchMode) {
                    bSearchMode = false;
                    switchClicked = true;
                    initSwitch(bSearchMode);
                }

            }
        });

        btnUsers = (TextView)findViewById(R.id.btn_switch_users);
        btnExplorer = (TextView)findViewById(R.id.btn_switch_explorer);

        ///////create pull to refresh scrollview=================================================
//        mPullRefreshScrollView = (PullToRefreshScrollView)findViewById(R.id.gridview_search);
//        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
//
//            @Override
//            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
//                if(!isLast){
//                    new SearchArticleTask().execute();
//                }
//                mPullRefreshScrollView.onRefreshComplete();
//            }
//        });
//
//        scrollView = mPullRefreshScrollView.getRefreshableView();

        //create pull to refresh grid view
        mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.gridview_search);



        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
//                Toast.makeText(SearchActivity.this, "Pull Down!", Toast.LENGTH_SHORT).show();
//                new GetDataTask().execute();
                mPullRefreshGridView.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
//                Toast.makeText(SearchActivity.this, "Pull Up!", Toast.LENGTH_SHORT).show();
                if(!isLast){
                    new SearchArticleTask().execute();
                }
                mPullRefreshGridView.onRefreshComplete();
            }

        });
        mGridView = mPullRefreshGridView.getRefreshableView();
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, FeedViewActivity.class);
                intent.putExtra("mode", 1);
                intent.putExtra("article_id", sectionList.get(position).GetFeedBodyModel().GetArticleId());
                mContext.startActivity(intent);
            }
        });
//
        /////////////////////create pull to refresh list view====================================
        mPullRefreshSearchListView = (PullToRefreshListView)findViewById(R.id.lv_search);
        mPullRefreshSearchListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                if(!isLast){
                    new SearchTask().execute();
                }
                mPullRefreshSearchListView.onRefreshComplete();


            }
        });

        actualListView = mPullRefreshSearchListView.getRefreshableView();
        actualListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent = new Intent(SearchActivity.this, ProfileFriendActivity.class);
                intent.putExtra("userid", arrUserModel.get(position - 1).GetId());
                startActivity(intent);

//                callGC(intent);

            }
        });
        ///////////////////////==================================================================
        tvToCamera = (TextView)findViewById(R.id.search_camera);
        tvToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, CaptureActivity.class);
                startActivity(intent);

            }
        });
        tvToFeed = (TextView)findViewById(R.id.search_feed);
        tvToFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, FeedViewActivity.class);
                startActivity(intent);
                Utilities.deleteCache(mContext);
//                finishAffinity();
                finish();
//                callGC(intent);
            }
        });
        tvToMessage = (TextView)findViewById(R.id.search_message);
        tvToMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, MessageActivity.class);
                startActivity(intent);
                Utilities.deleteCache(mContext);
//                finishAffinity();
                finish();
//                callGC(intent);
            }
        });
        tvToProfile = (TextView)findViewById(R.id.search_profile);
        tvToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                startActivity(intent);
                Utilities.deleteCache(mContext);
//                finishAffinity();
                finish();
//                callGC(intent);
            }
        });

        initSwitch(bSearchMode);
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        System.runFinalization();
//        Runtime.getRuntime().gc();
//        System.gc();

    }


    public void download(final int pos, final SearchGridViewModel searchGridViewModel){

        String url = sectionList.get(pos).GetFeedBodyModel().GetAttachURL();
        ///get filename from url
        final String fileName = url.substring(url.lastIndexOf('/') + 1);
        ///creat download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        ////////set destinatin storage path
//        request.setDestinationInExternalPublicDir(PATH, fileName);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        final long downloadId = manager.enqueue(request);
        searchGridViewModel.circleProgressView.setValueAnimated(0, 10);
        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean downloading = true;
                double dl_progress = 0;
                while (downloading) {

                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadId);

                    Cursor cursor = manager.query(q);
                    cursor.moveToFirst();
                    int bytes_downloaded = cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                    }

                    dl_progress = bytes_total != 0 ? (int) ((bytes_downloaded * 100l) / bytes_total) : 0;

                    final double finalDl_progress = dl_progress;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            int percent = (int) finalDl_progress;
                            searchGridViewModel.circleProgressView.setValueAnimated(percent, 100);
                            if (percent == 100) {
                                searchGridViewModel.circleProgressView.setVisibility(View.GONE);
                                searchGridViewModel.circularImageView.setVisibility(View.VISIBLE);
                                Bitmap bitmap = null;
                                if(sectionList.get(pos).GetFeedBodyModel().GetAttatchType().equals("2")){
                                    searchGridViewModel.ivIsVideo.setVisibility(View.VISIBLE);
                                    searchGridViewModel.circularImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle2));
                                }else {
                                    searchGridViewModel.ivIsVideo.setVisibility(View.GONE);
                                    ///get bitmap from localpath
                                    bitmap = BitmapFactory.decodeFile(PATH + fileName);
                                    if(bitmap != null){
                                        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, screenWidth, screenWidth);
                                        searchGridViewModel.circularImageView.setImageBitmap(thumbnail);
                                    }else {
                                        searchGridViewModel.circularImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle2));
                                    }

                                }

                            }
                        }
                    });
                    cursor.close();
                }

            }
        }).start();
    }

    private void initSwitch(boolean flag){
        search.onActionViewCollapsed();
        initVariables();
        strSearchKey = "";

        ////release memory
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
        ////////////
        if(flag){
            rlUsers.setClickable(false);
            rlExplorer.setClickable(true);

            btnUsers.setTextColor(getResources().getColor(R.color.green));

            btnExplorer.setTextColor(getResources().getColor(R.color.white));

            mPullRefreshSearchListView.setVisibility(View.VISIBLE);
            mPullRefreshGridView.setVisibility(View.GONE);




            new SearchTask().execute();
        }else{
            rlUsers.setClickable(true);
            rlExplorer.setClickable(false);

            btnUsers.setTextColor(getResources().getColor(R.color.white));

            btnExplorer.setTextColor(getResources().getColor(R.color.green));

            mPullRefreshSearchListView.setVisibility(View.GONE);
            mPullRefreshGridView.setVisibility(View.VISIBLE);
//            linearLayout.removeAllViews();
            new SearchArticleTask().execute();
        }
    }

    private void startTimer(){
        final Handler handler = new Handler();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if(switchClicked){

                            if(!bSearchMode){
                                if(padding < ((screenWidth / 2) - 60)){
                                    padding +=15;
                                    llSwitch.setPadding(padding, 0,0,0);

                                }else {
                                    switchClicked = false;
                                }
                            }else {
                                if(padding > 0){
                                    padding -= 15;
                                    llSwitch.setPadding(padding, 0,0,0);
                                }else {
                                    switchClicked = false;
                                }

                            }
                        }

                    }
                });
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 15);
    }

    private void search(){
        initVariables();

        if(bSearchMode){
            new SearchTask().execute();
        }else{
//            linearLayout.removeAllViews();
            new SearchArticleTask().execute();

        }
    }

    public void changeFollow(UserModel userModel, int position, boolean flag){

        isFollow = flag;
        strId = userModel.GetId();
        if(isFollow){
            arrUserModel.get(position).SetIsFollowing("1");
        }else{
            arrUserModel.get(position).SetIsFollowing("0");
        }
        mAdapter.notifyDataSetChanged();
        new ChangeFollowTask().execute();
    }

    private boolean checkFileExist(String fileName){

        File file = new File(PATH, fileName);
        if(file.exists()){
            return true;
        }else
            return false;
    }

    private void setImageViewSize(CircularImageView imageview){

        imageview.setMaxWidth((int)(screenWidth / 3) - 5);
        imageview.setMaxHeight((int) (screenWidth / 3) - 5);

    }

    private void setRelativeLayoutSize(RelativeLayout relativeLayout){
        ViewGroup.LayoutParams layoutParams = null;
        try{
            layoutParams =  relativeLayout.getLayoutParams();
        }catch (Exception e){
            e.printStackTrace();
        }
        layoutParams.width = screenWidth / 3;
        layoutParams.height = screenWidth / 3;


        relativeLayout.setLayoutParams(layoutParams);


    }

    class SearchTask extends AsyncTask<String, Integer, String> {
        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {

            if(offset > 1){
                mAdapter.notifyDataSetChanged();
                mPullRefreshSearchListView.onRefreshComplete();
            }else{
                if(arrUserModel.size() > 0){
                    SetSearchList();
                    ////////remove all views from gridview
                    if(mGridView.getChildCount() > 0){
                        mGridView.setAdapter(null);
                    }
                }
            }
            hideProgress();
            if(resultCode == 1){
                Utilities.showOKDialog(mContext, Constant.ALERT1);
            }
            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {
            String url = Constant.SEARCH;
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
                resultCode = 1;
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


                        JSONArray jsonArray = result.getJSONArray("users");
                        int n = jsonArray.length();
                        offset ++;
                        if(n < Constant.MAX_USER_AMOUNT_PER_PAGE){

                            isLast = true;
                        }else{

                            isLast = false;
                        }


                        for (int i = 0; i < n; i ++){
                            JSONObject object = jsonArray.getJSONObject(i);

                            UserModel userModel = new UserModel();

                            userModel.SetEmail(object.getString("email"));
                            userModel.SetPhoto(object.getString("photo"));
                            userModel.SetName(object.getString("name"));
                            userModel.SetId(object.getString("user_id"));
                            userModel.SetIsFollowing(object.getString("is_following"));

                            arrUserModel.add(userModel);
                        }

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

    class SearchArticleTask extends AsyncTask<String, Integer, String> {
        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {



            if(resultCode == 2){
                Utilities.showOKDialog(mContext, Constant.ALERT1);
            }else if(resultCode == 0) {
//                SetScrollView();
                if(offset > 1){
                    mGridAdapter.notifyDataSetChanged();
                }else {
                    SetSearchGridList();
                }
            }else if(resultCode == 1) {
                Utilities.showOKDialog(mContext, responseDesc);
            }
            hideProgress();
            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {
            String url = Constant.SEARCH_ARTICLE;
            parameters = new ArrayList<NameValuePair>();
            if (strSearchKey.length() == 0){

                parameters.add(new BasicNameValuePair("index", String.valueOf(offset)));
            }else {

                parameters.add(new BasicNameValuePair("index", String.valueOf(offset)));
                url = url +"/" + strSearchKey;
            }

            JSONObject result =  HttpManager.getInstance().callPost(mContext, url, parameters, true);

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

                        JSONArray arrResult = result.getJSONArray("articles");

                        int n = arrResult.length();
                        offset ++;
                        if(n < Constant.MAX_FEED_ARTICLE_PER_PAGE){

                            isLast = true;
                        }else{

                            isLast = false;
                        }

                        for(int i = 0; i < n; i ++) {


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
                            bodyModel.SetFlag(false);

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

                            resultCode = 0;
                        }
                    } else {
                        resultCode = 1;
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

    class ChangeFollowTask extends AsyncTask<String, Integer, String> {

        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(resultCode == 1) {
                Utilities.showOKDialog(mContext, Constant.ALERT1);
            } else if(resultCode == 2) {
                Utilities.showOKDialog(mContext, "Follow successfully");
            } else if(resultCode == 3) {
                Utilities.showOKDialog(mContext, "Unfollow successfully");
            }
            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {
            String url ;
            parameters = new ArrayList<NameValuePair>();
            if (isFollow){
                parameters.add(new BasicNameValuePair("selected_id", strId));
                url = Constant.FOLLOW + strId + "/" + "follow";
            }else {
                parameters.add(new BasicNameValuePair("selected_id", strId));
                url = Constant.UNFOLLOW + strId + "/" + "unfollow";
            }

            JSONObject result =  HttpManager.getInstance().callPost(mContext, url, parameters, true);

            if (result == null) {
                resultCode = 1;
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

                        if(isFollow){
                            resultCode = 2;
//                            Utilitie.showToast(SearchActivity.this, "Follow successfully");
                        }else {
                            resultCode = 3;
//                            Utilitie.showToast(SearchActivity.this, "Unfollow successfully");
                        }


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

    public void SetSearchList(){

        mAdapter = new SearchAdapter(mContext, arrUserModel);
        actualListView.setAdapter(mAdapter);
    }

    public void SetSearchGridList(){
        mGridAdapter = new SearchGridAdapter(mContext, sectionList);

        mGridView.setAdapter(mGridAdapter);
//        drawGrid((offset - 1) * Constant.MAX_FEED_ARTICLE_PER_PAGE);
    }
//    private void drawGrid(int start){
//
//        View view = null;
//        LinearLayout h_linearLayout = null;
//        for (int i = start; i < sectionList.size(); i ++){
//            Log.d("==========================================", String.valueOf(i));
//            if(i % 3 == 0){
//                view = layoutInflater.inflate(R.layout.item_profile_grid_item, null);
//                try{
//                    h_linearLayout = (LinearLayout)view.findViewById(R.id.ll_profile_grid_item);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//            }
//            View v2 = layoutInflater.inflate(R.layout.item_search_grid, null);
//
//            SearchGridViewModel searchGridViewModel = new SearchGridViewModel();
//
//            searchGridViewModel.circularImageView = (CircularImageView)v2.findViewById(R.id.civ_search_grid);
//            final int finalI1 = i;
//            searchGridViewModel.circularImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(SearchActivity.this, FeedViewActivity.class);
//                intent.putExtra("mode", 1);
//                intent.putExtra("article_id", sectionList.get(finalI1).GetFeedBodyModel().GetArticleId());
//                mContext.startActivity(intent);
//                }
//            });
//            setImageViewSize(searchGridViewModel.circularImageView);
//
//            searchGridViewModel.ivIsVideo = (ImageView)v2.findViewById(R.id.iv_isvideo_search);
//            searchGridViewModel.relativeLayout = (RelativeLayout)v2.findViewById(R.id.rl_search_article);
//            setRelativeLayoutSize(searchGridViewModel.relativeLayout);
//
//            searchGridViewModel.circleProgressView = (CircleProgressView)v2.findViewById(R.id.kcjCircleView0);
//
//            final int finalI = i;
//
//            ///set image
//            final String fileName = sectionList.get(finalI).GetFeedBodyModel().GetAttachURL().substring(sectionList.get(finalI).GetFeedBodyModel().GetAttachURL().lastIndexOf('/') + 1);
//            if(!checkFileExist(fileName)){
//                searchGridViewModel.circleProgressView.setVisibility(View.VISIBLE);
//                searchGridViewModel.circularImageView.setVisibility(View.GONE);
//                searchGridViewModel.ivIsVideo.setVisibility(View.GONE);
//                try{
//                    download(i, searchGridViewModel);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//            }else {
//                searchGridViewModel.circularImageView.setVisibility(View.VISIBLE);
//                searchGridViewModel.circleProgressView.setVisibility(View.GONE);
//                if(sectionList.get(finalI).GetFeedBodyModel().GetAttatchType().equals("2")){
//                    searchGridViewModel.ivIsVideo.setVisibility(View.VISIBLE);
//                    /////make thumbnail and
////                    searchGridViewModel.circularImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle2));
//                    searchGridViewModel.circularImageView.setImageDrawable(getResources().getDrawable(R.drawable.circle2));
//                }else {
//                    searchGridViewModel.ivIsVideo.setVisibility(View.GONE);
//                    Bitmap thumbnail = null;
//                    try{
//                        ///get bitmap from local storeage
//                        Bitmap   bitmap = BitmapFactory.decodeFile(PATH + fileName);
//                        thumbnail = ThumbnailUtils.extractThumbnail(bitmap, screenWidth, screenWidth);
//
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//
//                    if(thumbnail != null){
//                        searchGridViewModel.circularImageView.setImageBitmap(thumbnail);
//                    }else {
//                        searchGridViewModel.circularImageView.setImageDrawable(getResources().getDrawable(R.drawable.circle2));
//                    }
//
//                }
//            }
//
//            try{
//                h_linearLayout.addView(v2, i % 3);
//
//                if(i == sectionList.size() - 1 && (i % 3) < 2){
//                    linearLayout.addView(view, (int)(i / 3) );
//                    return;
//                }
//                if((i % 3) == 2){
//                    try{
//                        linearLayout.addView(view, (int)(i / 3) );
//
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }
//
//
//    }

}
