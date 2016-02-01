package com.han.main;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.han.activities.ChatActivity;
import com.han.adapter.MessageAdapter;
import com.han.http.HttpManager;
import com.han.login.R;
import com.han.model.MessageModel;
import com.han.utils.Constant;
import com.han.utils.GlobalVariable;
import com.han.utils.Utilities;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    private TextView btnRefreshMessage;
    private ListView lvMessage;
    private PullToRefreshListView mPullRefreshSearchListView;
    private TextView tvToFeed, tvToCamera, tvToSearch, tvToProfile;
    private TextView tvMessage;
    private TextView tvNoticeInvite, tvNoticeMessage, tvNoticeRequest;
    private MessageAdapter mAdapter;
    private ArrayList<MessageModel> arrMessageModel;

    private Context mContext;
    private ProgressDialog mProgressDialog = null;

    private String responseCode;
    private String responseDesc;

    public boolean isLast ;
    private int count;
    private int offset;

    GlobalVariable globalVariable ;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initVariables();
        initUI();
        setFont();
        messageTask();
//        SetSearchList();


    }
    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
        ////show notification badge
        if(globalVariable.getValue(preferences, Constant.N_INVITATION) > 0){
            tvNoticeInvite.setVisibility(View.VISIBLE);
            tvNoticeInvite.setText(String.valueOf(globalVariable.getValue(preferences, Constant.N_INVITATION)));

        }
        if(globalVariable.getValue(preferences, Constant.N_MESSAGE) > 0){
            tvNoticeMessage.setVisibility(View.VISIBLE);
            tvNoticeMessage.setText(String.valueOf(globalVariable.getValue(preferences, Constant.N_MESSAGE)));
        }else {
        }
        if(globalVariable.getValue(preferences, Constant.N_REQUEST) > 0){
            tvNoticeRequest.setVisibility(View.VISIBLE);
            tvNoticeRequest.setText(String.valueOf(globalVariable.getValue(preferences, Constant.N_REQUEST)));
        }

    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();


    }
    private void callGC(Intent intent) {
        // For stack clear
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // For calling GC
        System.runFinalization();
        System.exit(0);
    }
    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            String name = intent.getStringExtra("name");
            String photo = intent.getStringExtra("photo");
            //do other stuff here
        }
    };
    private void messageTask(){
        if (Utilities.haveNetworkConnection(MessageActivity.this)) {

            try{
                new MessageTask().execute();
            }catch (Exception e){
                e.printStackTrace();
            }

        } else {
            Toast.makeText(MessageActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }

    }
    private void initUI(){

        tvNoticeInvite = (TextView)findViewById(R.id.txt_invitation_cnt_message);
        tvNoticeMessage = (TextView)findViewById(R.id.txt_message_cnt_message);
        tvNoticeRequest = (TextView)findViewById(R.id.txt_request_cnt_message);

        btnRefreshMessage = (TextView)findViewById(R.id.btn_refresh_message);
        btnRefreshMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        mPullRefreshSearchListView = (PullToRefreshListView)findViewById(R.id.lv_message);

        mPullRefreshSearchListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                if(!isLast){
                    new MessageTask().execute();
                }

            }
        });
        lvMessage = mPullRefreshSearchListView.getRefreshableView();
        lvMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int notificationNum = Integer.parseInt(arrMessageModel.get(position - 1).GetUnreadCount());
                if(notificationNum > 0){
                    for(int i = 0; i < notificationNum; i++) {
                        globalVariable.decreaseValue(preferences, Constant.N_MESSAGE);
                    }
                }
                Intent intent = new Intent(MessageActivity.this, ChatActivity.class);
                intent.putExtra("id", arrMessageModel.get(position - 1).GetUserId());
//                intent.putExtra("email", mm.GetEmail());
                startActivity(intent);

            }
        });
        tvToCamera = (TextView)findViewById(R.id.message_camera);
        tvToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, CaptureActivity.class);
                startActivity(intent);
            }
        });
        tvToFeed = (TextView)findViewById(R.id.message_feed);
        tvToFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, FeedViewActivity.class);
                startActivity(intent);
                finish();
//                callGC(intent);
            }
        });
        tvToSearch = (TextView)findViewById(R.id.message_search);
        tvToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
//                callGC(intent);
            }
        });
        tvToProfile = (TextView)findViewById(R.id.message_profile);
        tvToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
//                callGC(intent);

            }
        });
        tvMessage = (TextView)findViewById(R.id.tv_message);

    }
    public void initVariables(){
        arrMessageModel = new ArrayList<MessageModel>();
        mContext = this;
        count = 0;
        isLast = false;
        offset = 0;

        globalVariable = new GlobalVariable();
        preferences = Utilities.getSharedPreferences(this);
    }
    private void setFont(){

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "Calibri.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "Calibri-Bold.ttf");

        tvMessage.setTypeface(custom_font_bold);

    }
    private void refresh(){
        initVariables();
        messageTask();
    }
    class MessageTask extends AsyncTask<String, Integer, String> {

        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {

            if(resultCode == 1){
                Utilities.showOKDialog(mContext, responseDesc);
            }
            if(offset > 1){
                mAdapter.notifyDataSetChanged();
                mPullRefreshSearchListView.onRefreshComplete();
            }else{
                if(arrMessageModel.size() > 0){
                    setSearchList();
                }
            }
            hideProgress();
            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {

            JSONObject result =  HttpManager.getInstance().callGet(mContext, Constant.MESSAGES_USERS, null, true);

            if (result == null) {
                int resultCode = 2;
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
                        if(n < Constant.MAX_USER_AMOUNT_PER_PAGE){

                            isLast = true;
                        }else{
                            offset ++;
                            isLast = false;
                        }
                        for (int i = 0; i < n; i ++){
                            JSONObject obj = jsonArray.getJSONObject(i);

                            MessageModel messageModel = new MessageModel();

                            messageModel.SetEmail(obj.getString("email"));
                            messageModel.SetName(obj.getString("name"));
                            messageModel.SetUserId(obj.getString("user_id"));
                            messageModel.SetUnreadCount(obj.getString("unread_count"));
                            messageModel.SetMessage(obj.getString("message"));
                            messageModel.SetPhotoURL(obj.getString("photo"));

                            arrMessageModel.add(messageModel);
                        }

                        int resultCode = 0;
                    } else {
                        int resultCode = 1;
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            return null;
        }
    }

    public void setSearchList(){
//        arrUserModel = getFeedItem();
        mAdapter = new MessageAdapter(mContext, arrMessageModel);
        lvMessage.setAdapter(mAdapter);
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
