package com.han.main;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.han.activities.InboxActivity;

import com.han.adapter.FeedAdapter;
import com.han.login.R;
import com.han.http.HttpManager;
import com.han.model.CommentModel;
import com.han.model.FeedBannerModel;
import com.han.model.FeedBodyModel;
import com.han.model.FeedModel;
import com.han.utils.Constant;
import com.han.utils.GlobalVariable;
import com.han.utils.Utilities;
import com.han.widget.CustomLayoutManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import at.grabner.circleprogress.CircleProgressView;

public class FeedViewActivity extends AppCompatActivity  {

    private  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator ;

    private TextView btnToInbox, btnRefresh, tvBack;
    private TextView tvToSearch, tvToMessage, tvToCamera, tvToProfile, tvBanner;
    private TextView tvNoticeFeed, tvNoticeMessage, tvNoticeInbox, tvNoticeRequest;
    int offset , count = 0;
    public boolean isLast ;
    private int mode;
    public boolean isLikeUser;

    ArrayList<FeedModel> sectionList ;

    //recycler view...
//    private RecyclerView list;
//    private StickyHeadersItemDecoration top;
    ///////sticky adapter
//    private BodyAdapter personAdapter;
//    private HeaderAdapter headerAdapter;
    ///list view
    private PullToRefreshListView mPullRefreshSearchListView;
    private ListView listView;
    private FeedAdapter feedAdapter;
    ////////////////////////////////////////////////

    private Context mContext;
    private ProgressDialog mProgressDialog;
    private String responseCode;
    private String responseDesc;
    private ArrayList<NameValuePair> parameters;
    private String strHashCode;

    private String articleId;
    private boolean islike;
    private int position;

    private int currentPosition = 0;

    CustomLayoutManager layoutManager;

    DownloadManager manager ;

    GlobalVariable globalVariable ;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_view);

        initVariables();
        initUI();
        setFont();

        mode = getIntent().getIntExtra("mode", 0);

        if(mode == 1){
            articleId = getIntent().getStringExtra("article_id");
            btnRefresh.setVisibility(View.GONE);
            btnToInbox.setVisibility(View.GONE);
            tvBack.setVisibility(View.VISIBLE);
            new LoadFeedTask2().execute();
        }else if(mode == 0){

            loadFeedData();
        }else if (mode == 2){
            articleId = getIntent().getStringExtra("article_id");
            btnRefresh.setVisibility(View.GONE);
            btnToInbox.setVisibility(View.GONE);
            tvBack.setVisibility(View.VISIBLE);
            new LoadFeedTask3().execute();
        }


    }

    public void freeMemory(){
        //free memory
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
        //free cache
        Utilities.deleteCache(mContext);
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
        //free memory
        freeMemory();
        ////show notification badge
        if(globalVariable.getValue(preferences, Constant.N_INVITATION) > 0){
            tvNoticeFeed.setVisibility(View.VISIBLE);
            tvNoticeFeed.setText(String.valueOf(globalVariable.getValue(preferences,Constant.N_INVITATION)));
            tvNoticeInbox.setVisibility(View.VISIBLE);
            tvNoticeInbox.setText(String.valueOf(globalVariable.getValue(preferences,Constant.N_INVITATION)));
        }else {
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
    protected void onDestroy() {

        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();

        super.onDestroy();
    }
    private void initVariables(){
        mContext = this;
        globalVariable = new GlobalVariable();
        preferences = Utilities.getSharedPreferences(this);
        isLikeUser = false;
//        start = 0;
        offset = 0;
        strHashCode = "";
        mProgressDialog = null;
        sectionList = new ArrayList<FeedModel>();
        articleId = "";
        islike = false;
        isLast = false;
        currentPosition = 0;
        layoutManager = new CustomLayoutManager(this);
        manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

    }

    private void initUI(){

//        list = (RecyclerView) findViewById(R.id.recycler_view);
//        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mPullRefreshSearchListView = (PullToRefreshListView)findViewById(R.id.recycler_view);
        mPullRefreshSearchListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                if(!isLast){
                    new LoadFeedTask().execute();
                }
                mPullRefreshSearchListView.onRefreshComplete();


            }
        });

        listView = mPullRefreshSearchListView.getRefreshableView();

        btnToInbox = (TextView)findViewById(R.id.btn_to_inbox);
        btnToInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedViewActivity.this, InboxActivity.class);
                startActivity(intent);
            }
        });
        tvBack = (TextView)findViewById(R.id.btn_feed_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.deleteCache(mContext);
                finish();
            }
        });
        btnRefresh = (TextView)findViewById(R.id.btn_refresh_feed);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        tvNoticeFeed = (TextView)findViewById(R.id.txt_invitation_cnt_feed_view);
        tvNoticeMessage = (TextView)findViewById(R.id.txt_message_cnt_feed_view);
        tvNoticeInbox = (TextView)findViewById(R.id.txt_invitation_cnt_inbox);
        tvNoticeRequest = (TextView)findViewById(R.id.txt_request_cnt_feed_view);

        tvToCamera = (TextView)findViewById(R.id.feed_camera);
        tvToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedViewActivity.this, CaptureActivity.class);
                startActivity(intent);
            }
        });
        tvToMessage = (TextView)findViewById(R.id.feed_message);
        tvToMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedViewActivity.this, MessageActivity.class);
                startActivity(intent);
                Utilities.deleteCache(mContext);
                finish();
//                callGC(intent);
            }
        });
        tvToProfile = (TextView)findViewById(R.id.feed_profile);
        tvToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedViewActivity.this, ProfileActivity.class);
                startActivity(intent);
                Utilities.deleteCache(mContext);
                finish();
//                callGC(intent);
            }
        });
        tvToSearch = (TextView)findViewById(R.id.feed_search);
        tvToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedViewActivity.this, SearchActivity.class);
                startActivity(intent);
                Utilities.deleteCache(mContext);
                finish();
//                callGC(intent);
            }
        });
        tvBanner = (TextView)findViewById(R.id.tv_feed);

//        list.setOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
////                int i = linearLayoutManager.findLastCompletelyVisibleItemPosition();
//                int j = linearLayoutManager.findLastVisibleItemPosition();
/////////////===========================================
//                currentPosition = j;
////                list.smoothScrollToPosition(j + 1);
//                if(!sectionList.get(j).GetFeedBodyModel().GetFlag()){
//                    if(j % 3 == 0){
////                        new DownloadWebPageTask().execute(new String[]{String.valueOf(j)});
//                    }
////                    LinearLayout linearLayout = getLinearLayoutByPosition(j, list);
////                    download(j, linearLayout);
////                    sectionList.get(j).GetFeedBodyModel().SetFlag(true);
//                }
////                ImageView imageview = getImageViewByPosition(j, list);
////                imageview.setImageBitmap(sectionList.get(j).GetFeedBodyModel().GetBitmap());
//////////////////////////=====================================
//                if (j == sectionList.size() - 1) {
//                    //Its at bottom ..
//                    Log.e("ennnd", "deeeeeddd===============");
//                    if (!isLast) {
//                        Log.e("ennnd", "call__api__now");
////                        offset = limit;
//                        count ++;
//                        if (count == 4) {
//                            if(mode == 1){
//                                new LoadFeedTask2().execute();
//                            }else if(mode == 0){
//                                new LoadFeedTask().execute();
//                            }else if(mode == 2){
//                                new LoadFeedTask3().execute();
//                            }
//                            count = 0;
//
//                        }
//                    }
//                }
//            }
//        });

    }

    private void loadFeedData(){
        if (Utilities.haveNetworkConnection(FeedViewActivity.this)) {

            try{
                LoadFeedTask task = new LoadFeedTask();
                task.execute();

            }catch (Exception e){
                e.printStackTrace();
            }

        } else {
            Toast.makeText(FeedViewActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }

    }

    private void setFont(){

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "Calibri-Bold.ttf");

        tvBanner.setTypeface(custom_font);

    }

    private void setListAdapter(){
        if(sectionList.size() > 0){

//            personAdapter = new BodyAdapter(mContext, sectionList);
//            headerAdapter = new HeaderAdapter(sectionList);
//
//            top = new StickyHeadersBuilder()
//                    .setAdapter(personAdapter)
//                    .setRecyclerView(list)
//                    .setStickyHeadersAdapter(headerAdapter)
//                    .build();
//
//
//            list.setAdapter(personAdapter);
//            list.addItemDecoration(top);

            feedAdapter = new FeedAdapter(mContext, sectionList);
            listView.setAdapter(feedAdapter);
        }


    }

    private void refresh(){
        setContentView(R.layout.activity_feed_view);
        initVariables();
        initUI();
        setFont();
        loadFeedData();
    }

    public void changeLike(String articleId, int position, boolean islike){
        this.islike  = islike;
        this.articleId = articleId;
        this.position = position;
        new ChangeLikeTask().execute();

    }

    public void download(final int pos, LinearLayout linearLayout){
        String url = sectionList.get(pos).GetFeedBodyModel().GetAttachURL();
        ///get filename from url
        final String fileName = url.substring(url.lastIndexOf('/') + 1);
        ///creat download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        ////////set destinatin storage path
//        request.setDestinationInExternalPublicDir(PATH, fileName);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        final long downloadId = manager.enqueue(request);

        final LinearLayout layout = linearLayout;
        layout.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate (R.layout.custom_circle_progress, null);
        final CircleProgressView progressView = (CircleProgressView)view.findViewById(R.id.kcjCircleView);
        progressView.setValueAnimated(0, 10);
        layout.addView(view);

        try{
            new Thread(new Runnable() {
                @Override
                public void run() {

                    boolean downloading = true;

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

                        final double dl_progress = bytes_total != 0 ? (int) ((bytes_downloaded * 100l) / bytes_total) : 0;

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                int percent = (int) dl_progress;
                                progressView.setValueAnimated(percent, 50);
                                if (percent == 100) {
                                    if(sectionList.get(pos).GetFeedBodyModel().GetAttatchType().equals("1")){
                                        Bitmap bitmap = BitmapFactory.decodeFile(PATH + fileName);

                                        if(bitmap != null){
                                            sectionList.get(pos).GetFeedBodyModel().SetFlag(true);
                                            sectionList.get(pos).GetFeedBodyModel().SetBitmap(bitmap);
                                            feedAdapter.notifyDataSetChanged();

                                        }
                                    }
                                }
                            }
                        });
                        cursor.close();
                    }
                }
            }).start();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    class LoadFeedTask extends AsyncTask<String, Integer, String> {

        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {

            if(resultCode == 0){
                if(offset > 1){
                    feedAdapter.notifyDataSetChanged();
                    mPullRefreshSearchListView.onRefreshComplete();
                }else{
                    setListAdapter();
                }
            }else if(resultCode == 1) {
                Utilities.showOKDialog(mContext, responseDesc);
            }else if(resultCode == 2) {
                Utilities.showOKDialog(mContext, "Please try again later!");
            }

            hideProgress();
            this.cancel(true);
            super.onPostExecute(result);
        }


        @Override
        protected String doInBackground(String... param) {


            parameters = new ArrayList<NameValuePair>();
            String strUrl;

            strUrl = Constant.ARTICLE_INDEX;

            parameters.add(new BasicNameValuePair("index", String.valueOf(offset)));

            Log.d("LoadFeedTask", "LoadFeedTask doInBackground");
            JSONObject result =  HttpManager.getInstance().callPost(mContext, strUrl, parameters, true);

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
                        JSONArray arrResult = result.getJSONArray("articles");
                        int n = arrResult.length();
                        if(n < Constant.MAX_FEED_ARTICLE_PER_PAGE){

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
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            return null;
        }
    }
    class ChangeLikeTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            feedAdapter.notifyDataSetChanged();
            this.cancel(true);
            super.onPostExecute(result);
        }


        @Override
        protected String doInBackground(String... param) {

            parameters = new ArrayList<NameValuePair>();
            String strUrl = Constant.ARTICLE_ID_LIKE + articleId + "/like";

            parameters.add(new BasicNameValuePair("id", articleId));

            Log.d("LoadFeedTask", "LoadFeedTask doInBackground");
            JSONObject result =  HttpManager.getInstance().callPost(mContext, strUrl, parameters, true);

            if (result == null) {

                Utilities.showToast(mContext, "Plase try again...");
            }else
            {
                try {
                    if (!result.isNull("success")) {
                        responseCode = result.getString("success");

                    }
                    if (!result.isNull("error")) {
                        responseDesc = result.getString("error");
                    }
                    if (responseCode.equalsIgnoreCase("true")) {

                        String str = sectionList.get(position).GetFeedBodyModel().GetLikeCount();
                        int likeCount = Integer.parseInt(str);
                        if(islike){
                            likeCount ++;
                            sectionList.get(position).GetFeedBodyModel().SetLikeCount(String.valueOf(likeCount));
                            sectionList.get(position).GetFeedBodyModel().SetIslike("1");
                        }else {
                            likeCount --;
                            sectionList.get(position).GetFeedBodyModel().SetLikeCount(String.valueOf(likeCount));
                            sectionList.get(position).GetFeedBodyModel().SetIslike("0");
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
    class LoadFeedTask2 extends AsyncTask<String, Integer, String> {

        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(offset > 1){
                feedAdapter.notifyDataSetChanged();
            }else{
                setListAdapter();
            }

            this.cancel(true);
            super.onPostExecute(result);
        }


        @Override
        protected String doInBackground(String... param) {

            parameters = new ArrayList<NameValuePair>();

            String strUrl = Constant.ARTICLE_ID_LIKE + articleId;


            Log.d("LoadFeedTask", "LoadFeedTask doInBackground");
            JSONObject result =  HttpManager.getInstance().callPost(mContext, strUrl, null, true);

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
    class LoadFeedTask3 extends AsyncTask<String, Integer, String> {

        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {

            if(resultCode == 0){
                if(offset > 1){
                    feedAdapter.notifyDataSetChanged();
                }else{
                    setListAdapter();
                }
            }else if (resultCode == 1){
                Utilities.showOKDialog(mContext, responseDesc);
            }else if(resultCode == 2){
                Utilities.showOKDialog(mContext, "Please try again later!");
            }

            hideProgress();

            this.cancel(true);
            super.onPostExecute(result);
        }


        @Override
        protected String doInBackground(String... param) {


            parameters = new ArrayList<NameValuePair>();
            String strUrl = Constant.GET_EDIT_ARTICLES + String.valueOf(offset);

            parameters.add(new BasicNameValuePair("selected_id", articleId));

            Log.d("LoadFeedTask", "LoadFeedTask doInBackground");
            JSONObject result =  HttpManager.getInstance().callPost(mContext, strUrl, parameters, true);

            if (result == null) {
                resultCode = 11;

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
                        if(n < Constant.MAX_FEED_ARTICLE_PER_PAGE){

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
                            resultCode = -0;
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
    public synchronized void hideProgress (){
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

    public void showProgress(){
        if (mProgressDialog != null && mProgressDialog.isShowing())
            return;

        mProgressDialog = new ProgressDialog(mContext, R.style.ProgressDialogTheme);
        mProgressDialog.setCancelable(false);

        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        mProgressDialog.show();
    }


}
//    public ImageView getImageViewByPosition(int pos, RecyclerView listView) {
//        PersonAdapter.ViewHolder viewHolder = (PersonAdapter.ViewHolder) listView.findViewHolderForPosition(pos);
//        return viewHolder.imageView;
////        final int firstListItemPosition = listView.getFirstVisiblePosition();
////        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
////
////        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
////            return listView.getAdapter().getView(pos, null, listView);
////        } else {
////            final int childIndex = pos - firstListItemPosition;
////            return listView.getChildAt(childIndex);
////        }
//    }
//    private LinearLayout getLinearLayoutByPosition(int position, RecyclerView listView){
//        PersonAdapter.ViewHolder viewHolder = (PersonAdapter.ViewHolder) listView.findViewHolderForPosition(position);
//        return viewHolder.llCircleProgress;
//    }
//    private void setThumbnail(int position, ImageView imageView){
//        if(sectionList.get(position).GetFeedBodyModel().GetAttatchType().equals("1")){
////            imageLoader.displayImage(sectionList.get(position).GetFeedBodyModel().GetAttachURL(), imageView, options);
//            Bitmap bitmap =  imageLoader.loadImageSync(sectionList.get(position).GetFeedBodyModel().GetAttachURL());
//            imageView.setImageBitmap(bitmap);
//        }else {
//
//        }
//    }
//    private void getBitmap(int pos){
//        Bitmap bitmap = null;
//        if(sectionList.get(pos).GetFeedBodyModel().GetAttatchType().equals("1")){
//            bitmap =  imageLoader.loadImageSync(sectionList.get(pos).GetFeedBodyModel().GetAttachURL());
//            sectionList.get(pos).GetFeedBodyModel().SetBitmap(bitmap);
//
//        }else {
//
//        }
//    }

//    public void startProgress(final int pos) {
//    // do something long
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            for (int i = pos; i < (pos + 2); i++) {
//                final int value = i;
//                showProgress();
//                getBitmap(pos);
////                    doFakeWork(i);
////                    progress.post(new Runnable() {
////                        @Override
////                        public void run() {
////                            text.setText("Updating");
////                            progress.setProgress(value);
////                        }
////                    });
//            }
//        }
//    };
//    new Thread(runnable).start();
//}