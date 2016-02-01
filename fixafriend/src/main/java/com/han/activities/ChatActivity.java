package com.han.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.han.ImageDownloadHelper.UrlImageViewCallback;
import com.han.ImageDownloadHelper.UrlImageViewHelper;
import com.han.adapter.ChatAdapter;
import com.han.http.HttpManager;
import com.han.login.R;
import com.han.model.ChatModel;
import com.han.utils.Constant;
import com.han.utils.Utilities;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatActivity extends Activity {
    private TextView btnBack;
    private ListView lvChat;
    private PullToRefreshListView mPullRefreshSearchListView;
    private EditText etMessage;
    private TextView tvSend, tvBanner;
    private ImageView toProfile;
    private ChatAdapter mAdapter;
    private ArrayList<ChatModel> arrUserModel;

    private int offset = 0;
    private boolean islast ;
    private String userId= "";
    private String userName;
    private String message ;
    private String photoUrl;
//    private String email;
    private Context mContext;
    private ArrayList<NameValuePair> parameters;
    private ProgressDialog mProgressDialog = null;
    private String responseCode;
    private String responseDesc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initVariables();
        initUI();
        setFont();

        new LoadChatHistoryTask().execute();

    }

    private void initUI(){
        toProfile = (ImageView)findViewById(R.id.btn_chat_to_profile);
        toProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(ChatActivity.this, ProfileFriendActivity.class);
                intent.putExtra("userid", userId);
                startActivity(intent);

            }
        });
        btnBack = (TextView)findViewById(R.id.btn_chat_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPullRefreshSearchListView = (PullToRefreshListView)findViewById(R.id.lv_chat);
        mPullRefreshSearchListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                if(!islast){
                    new LoadChatHistoryTask().execute();
                }
                mPullRefreshSearchListView.onRefreshComplete();
//                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
//                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
//
//                // Update the LastUpdatedLabel
//                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
//
//                // Do work to refresh the list here.
//                new GetDataTask().execute();
            }
        });
//        mPullRefreshSearchListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
//
//            @Override
//            public void onLastItemVisible() {
//
//            }
//        });
        lvChat = mPullRefreshSearchListView.getRefreshableView();
        etMessage = (EditText)findViewById(R.id.et_message);
        tvSend = (TextView)findViewById(R.id.tv_send);
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = etMessage.getText().toString();
                if (!message.equals("")) {
                    new SendMessageTask().execute();
                    etMessage.setText("");
                    addItemBelow(message, true);
                }

            }
        });
        tvBanner = (TextView)findViewById(R.id.textView10);
    }
    private void initVariables(){
        arrUserModel = new ArrayList<ChatModel>();
        mContext = this;
        Intent intent = getIntent();
        userId = intent.getStringExtra("id");
        userName = "";
        photoUrl = "";
        islast = false;
    }

    private void setFont(){

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "Calibri.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(), "Calibri-Bold.ttf");
        tvBanner.setTypeface(custom_font_bold);
        etMessage.setTypeface(custom_font);
    }

    public String getId(){
        return userId;
    }

    private void addItemBelow(String msg, boolean flag){
        ChatModel chatModel = new ChatModel();
        SharedPreferences preferences = Utilities.getSharedPreferences(this);
        String senderId = preferences.getString("user_id", null);

        if(flag){
            chatModel.SetMessage(msg);
            chatModel.SetReceiverId(userId);
            chatModel.SetSenderId(senderId);

        }else {
            chatModel.SetMessage(msg);
            chatModel.SetReceiverId(senderId);
            chatModel.SetSenderId(userId);
        }
        arrUserModel.add(chatModel);
        mAdapter.notifyDataSetChanged();
        lvChat.setSelection(arrUserModel.size() - 1);

    }


    private void setPhoto(){
        if(!photoUrl.equals("")){

            UrlImageViewHelper.setUrlDrawable(toProfile, photoUrl, R.drawable.loading, new UrlImageViewCallback() {
                @Override
                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                    if (!loadedFromCache) {
                        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
                        scale.setDuration(300);
                        scale.setInterpolator(new OvershootInterpolator());
                        imageView.startAnimation(scale);
                    }
                }
            });
        }else {
            toProfile.setBackgroundDrawable(getResources().getDrawable(R.drawable.user_web));
        }
        if(!userName.equals("")){
            tvBanner.setText(userName);
        }
    }

    class LoadChatHistoryTask extends AsyncTask<String, Integer, String> {

        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {

            if(resultCode == 0){
                setPhoto();
                if(offset > 1){
                    mAdapter.notifyDataSetChanged();
                    mPullRefreshSearchListView.onRefreshComplete();
                }else {
                    SetChatList();
                }
            }else if(resultCode == 1){
                Utilities.showOKDialog(mContext, responseDesc);
            }

            hideProgress();
            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {


            String url = Constant.MESSAGES_HISTORY + userId + "/" + String.valueOf(offset)  + "/" + "history";

            JSONObject result =  HttpManager.getInstance().callGet(mContext, url, null, true);

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


                        photoUrl = result.getString("selected_user_photo");
                        userName = result.getString("selected_user_name");

                        JSONArray jsonArray= result.getJSONArray("history");

                        int n = jsonArray.length();
                        offset ++;
                        if(n < Constant.MAX_USER_AMOUNT_PER_PAGE){
                            islast = true;
                        }

                        ArrayList<ChatModel> buffer = new ArrayList<ChatModel>();


                        for(int i = 0; i < n; i ++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                            ChatModel chatModel = new ChatModel();
                            chatModel.SetMessage(obj.getString("message"));
                            chatModel.SetTime(obj.getInt("send_date"));
                            chatModel.SetSenderId(obj.getString("sender_id"));
                            chatModel.SetReceiverId(obj.getString("receiver_id"));
                            chatModel.SetPhotoURL(photoUrl);
                            buffer.add(chatModel);
                        }
                        for(int j = buffer.size() - 1; j >= 0 ; j --) {
                            arrUserModel.add(0, buffer.get(j));
                        }
                        resultCode = 0;

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
    public void inverseData(ArrayList<ChatModel> bufferData){

    }

    class SendMessageTask extends AsyncTask<String, Integer, String> {

        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
//            SetChatList();
            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {


            parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("selected_id", userId));
            parameters.add(new BasicNameValuePair("message",message));
            String url = Constant.MESSAGES_HISTORY  + userId + "/send";

            JSONObject result =  HttpManager.getInstance().callPost(mContext, url, parameters, true);

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

                        Utilities.showToast(mContext, "Message send successfully!");
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
    ///for test////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void SetChatList(){
//        arrUserModel = getChatItem();
        mAdapter = new ChatAdapter(mContext, arrUserModel);
        lvChat.setAdapter(mAdapter);
        lvChat.setSelection(arrUserModel.size() -1);
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
    ////////register notificatin receiver
    //register your activity onResume()

    @Override
    public void onResume() {
    super.onResume();
    this.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));

        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
    }


    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String message = intent.getStringExtra("message");
            String name = intent.getStringExtra("name");
            String photo = intent.getStringExtra("photo");
            addItemBelow(message, false);
            //do other stuff here
        }
    };
    public String getEmoji(String str) {
        String strKey = "\\ud83d";
        while(str.contains(strKey)) {

            int start = str.indexOf("\\ud83d");
            String str3 = str.substring(start , start + 12);
            ///get emoji from string
            String str4 = getUnicode(str3);
            //replace string with emoji
            String result = str.replace(str3, str4);
            str = result;

        }
        return str;
    }
    public String getUnicode(String myString) {
        String str = myString.split(" ")[0];
        str = str.replace("\\","");
        String[] arr = str.split("u");
        String text = "";
        for(int i = 1; i < arr.length; i++){
            int hexVal = Integer.parseInt(arr[i], 16);
            text += (char)hexVal;
        }
        return text;
    }
    public String getEmoji1(String str) {
        String strKey = "\\\\ud83d";
        while(str.contains(strKey)) {

            int start = str.indexOf(strKey);
            String str3 = str.substring(start , start + 14);
            ///get emoji from string
            String str4 = getUnicode1(str3);
            //replace string with emoji
            String result = str.replace(str3, str4);
            str = result;

        }
        return str;
    }
    public String getUnicode1(String myString) {
        String str = myString.split(" ")[0];
        str = str.replace("\\\\","");
        String[] arr = str.split("u");
        String text = "";
        for(int i = 1; i < arr.length; i++){
            int hexVal = Integer.parseInt(arr[i], 16);
            text += (char)hexVal;
        }
        return text;
    }

}
