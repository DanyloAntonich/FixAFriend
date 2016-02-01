package com.han.activities;

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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.han.http.HttpManager;
import com.han.sticky_adapter.InboxAdapter;
import com.han.sticky_adapter.InboxHeaderAdapter;
import com.han.login.R;
import com.han.main.CaptureActivity;
import com.han.main.MessageActivity;
import com.han.main.ProfileActivity;
import com.han.main.SearchActivity;
import com.han.model.InboxModel;
import com.han.utils.Constant;
import com.han.utils.GlobalVariable;
import com.han.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import at.grabner.circleprogress.CircleProgressView;

public class InboxActivity extends AppCompatActivity  {

    private  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator ;

    private TextView btnToFeed, btnRefresh, tvAlert;
    private TextView tvToSearch, tvToMessage, tvToCamera, tvToProfile, tvBanner;
    private TextView tvNoticeInvite, tvNoticeMessage, tvNoticeRequest;

    int offset = 0, count = 0;
    public boolean isLast = true;

    public boolean isLikeUser;

    private DrawerLayout drawerLayout;

    // LayoutManager of RecyclerView
    private LinearLayoutManager mLayoutManager;

    //    public SessionManager session;
    private RelativeLayout relListViewParent;


    //Sticky Header....
    private RecyclerView list;
    private StickyHeadersItemDecoration top;
    private InboxAdapter inboxAdapter;
    private InboxHeaderAdapter inboxHeaderAdapter;
    protected int start = 0;
    //////////////////////////////////////////////////////////////////////////////
    ArrayList<InboxModel> arrInboxModel;

    ////////////////////////////////////////////////
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private String responseCode;
    private String responseDesc;
    //////////////////////////////////////////

    DownloadManager manager ;
    GlobalVariable globalVariable;
    SharedPreferences preferences;
//    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        initVariables();
        initUI();
        setFont();
        loadInboxTask();


        if(globalVariable.getValue(preferences, Constant.N_INVITATION) > 0){
            int notifyNum = globalVariable.getValue(preferences, Constant.N_INVITATION);
            if(notifyNum > arrInboxModel.size()){
                while (arrInboxModel.size() > notifyNum){
                    globalVariable.decreaseValue(preferences, Constant.N_INVITATION);
                }

            }
            if(arrInboxModel.size() != 0){
                tvNoticeInvite.setVisibility(View.VISIBLE);
                tvNoticeInvite.setText(String.valueOf(globalVariable.getValue(preferences, Constant.N_INVITATION)));

            }

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
    private void loadInboxTask(){
        if (Utilities.haveNetworkConnection(InboxActivity.this)) {

            try{
                LoadInboxTask task = new LoadInboxTask();
                task.execute();
            }catch (Exception e){
                e.printStackTrace();
            }

        } else {
            Toast.makeText(InboxActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }

    }
    private void initVariables(){
        isLikeUser = false;
        mContext = this;
        offset = 0;
        arrInboxModel = new ArrayList<InboxModel>();
        manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        globalVariable = new GlobalVariable();
        preferences = Utilities.getSharedPreferences(this);
    }
    private void initUI(){

        tvAlert = (TextView)findViewById(R.id.tv_inbox_alert);
        tvNoticeMessage = (TextView)findViewById(R.id.badge_inbox_message);
        tvNoticeRequest = (TextView)findViewById(R.id.badge_inbox_request);
        tvNoticeInvite = (TextView)findViewById(R.id.badge_inbox_invitation);
//        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_inbox);
        list = (RecyclerView) findViewById(R.id.inbox_list);
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        btnToFeed = (TextView)findViewById(R.id.btn_to_feed);
        btnToFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnRefresh = (TextView)findViewById(R.id.btn_refresh_inbox);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshView();
            }
        });

        tvToCamera = (TextView)findViewById(R.id.inbox_camera);
        tvToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InboxActivity.this, CaptureActivity.class);
                startActivity(intent);

            }
        });
        tvToMessage = (TextView)findViewById(R.id.inbox_message);
        tvToMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InboxActivity.this, MessageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvToProfile = (TextView)findViewById(R.id.inbox_profile);
        tvToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InboxActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvToSearch = (TextView)findViewById(R.id.inbox_search);
        tvToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InboxActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvBanner = (TextView)findViewById(R.id.tv_inbox_title);

        list.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int j = linearLayoutManager.findLastVisibleItemPosition();
                if (j == arrInboxModel.size() - 1) {
                    //Its at bottom ..
                    Log.e("ennnd", "deeeeeddd===============");
                    if (!isLast) {
                        Log.e("ennnd", "call__api__now");
//                        offset = limit;
                        count ++;
                        if (count == 4) {

                            LoadInboxTask task = new LoadInboxTask();
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
        if(arrInboxModel.size() > 0){

            inboxAdapter =  new InboxAdapter(mContext, arrInboxModel);
            inboxHeaderAdapter = new InboxHeaderAdapter(arrInboxModel) ;

            top = new StickyHeadersBuilder()
                    .setAdapter(inboxAdapter)
                    .setRecyclerView(list)
                    .setStickyHeadersAdapter(inboxHeaderAdapter)
                    .build();


            list.setAdapter(inboxAdapter);
            list.addItemDecoration(top);
        }
        }
    private void refreshView(){
//        list.removeAllViews();
        setContentView(R.layout.activity_inbox);
        initVariables();
        initUI();
        setFont();
        if (Utilities.haveNetworkConnection(InboxActivity.this)) {

            try{
                LoadInboxTask task = new LoadInboxTask();
                task.execute();
            }catch (Exception e){
                e.printStackTrace();
            }

        } else {
            Toast.makeText(InboxActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }
    ////////////////////////////////////////////////////////////////////////////
    public void download(final int pos, RelativeLayout linearLayout){
        String url = arrInboxModel.get(pos).GetArticleAttach();
        ///get filename from url
        final String fileName = url.substring(url.lastIndexOf('/') + 1);
        ///creat download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        ////////set destinatin storage path
//        request.setDestinationInExternalPublicDir(PATH, fileName);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        final long downloadId = manager.enqueue(request);
        final RelativeLayout layout = linearLayout;
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
                                Bitmap bitmap = null;
                                if (percent == 100) {
                                    if(arrInboxModel.get(pos).GetArticleType().equals("1")){
                                        bitmap = BitmapFactory.decodeFile(PATH + fileName);

                                        if(bitmap != null){
                                            arrInboxModel.get(pos).SetBitmap(bitmap);
                                            inboxAdapter.notifyDataSetChanged();

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
//    @Override
//    public void onRefresh() {
////        swipeRefreshLayout.setRefreshing(true);
////
////        if(mode){
////            new LoadFeedTask2().execute();
////        }else {
////            new LoadFeedTask().execute();
////        }
//        swipeRefreshLayout.setRefreshing(false);
//    }
    class LoadInboxTask extends AsyncTask<String, Integer, String> {


    int resultCode  = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {


            if(offset > 1){
                inboxAdapter.notifyDataSetChanged();
            }else{
                if(arrInboxModel.size() > 0){
                    setListAdapter();
                }else {
                    tvAlert.setVisibility(View.VISIBLE);
                }

            }
            hideProgress();
            if(resultCode == 1){
                Utilities.showOKDialog(mContext, Constant.ALERT1);
            }
//            swipeRefreshLayout.setRefreshing(false);
            this.cancel(true);
            super.onPostExecute(result);
        }


        @Override
        protected String doInBackground(String... param) {

            Log.d("LoadFeedTask", "LoadFeedTask doInBackground");
            JSONObject result =  HttpManager.getInstance().callGet(mContext, (Constant.GET_INVITATION_ARTICLE + String.valueOf(offset)), null, true);

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
                        JSONArray arrResult = result.getJSONArray("articles");
                        int n = arrResult.length();
                        offset ++;
                        if(n < Constant.MAX_ARTICLE_AMOUNT_PER_PAGE){

                            isLast = true;
                        }else{

                            isLast = false;
                        }

                        for(int i = 0; i < n; i ++){
                            InboxModel im = new InboxModel();
                            JSONObject obj = arrResult.getJSONObject(i);

                            im.SetSenderId(obj.getString("sender_id"));
                            im.SetName(obj.getString("name"));
                            im.SetSenderEmail(obj.getString("sender_email"));
                            im.SetArticleType(obj.getString("article_type"));
                            im.SetPhotoURL(obj.getString("photo"));
                            im.SetInvitationId(obj.getString("invitation_id"));
                            im.SetArticleAttach(obj.getString("article_attach"));
                            im.SetLimite(obj.getInt("limit"));
                            im.SetSendDate(obj.getInt("send_date"));
                            arrInboxModel.add(im);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        switch(resultCode){
            case 100:
                refreshView();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
