package com.han.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.han.http.HttpManager;
import com.han.main.FeedViewActivity;
import com.han.model.LoginModel;
import com.han.utils.Constant;
import com.han.utils.GlobalVariable;
import com.han.utils.Utilities;

import io.fabric.sdk.android.Fabric;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public  class LoginAcitivity extends Activity implements View.OnClickListener  {

    public EditText etEmail, etPassword;
    private Button  btnLogin, btnSignup;
    private ImageView btnForgotPassword;
    private TextView tvBanner, tvFacebook;
    private RelativeLayout btnFacebook;

    private ArrayList<NameValuePair> loginParam;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private String responseCode;
    private String responseDesc;
    private SharedPreferences objSharedPref;

    private String strEmail;
    private String strPassword;
    private ProfileTracker profileTacker;
    private LoginModel loginModel;
//////////////fb login

    CallbackManager callbackManager;
    AccessToken fbAccessToken ;

    GlobalVariable globalVariable ;

    ///////////////////gcm
     private String regId;
    private static final String PROPERTY_REG_ID = "PROPERTY_REG_ID";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1;
    private static final String SENDER_ID = "433675293423";///get from google
    GoogleCloudMessaging gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login_acitivity);

        initVariables();
        initUI();
        setFont();
        register();

//        printKeyHash(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoLogin();
    }

    private void initVariables(){
        mContext = this;
        objSharedPref = Utilities.getSharedPreferences(mContext);
        globalVariable = new GlobalVariable();
//////////////////////////////////FACEBOOK LOGIN==start
        FacebookSdk.sdkInitialize(getApplicationContext());
//get current token
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                Profile.fetchProfileForCurrentAccessToken();
                final LoginResult result = loginResult;

                fbAccessToken = loginResult.getAccessToken();

                Log.d("username===============", loginResult.toString());
                Log.d("success", "==========================");

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

                                Log.v("result======", graphResponse.toString());
                                try {
                                    ///get facebook profile data
                                    String email = jsonObject.getString("email");
                                    String gender = jsonObject.getString("gender");
                                    int nGender = 1;
                                    if (gender.equals("female")) {
                                        nGender = 2;
                                    }
                                    String name = jsonObject.getString("name");
                                    String id = jsonObject.getString("id");
                                    String photo = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");
                                    String access_token = loginResult.getAccessToken().toString();

                                    AccessToken currentAccestoken = AccessToken.getCurrentAccessToken();
                                    access_token = currentAccestoken.getToken();
                                    // save fb profile data as preference
                                    SharedPreferences.Editor edit = objSharedPref.edit();

                                    edit.putString("fb_access_token", access_token);
                                    edit.putString("fb_id", id);
                                    edit.putString("fb_name", name);
                                    edit.putString("fb_photo", photo);
                                    edit.putString("fb_email", email);
                                    edit.putString("fb_gender", String.valueOf(nGender));
                                    edit.commit();

                                    setFBLoginParam();

                                } catch (JSONException e) {
                                    Utilities.showToast(mContext, "Facebook Login failed!");
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday,first_name,last_name,age_range,picture.type(normal)");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
//                Utilitie.showToast(mContext, "Facebook login failed!");
                AccessToken.setCurrentAccessToken(null);
            }

            @Override
            public void onError(FacebookException e) {

                Utilities.showToast(mContext, "Your Facebook information is incorrect!");
                AccessToken.setCurrentAccessToken(null);
            }
        });

//        profileTacker = new ProfileTracker() {
//            @Override
//            protected void onCurrentProfileChanged(Profile profile, Profile profile1) {
//
//                AccessToken currentAccestoken = AccessToken.getCurrentAccessToken();
//                Log.d("currentAccesstoken", currentAccestoken.toString());
//                Log.d("currentAccesstoken", currentAccestoken.getToken());
//                if (currentAccestoken != null && profile1 != null) {
////                    UserInfo user = new UserInfo(profile1.getName(), currentAccestoken);
////                    UserInfo.put(user);
////
//                    Log.d("username===============", profile1.getName());
//                    Log.d("username===============", profile1.toString());
//
//                    System.out.println(profile1);
//                }
//            }
//        };
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
//        facebook.authorizeCallback(requestCode, resultCode, data);
    }
    public void loginToFacebook() {
        if (Utilities.haveNetworkConnection(mContext)) {
//            UserInfo user = new UserInfo();
//                user.clear();
//            if (user.get() == null || user.get().getAccessToken() == null) {
            String accessToken = objSharedPref.getString("fb_access_token", "");
            if(accessToken.equals("")){
                try{
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile, email, user_birthday, user_photos"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                setFBLoginParam();
            }
        } else {

            Utilities.showOKDialog(mContext, "No internet access");
        }
//
    }
    private void setFBLoginParam(){
        //set login parameters
        loginParam = new ArrayList<NameValuePair>();
        loginParam.add(new BasicNameValuePair("email", objSharedPref.getString("fb_email", "")));
        loginParam.add(new BasicNameValuePair("gender", objSharedPref.getString("fb_gender", "")));
        loginParam.add(new BasicNameValuePair("name", objSharedPref.getString("fb_name", "")));
        loginParam.add(new BasicNameValuePair("facebookid", objSharedPref.getString("fb_id", "")));
        loginParam.add(new BasicNameValuePair("photo", objSharedPref.getString("fb_photo", "")));
        loginParam.add(new BasicNameValuePair("access_token", objSharedPref.getString("fb_access_token", "")));
        loginParam.add(new BasicNameValuePair("device_id", Constant.DEVICE_ID));
        loginParam.add(new BasicNameValuePair("device_type", Constant.DEVICE_TYPE));


        new FacebookLoginTask().execute();
    }

    ///////////////////////////FACEBOOK LOGIN== end

    private void initUI(){

        tvFacebook = (TextView)findViewById(R.id.tv_facebook_login);
        etEmail = (EditText)findViewById(R.id.et_login_email);
        etPassword = (EditText)findViewById(R.id.et_login_password);

        btnLogin = (Button)findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        btnSignup = (Button)findViewById(R.id.btn_signup);
        btnSignup.setOnClickListener(this);
        btnFacebook = (RelativeLayout)findViewById(R.id.btn_fb);
        btnFacebook.setOnClickListener(this);
        btnForgotPassword = (ImageView)findViewById(R.id.btn_forgot_password);
        btnForgotPassword.setOnClickListener(this);

        tvBanner = (TextView)findViewById(R.id.textView);
        /////////////////for test/////////////////////////////////////////////////

    }
    private void setFont(){

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "Calibri.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "Calibri-Bold.ttf");
        tvBanner.setTypeface(custom_font_bold);
        etEmail.setTypeface(custom_font);
        etPassword.setTypeface(custom_font);
        tvFacebook.setTypeface(custom_font_bold);
        btnLogin.setTypeface(custom_font_bold);
        btnSignup.setTypeface(custom_font_bold);
    }
    private void autoLogin(){
        String login_mode = objSharedPref.getString("login_mode", null);
        if(login_mode != null){
            if(login_mode.equals("email")){
                strPassword = objSharedPref.getString("password", null);
                strEmail = objSharedPref.getString("email", null);
                new LoginTask().execute();
            }else if(login_mode.equals("facebook")){
                setFBLoginParam();
            }

        }
    }
    @Override
    public void onClick(View v) {
        if (v == btnLogin){
            strEmail = etEmail.getText().toString();
            strPassword = etPassword.getText().toString();
            if(TextUtils.isEmpty(strEmail)){
                Utilities.showToast(mContext, "Please input email address!");
                return;

            }else if(!isEmailValid(strEmail)){
                Utilities.showToast(mContext, "Please input valid email address!");
                return;
            }else if(TextUtils.isEmpty(strPassword)){
                Utilities.showToast(mContext, "Please input password.");
                return;

            }else{
//                new LoginTask().execute();
            }
//            strPassword = "123456";
//            strEmail = "qingqing@gmail.com";
//            strPassword = "q";
//            strEmail = "ggolggoli815@gmail.com";
//            final Intent mainintent= new Intent(mContext, CaptureActivity.class);
//            startActivity(mainintent);
            if (Utilities.haveNetworkConnection(LoginAcitivity.this)) {
//                globalVariable.initValues();
                new LoginTask().execute();
            } else {
                Toast.makeText(LoginAcitivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            }


        }else if(v == btnSignup){
            final Intent intent = new Intent(LoginAcitivity.this, SignupAcitivity.class);
            startActivity(intent);
        }else if(v == btnForgotPassword){
            final Intent intent = new Intent(LoginAcitivity.this, ForgotPassword.class);
            startActivity(intent);
        }else if(v == btnFacebook){
            loginToFacebook();
        }
    }
    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    class FacebookLoginTask extends AsyncTask<String, Integer, String> {

        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(resultCode == 1){
                Utilities.showToast(mContext, "'" + strEmail + "' " + responseDesc);
            }else if(resultCode == 2){
                Utilities.showToast(mContext, responseDesc);
            }
            this.cancel(true);
            super.onPostExecute(result);
        }


        @Override
        protected String doInBackground(String... param) {

            JSONObject result = null;
            try{
                result =  HttpManager.getInstance().callPost(mContext, Constant.FACEBOOK_LOGIN, loginParam, false);
            }catch (Exception e){
                e.printStackTrace();
            }


            if (result == null) {
                resultCode = 3;

            }else
            {
                try
                {
                    if (!result.isNull("success")) {
                        responseCode = result.getString("success");

                    }
                    if (!result.isNull("error")) {
                        responseDesc = result.getString("error");
                        resultCode = 1;
                    }
                    if (responseCode.equalsIgnoreCase("true")) {

                        JSONObject obj = result.getJSONObject("userinfo");
                        loginModel = new LoginModel();
                        loginModel.SetEmail(obj.getString("email"));
                        loginModel.SetPassword(obj.getString("password"));
                        loginModel.SetLoginMode("facebook");
                        loginModel.SetAboutMe(obj.getString("about_me"));
                        loginModel.SetPhotoUrl(obj.getString("photo"));
                        loginModel.SetName(obj.getString("name"));
                        loginModel.SetId(obj.getString("id"));
                        loginModel.SetFbId(obj.getString("fb_id"));
                        loginModel.SetPurchased(obj.getString("purchased"));
                        loginModel.SetAccessToken(obj.getString("access_token"));
                        loginModel.SetIsRegistered(obj.getString("isRegistered"));

                        SharedPreferences.Editor edit = objSharedPref.edit();

                        edit.putString("access-token", loginModel.GetAccessToken());

                        edit.putString("longitude", String.valueOf(Constant.longitude));
                        edit.putString("latitude", String.valueOf(Constant.latitude));
                        edit.putString("user_id", obj.getString("id"));
                        edit.putString("user_name", obj.getString("name"));
                        edit.putString("user_photo", obj.getString("photo"));
                        edit.putString("password", strPassword);
                        edit.putString("email", obj.getString("email"));
                        edit.putString("fb_id", obj.getString("fb_id"));
                        edit.putString("isRegistered", obj.getString("isRegistered"));
                        edit.putString("deviceType", obj.getString("deviceType"));
                        edit.putString("status", obj.getString("status"));
                        edit.putString("purchased", obj.getString("purchased"));
                        edit.putString("login_mode", "facebook");
                        edit.commit();

                        final Intent mainintent= new Intent(mContext, FeedViewActivity.class);
                        startActivity(mainintent);
                    } else {
                        resultCode = 2;
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        profileTacker.stopTracking();
    }
    class LoginTask extends AsyncTask<String, Integer, String> {

        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();

            if(resultCode == 1){
                Utilities.showToast(mContext, "'" + strEmail + "' " + responseDesc);
            }else if(resultCode == 2){
                Utilities.showToast(mContext, "Access denied!");
            }
            this.cancel(true);
            super.onPostExecute(result);
            if(resultCode == 0){
                new GetAllNotificationTask().execute();
            }
        }


        @Override
        protected String doInBackground(String... param) {

            loginParam = new ArrayList<NameValuePair>();
            loginParam.add(new BasicNameValuePair("email", strEmail));
            loginParam.add(new BasicNameValuePair("password", strPassword));
            ////////////////for test

            loginParam.add(new BasicNameValuePair("device_id", regId));
            loginParam.add(new BasicNameValuePair("device_type", Constant.DEVICE_TYPE));
            JSONObject result =  HttpManager.getInstance().callPost(mContext, Constant.LOG_IN, loginParam, false);

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

                        JSONObject obj = result.getJSONObject("userinfo");
                        loginModel = new LoginModel();
                        loginModel.SetEmail(strEmail);
                        loginModel.SetPassword(strPassword);
                        loginModel.SetLoginMode("email");
                        loginModel.SetAboutMe(obj.getString("about_me"));
                        loginModel.SetPhotoUrl(obj.getString("photo"));
                        loginModel.SetName(obj.getString("name"));
                        loginModel.SetId(obj.getString("id"));
                        loginModel.SetPurchased(obj.getString("purchased"));
                        loginModel.SetAccessToken(obj.getString("access_token"));
                        loginModel.SetIsRegistered(obj.getString("isRegistered"));

                        SharedPreferences.Editor edit = objSharedPref.edit();

                        edit.putString("access-token", loginModel.GetAccessToken());

                        edit.putString("longitude", String.valueOf(Constant.longitude));
                        edit.putString("latitude", String.valueOf(Constant.latitude));
                        edit.putString("user_id", obj.getString("id"));
                        edit.putString("user_name", obj.getString("name"));
                        edit.putString("user_photo", obj.getString("photo"));
                        edit.putString("password", strPassword);
                        edit.putString("email", obj.getString("email"));
                        edit.putString("fb_id", obj.getString("fb_id"));
                        edit.putString("isRegistered", obj.getString("isRegistered"));
                        edit.putString("deviceType", obj.getString("deviceType"));
                        edit.putString("status", obj.getString("status"));
                        edit.putString("purchased", obj.getString("purchased"));
                        edit.putString("login_mode", "email");
//                        edit.putBoolean(Utils.ISREMEMBER, isAgreeTerms);
                        edit.commit();

//                        final Intent mainintent= new Intent(mContext, FeedViewActivity.class);
//                        startActivity(mainintent);
                        resultCode = 0;

                        ///

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
    class GetAllNotificationTask extends AsyncTask<String, Integer, String> {

        int resultCode = -1;
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();

            if(resultCode == 1){
                Utilities.showToast(mContext, "'" + strEmail + "' " + responseDesc);
            }else if(resultCode == 2){
                Utilities.showToast(mContext, "Please try again later!");
            }
            this.cancel(true);
            super.onPostExecute(result);
        }


        @Override
        protected String doInBackground(String... param) {


            JSONObject result =  HttpManager.getInstance().callGet(mContext, Constant.NOTIFICATION, null, true);

            if (result == null) {
                resultCode = 2;

            }else
            {
                try
                {
                    if (!result.isNull("success")) {
                        responseCode = result.getString("success");
                        resultCode = 1;
                    }
                    if (!result.isNull("error")) {
                        responseDesc = result.getString("error");
                    }
                    if (responseCode.equalsIgnoreCase("true")) {


                        int invitation  = Integer.parseInt(result.getString("new_invitation"));
                        int message  = Integer.parseInt(result.getString("new_message"));
                        int request  = Integer.parseInt(result.getString("new_request"));
                        int total = invitation + message + request;

                        globalVariable.setValue(objSharedPref, Constant.N_INVITATION, invitation);
                        globalVariable.setValue(objSharedPref, Constant.N_MESSAGE, message);
                        globalVariable.setValue(objSharedPref, Constant.N_REQUEST, request);
                        globalVariable.setValue(objSharedPref, "total_notification", total);

                        final Intent mainintent= new Intent(mContext, FeedViewActivity.class);
                        startActivity(mainintent);

                        resultCode = 0;

                        ///

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



//    public void getProfileInformation() {
//        mAsyncRunner.request("me", new AsyncFacebookRunner.RequestListener() {
//            @Override
//            public void onComplete(String response, Object state) {
//
//                String json = response;
//                try {
//                    // Facebook Profile JSON data
//                    JSONObject profile = new JSONObject(json);
//
//                    // getting name of the user
//                    final String name = profile.getString("name");
//                    final String first_name = profile.getString("first_name");
//                    final String last_name = profile.getString("last_name");
//                    final String link = profile.getString("link");
//
//                    // getting email of the user
//                    final String email = profile.getString("email");
//                    final String facebookId = profile.getString("id");
//
//                    String facebook_photo = "https://graph.facebook.com/" + facebookId + "/picture?type=large";
//
//                    List<NameValuePair> params = new ArrayList<NameValuePair>();
//                    params.add(new BasicNameValuePair("username", name));
//
//                    params.add(new BasicNameValuePair("email", email));
//                    params.add(new BasicNameValuePair("photo", facebook_photo));
////			        params.add(new BasicNameValuePair("deviceid", regID));
//
////			    	JSONObject result =  HttpManager.getInstance().callPost(getBaseContext(), "account/facebook_login", params, false);
////
////			    	try {
////						response = result.getString("success");
////
////						if(response.equals("true")) {
////
////							JSONObject user_info = result.getJSONObject("account_info");
////
////							SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
////			                SharedPreferences.Editor e = prefs.edit();
////
////							e.putString("Access-Token", user_info.getString("Access-Token"));
////							e.putString("Device-Id", user_info.getString("Device-Id"));
////							e.putString("user_id", user_info.getString("user_id"));
////							e.putString("username", user_info.getString("username"));
////							e.putString("photo", user_info.getString("photo"));
////							e.commit();
////
////							startActivity(new Intent(mContext, MainActivity.class));
////							finish();
////						}
////					} catch (JSONException e) {
////						// TODO Auto-generated catch block
////						e.printStackTrace();
////					}
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onIOException(IOException e, Object state) {
//            }
//
//            @Override
//            public void onFileNotFoundException(FileNotFoundException e,
//                                                Object state) {
//            }
//
//            @Override
//            public void onMalformedURLException(MalformedURLException e,
//                                                Object state) {
//            }
//
//            @Override
//            public void onFacebookError(FacebookError e, Object state) {
//            }
//        });
//    }
    //for gcm register
    private void register() {
        if (checkPlayServices()) {
//            GCMRegistrar.checkManifest(this);
            gcm = GoogleCloudMessaging.getInstance(this);
            try {
                regId = getRegistrationId(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (regId.isEmpty()) {
                registerInBackground();
            } else {
//                Utilitie.showToast("Registration ID already exists: " + regId);
            }
        } else {
//            Log.e(TAG, "No valid Google Play Services APK found.");
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
//                Log.e(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) throws Exception {
        final SharedPreferences prefs = Utilities.getSharedPreferences(this);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }

        return registrationId;
    }
////get gcm register key
    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(LoginAcitivity.this);
                    }
                    regId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID: " + regId;

                    sendRegistrationId(regId);

                    storeRegistrationId(LoginAcitivity.this, regId);
//                    Log.i(TAG, msg);
                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();
//                    Log.e(TAG, msg);
                }
                return msg;
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationId(String regId) {

    }

    private void storeRegistrationId(Context context, String regId) throws Exception {
//        final SharedPreferences prefs =
//                getSharedPreferences(LoginAcitivity.class.getSimpleName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = objSharedPref.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.commit();
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
}
