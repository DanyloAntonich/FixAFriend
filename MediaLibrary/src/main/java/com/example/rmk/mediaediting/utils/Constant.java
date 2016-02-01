package com.example.rmk.mediaediting.utils;

import android.view.inputmethod.InputMethodManager;

/**
 * Created by Administrator on 9/29/2015.
 */
public class Constant {
    public static String BASE_URL = "http://159.203.64.134/fixafriend/";
    public static String FACEBOOK_LOGIN = "auth/facebook-login";
    public static String LOG_IN = BASE_URL + "auth/login";
    public static String SIGN_UP = BASE_URL + "auth/signup";
    public static String LOG_OUT = BASE_URL + "auth/logout";
    public static String PROFILE = BASE_URL + "profile/get/";
    public static String SAVE = BASE_URL + "profile/save";
    public static String FORGOT_PASSWORD = BASE_URL + "auth/forgot-password";
    public static String CHANG_PASSWORD = BASE_URL + "auth/change-password";
    public static String SEND_ARTICLE = BASE_URL + "send-article";
    public static String GET_INVITATION_ARTICLE = BASE_URL + "get-invitation-articles/";
    public static String POST_ARTICLE_ID = BASE_URL + "post-article/";
    public static String ARTICLE_ID_LIKE = BASE_URL + "article/";
    public static String ARTICLE_INDEX = BASE_URL + "articles";
    public static String SEARCH = BASE_URL + "users/search";
    public static String SEARCH_ARTICLE = BASE_URL + "articles";
    public static String FOLLOW = BASE_URL + "users/";
    public static String UNFOLLOW = BASE_URL + "users/";
    public static String NOTIFICATION = BASE_URL + "notification";
    public static String GET_FRIENDS = BASE_URL + "friends";
    public static String MESSAGES_USERS = BASE_URL + "messages/users";
    public static String MESSAGES_HISTORY = BASE_URL + "messages/";
    public static String ACTIVITIES =  BASE_URL +"activities";
    public static String COMMENT =  BASE_URL +"article/";
    public static String ADD_COMMENT = "/add_comment";
    public static String GET_COMMENT = "/get_comments";
    public static String REQUEST_ARTICLE = BASE_URL + "request-article";

    public static String PREF_USER_DETAILS = "faf";
    public static String CONTACTNUMBER = "contact_number";
    public static String RECORDED_HOURS = "recorded_hours";
    public static String PRACTINER_CAPTION = "practiner_caption";
    public static String PRACTINER_LEVEL = "practiner_level";
    public static String ROLEID = "role_id";
    public static String PAYMENT_STATUS = "payment_status";
    public static String PHONENUMBER = "phnumber";
    public static String PASSWORD = "password";
    public static String ISREMEMBER = "rememberme";
    public static String COUNTRYCODE = "countrycode";
    public static final String GMT_PATTERN_24 = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_PATTERN_MMM_dd_yyyy = "dd/MM/yyyy hh:mm:ss a";
    public static InputMethodManager imm;

    public static double latitude = 41.0;
    public static double longitude = 123.0;

    public static int MAX_ARTICLE_AMOUNT_PER_PAGE = 10;
    public static int MAX_USER_AMOUNT_PER_PAGE = 20;
    public static int MAX_FEED_ARTICLE_PER_PAGE = 12;

    public static int MAX_THREAD_COUNT = 3;

    public static int MAX_DESCRIPTION = 500;

    public static String DEVICE_ID = "AIzaSyAq3r5lZv0XRaGevwhItdUu4jaoeVM41Hs";
    public static String DEVICE_TYPE = "Android";
}
