package com.han.main;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.han.ImageDownloadHelper.UrlImageViewCallback;
import com.han.ImageDownloadHelper.UrlImageViewHelper;
import com.han.activities.Activity;
import com.han.activities.CommentActivity;
import com.han.activities.EditProfileActivity;
import com.han.activities.Followers;
import com.han.activities.Followings;
import com.han.activities.ImageViewActivity;
import com.han.activities.LikeUserActivity;
import com.han.activities.ProfileFriendActivity;
import com.han.activities.VideoViewActivity;
import com.han.http.HttpManager;
import com.han.login.R;
import com.han.model.CommentModel;
import com.han.model.FeedBodyModel;
import com.han.model.FeedViewModel;
import com.han.model.ProfileGridViewHolder;
import com.han.model.ProfileModel;
import com.han.settings.SettingActivity;
import com.han.utils.Constant;
import com.han.utils.GlobalVariable;
import com.han.utils.Utilities;
import com.han.widget.CircularImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import at.grabner.circleprogress.CircleProgressView;

public class ProfileActivity extends AppCompatActivity implements OnMapReadyCallback {

    private  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator ;

    PullToRefreshScrollView mPullRefreshScrollView;
    private ScrollView scrollView;
    private TextView tvToFeed, tvToCamera, tvToSearch, tvToMessage;
    private TextView tvBanner;
    private TextView tvNoticeInvite, tvNoticeMessage, tvNoticeRequest, tvNoticeActivity;
    private LayoutInflater layoutInflater;
    private LinearLayout linearLayout;


    DownloadManager manager ;

    Typeface custom_font;

    private ArrayList<FeedBodyModel> arrFeedBodyModel;
//    private ArrayList<FeedModel> arrFeedModel;
    private static int RESLUT_CODE = 100;
    int limit = 10;
    int state ;
    private Context mContext;
    private ProgressDialog mProgressDialog = null;
    private String responseCode;
    private String responseDesc;

    private String strUserId;
    private int offset = 0;
    private boolean isLast;
    private ArrayList<NameValuePair> parameters;
    private ProfileModel profileModel;


    /////////////map
    public GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    View viewMap;
    DisplayMetrics display ;
    int width;

    public int logout = 100;

    /////////////////
    int l_pos = -1;
    String l_article_id = "";
    boolean l_islike = false;

    GlobalVariable globalVariable ;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initVariables();
        initViewItems();
        setFont();
        garbageCollect();

        new LoadProfileTask().execute();


    }


    private void initVariables(){
        mContext = this;
        state = 0;
        isLast = false;
        offset = 0;
        arrFeedBodyModel = new ArrayList<FeedBodyModel>();

        SharedPreferences prefs = Utilities.getSharedPreferences(this);
        strUserId = prefs.getString("user_id", null);


        manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

        display = mContext.getResources().getDisplayMetrics();
        width = display.widthPixels;
        preferences = Utilities.getSharedPreferences(this);
        globalVariable = new GlobalVariable();
    }

    public void initViewItems(){

        tvNoticeInvite = (TextView)findViewById(R.id.txt_invitation_cnt_profile);
        tvNoticeMessage = (TextView)findViewById(R.id.txt_message_cnt_profile);
        tvNoticeRequest = (TextView)findViewById(R.id.txt_request_cnt_profile);
        tvNoticeActivity = (TextView)findViewById(R.id.txt_request_cnt_activity);
        layoutInflater = getLayoutInflater();
        linearLayout = (LinearLayout)findViewById(R.id.linear_listview);

        mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.scrollview);
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if(!isLast){
                    new LoadProfileTask().execute();
                }
                mPullRefreshScrollView.onRefreshComplete();
            }
        });

        scrollView = mPullRefreshScrollView.getRefreshableView();

        TextView btnActivity = (TextView)findViewById(R.id.btn_activity);
        TextView btnSetting = (TextView)findViewById(R.id.btn_setting);
        btnActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                intent = new Intent(ProfileActivity.this, Activity.class);

                startActivityForResult(intent, logout);
            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
                startActivityForResult(intent, RESLUT_CODE);

            }
        });

        tvToCamera = (TextView)findViewById(R.id.profile_camera);
        tvToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, CaptureActivity.class);
                startActivity(intent);
            }
        });
        tvToFeed = (TextView)findViewById(R.id.profile_feed);
        tvToFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FeedViewActivity.class);
                startActivity(intent);
                finishAffinity();
//                finish();
//                callGC(intent);
            }
        });
        tvToSearch = (TextView)findViewById(R.id.profile_search);
        tvToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SearchActivity.class);
                startActivity(intent);
                finishAffinity();
//                finish();
//                callGC(intent);
            }
        });
        tvToMessage = (TextView)findViewById(R.id.profile_message);
        tvToMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MessageActivity.class);
                startActivity(intent);
                finishAffinity();
//                finish();
//                callGC(intent);
            }
        });
        tvBanner  = (TextView)findViewById(R.id.textView8);
    }

    private void setFont(){

        custom_font = Typeface.createFromAsset(getAssets(),  "Calibri.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "Calibri-Bold.ttf");
        tvBanner.setTypeface(custom_font_bold);

    }

    private void drawBanner(){
        View v1 = layoutInflater.inflate(R.layout.item_profile_first, null);
        CircularImageView imageView = (CircularImageView)v1.findViewById(R.id.civ_profile);

        TextView tvPost = (TextView)v1.findViewById(R.id.tv_profile_posts);
        tvPost.setTypeface(custom_font);
        TextView tvFollower = (TextView)v1.findViewById(R.id.tv_profile_followers);
        tvFollower.setTypeface(custom_font);
        TextView tvFollowing = (TextView)v1.findViewById(R.id.tv_profile_following);
        tvFollowing.setTypeface(custom_font);
        TextView tvMessage = (TextView)v1.findViewById(R.id.tv_profile_message);
        tvMessage.setTypeface(custom_font);
        final TextView tvGrid = (TextView)v1.findViewById(R.id.tv_profile_gridview);
        final TextView tvList = (TextView)v1.findViewById(R.id.tv_profile_listview);
        final TextView tvMap = (TextView)v1.findViewById(R.id.tv_profile_map);

        TextView tvPost_ = (TextView)v1.findViewById(R.id.tv_profile_post);
        tvPost_.setTypeface(custom_font);
        TextView tvCenter = (TextView)v1.findViewById(R.id.tv_center);
        tvCenter.setTypeface(custom_font);
        TextView tvCenterBelow = (TextView)v1.findViewById(R.id.tv_center_below);
        tvCenterBelow.setTypeface(custom_font);

        Button btnEdit = (Button)v1.findViewById(R.id.btn_profile_edit);
        btnEdit.setTypeface(custom_font);
        if(profileModel.GetPhotoUrl().equals("")){
            imageView.setImageResource(R.drawable.user_web);
//            imageView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.user_web));
        }else{
            UrlImageViewHelper.setUrlDrawable(imageView, profileModel.GetPhotoUrl(), R.drawable.loading, new UrlImageViewCallback() {
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
        }
        tvPost.setText(profileModel.GetPostCount());
        tvFollower.setText(profileModel.GetFollowerCount());
        tvFollowing.setText(profileModel.GetFollowingCount());
        tvMessage.setText(profileModel.GetAboutMe());

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref = Utilities.getSharedPreferences(ProfileActivity.this);

                final Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("username", profileModel.GetName());
                intent.putExtra("email", profileModel.GetEmail());
                intent.putExtra("password", pref.getString("password", null));
                intent.putExtra("aboutme", profileModel.GetAboutMe());
                intent.putExtra("photourl", profileModel.GetPhotoUrl());
                startActivity(intent);

            }
        });

        tvFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, Followers.class);
                startActivity(intent);
            }
        });
        tvFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, Followings.class);
                startActivity(intent);
            }
        });
        tvGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvGrid.setBackgroundDrawable(getResources().getDrawable(R.drawable.feedtoggle_grid_icon_highlighted_2x));
                tvList.setBackgroundDrawable(getResources().getDrawable(R.drawable.feedtoggle_list_icon_2x));
                tvMap.setBackgroundDrawable(getResources().getDrawable(R.drawable.feedtoggle_map_icon_2x));

                int childCount = linearLayout.getChildCount();
                linearLayout.removeViews(1, childCount - 1);

                scrollView.setVerticalScrollBarEnabled(true);
                state = 0;
                garbageCollect();
                drawGrid(0);
            }
        });
        tvList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvGrid.setBackgroundDrawable(getResources().getDrawable(R.drawable.feedtoggle_grid_icon_2x));
                tvList.setBackgroundDrawable(getResources().getDrawable(R.drawable.feedtoggle_list_icon_highlighted_2x));
                tvMap.setBackgroundDrawable(getResources().getDrawable(R.drawable.feedtoggle_map_icon_2x));

                int childCount = linearLayout.getChildCount();
                linearLayout.removeViews(1, childCount - 1);

                scrollView.setVerticalScrollBarEnabled(true);
                state = 1;
                garbageCollect();
                drawList(0);
            }
        });
        tvMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvGrid.setBackgroundDrawable(getResources().getDrawable(R.drawable.feedtoggle_grid_icon_2x));
                tvList.setBackgroundDrawable(getResources().getDrawable(R.drawable.feedtoggle_list_icon_2x));
                tvMap.setBackgroundDrawable(getResources().getDrawable(R.drawable.feedtoggle_map_icon_highlighted_2x));

                int childCount = linearLayout.getChildCount();
                linearLayout.removeViews(1, childCount - 1);
                scrollView.setVerticalScrollBarEnabled(false);
                state = 2;
                garbageCollect();
                drawMap();
            }
        });

        linearLayout.removeAllViews();
        linearLayout.addView(v1, 0);
    }

    private void drawGrid(int start){
        start = (int)(start /3) * 3;

        if(start != 0 ){
            int nn = linearLayout.getChildCount() - 1;
            if (nn * 3 != start) {
                linearLayout.removeViewAt(start / 3 + 1);
            }

        }
        View view = null;
        LinearLayout h_linearLayout = null;
        for (int i = start; i < arrFeedBodyModel.size(); i ++){
            if(i % 3 == 0){
                view = layoutInflater.inflate(R.layout.item_profile_grid_item, null);
                h_linearLayout = (LinearLayout)view.findViewById(R.id.ll_profile_grid_item);
            }
            View v2 = layoutInflater.inflate(R.layout.item_profile_grid_image, null);
            ProfileGridViewHolder profileGridHolder = new ProfileGridViewHolder();

            profileGridHolder.imageView = (ImageView)v2.findViewById(R.id.iv_1);
            setImageViewSize(profileGridHolder.imageView);

            profileGridHolder.ivIsVideo = (ImageView)v2.findViewById(R.id.iv_isvideo1);
            profileGridHolder.relativeLayout = (RelativeLayout)v2.findViewById(R.id.rl_profile_0);
            setRelativeLayoutSize(profileGridHolder.relativeLayout);

            profileGridHolder.circularProgressView = (CircleProgressView)v2.findViewById(R.id.kcjCircleView1);


            Bitmap bitmap = null;
//            Bitmap thumbnail = null;
//            Drawable d = null;

            final String fileName = arrFeedBodyModel.get(i).GetAttachURL().substring(arrFeedBodyModel.get(i).GetAttachURL().lastIndexOf('/') + 1);
            if(!checkFileExist(fileName)){
                profileGridHolder.circularProgressView.setVisibility(View.VISIBLE);
                profileGridHolder.imageView.setVisibility(View.GONE);
                profileGridHolder.ivIsVideo.setVisibility(View.GONE);
                download(i, profileGridHolder);
            }else {
                profileGridHolder.imageView.setVisibility(View.VISIBLE);
                profileGridHolder.circularProgressView.setVisibility(View.GONE);
                if(arrFeedBodyModel.get(i).GetAttatchType().equals("2")){
                    profileGridHolder.ivIsVideo.setVisibility(View.VISIBLE);
                    /////make thumbnail and
                    profileGridHolder.imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.feed));

                }else {
                    profileGridHolder.ivIsVideo.setVisibility(View.GONE);

                    ///get bitmap from local storeage

                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(PATH + fileName, options);
                    //////
                    options.inSampleSize = 7;
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(PATH + fileName, options);
//                    thumbnail = ThumbnailUtils.extractThumbnail(bitmap, width / 3, width / 3);
                    profileGridHolder.imageView.setImageBitmap(bitmap);
                    ////////////////////////////////
//                    if(PATH + fileName != null && PATH + fileName != "") //<--CHECK FILENAME IS NOT NULL
//                    {
//                        File f = new File(PATH + fileName);
//                        if(f.exists()) // <-- CHECK FILE EXISTS OR NOT
//                        {
//                            d = Drawable.createFromPath(PATH + fileName);
//                            profileGridHolder.imageView.setImageDrawable(d);
//
//                        }
//                    }
//                    Picasso.with(mContext).load(new File(PATH + fileName)).into( profileGridHolder.imageView);
                }
            }
            final int finalI = i;
            profileGridHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (arrFeedBodyModel.get(finalI).GetAttatchType().equals("1")) {
                        Intent intent = new Intent(ProfileActivity.this, ImageViewActivity.class);
                        intent.putExtra("url", PATH + fileName);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(ProfileActivity.this, VideoViewActivity.class);
                        intent.putExtra("url", PATH + fileName);
                        startActivity(intent);
                    }
                }
            });
            h_linearLayout.addView(v2, i % 3);
//            linearLayout.addView(v2, i + 1);
            if(i == arrFeedBodyModel.size() - 1 && (i % 3) < 2){
                linearLayout.addView(view, (int)(i / 3) + 1);
                return;
            }
            if((i % 3) == 2){
                try{
                    linearLayout.addView(view, (int)(i / 3) + 1);

                }catch (Exception e){
                    e.printStackTrace();
                }

//                h_linearLayout.removeAllViews();

            }
        }


    }

    private void drawList(int start){
        for (int i = start; i < arrFeedBodyModel.size(); i ++){
            View v3 = layoutInflater.inflate(R.layout.item_profile_feed, null);
            final FeedViewModel viewModel = new FeedViewModel();
            final FeedBodyModel item = arrFeedBodyModel.get(i);

            viewModel.relativeLayout = (RelativeLayout)v3.findViewById(R.id.profile_relativeLayout);
            setRelativeLayoutSize(viewModel.relativeLayout);

            viewModel.imageView = (ImageView)v3.findViewById(R.id.iv_profile_feed_image);


            viewModel.imageViewLikes = (ImageView)v3.findViewById(R.id.iv_profile_feed_likes_1);
            viewModel.user_img = (ImageView)v3.findViewById(R.id.iv_user_profile);
            UrlImageViewHelper.setUrlDrawable(viewModel.user_img, arrFeedBodyModel.get(i).GetOwnerPhoto(), R.drawable.loading, new UrlImageViewCallback() {
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
            viewModel.ivIsVideo = (ImageView)v3.findViewById(R.id.iv_isvideo_list);

            viewModel.ivShare = (ImageView)v3.findViewById(R.id.iv_profile_feed_share);
            final int finalI1 = i;
            viewModel.ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder conductor = new AlertDialog.Builder(mContext);
                    conductor.setTitle("What do you want?");

                    int resId = mContext.getResources().getIdentifier("what_u_want_profile",
                            "array", mContext.getPackageName());
                    conductor.setItems(resId, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int index) {
                            int resId1 = mContext.getResources().getIdentifier("what_u_want_profile", "array",
                                    mContext.getPackageName());
                            String subject = mContext.getResources().getStringArray(resId1)[index];
                            sharingAction(subject, PATH + getFilename(item.GetAttachURL()), item.GetAttatchType(), finalI1);
                        }
                    });
                    AlertDialog alert = conductor.create();
                    alert.show();
                }
            });

            viewModel.time = (TextView)v3.findViewById(R.id.tv_time_profile);
            viewModel.username = (TextView)v3.findViewById(R.id.tv_profile_username);
            viewModel.mesage = (TextView)v3.findViewById(R.id.tv_profile_feed_message);
            viewModel.mesage.setTypeface(custom_font);
            viewModel.likecount = (TextView)v3.findViewById(R.id.tv_profile_feed_likes);
            viewModel.likecount.setTypeface(custom_font);
            viewModel.commentcount = (TextView)v3.findViewById(R.id.tv_profile_feed_comment);
            viewModel.commentcount.setTypeface(custom_font);
            viewModel.editedBy = (TextView)v3.findViewById(R.id.tv_profile_feed_edited_by);
            viewModel.editedBy .setTypeface(custom_font);
            String strEditedBy = "Edited by " + removeUnderBar(item.GetPosterName());
            viewModel.editedBy.setText(strEditedBy);
            final int finalI2 = i;
            viewModel.editedBy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences preferences = Utilities.getSharedPreferences(mContext);
                    String userId = preferences.getString("user_id", null);
                    if(userId.equals(item.GetPosterId())){
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        mContext.startActivity(intent);

                    }else {
                        Intent intent = new Intent(mContext.getApplicationContext(), ProfileFriendActivity.class);
                        intent.putExtra("userid", item.GetPosterId());
                        mContext.startActivity(intent);


//                        finishAffinity();
//                        callGC(intent);
                    }
                }
            });

            viewModel.linearLayout = (LinearLayout)v3.findViewById(R.id.ll_profile_comment);
            viewModel.circleProgressView = (CircleProgressView)v3.findViewById(R.id.kcjCircleView2);

            ArrayList<CommentModel> arrComment = item.GetArrCommentModel();
            if(arrComment.size() > 0){
                configureCommentLayout( viewModel.linearLayout, arrComment);
            }
            viewModel.likecount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, LikeUserActivity.class);
                    intent.putExtra("article_id", item.GetArticleId());
                    startActivity(intent);

                }
            });
            viewModel.commentcount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, CommentActivity.class);
                    intent.putExtra("article_id", item.GetArticleId());
                    startActivity(intent);

                }
            });
            final int finalI = i;
            viewModel.imageViewLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strArticleId = arrFeedBodyModel.get(finalI).GetArticleId();
                    String str = item.GetLikeCount();
                    int likeCount = Integer.parseInt(str);
                    if (item.GetIslike().equals("0")) {
                        changeLike(strArticleId, finalI, true);
                        viewModel.imageViewLikes.setBackgroundDrawable(getResources().getDrawable(R.drawable.liked_3x));

                        likeCount ++;
                        viewModel.likecount.setText(String.valueOf(likeCount) + " likes");
                    } else {
                        changeLike(strArticleId, finalI, false);
                        viewModel.imageViewLikes.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_3x));

                        likeCount --;
                        viewModel.likecount.setText(String.valueOf(likeCount) + " likes");
                    }
                }
            });
            viewModel.username.setText(item.GetPosterName().replace("_", " "));
            viewModel.mesage.setText(item.GetDescription());
            viewModel.time.setText(countTime(item.GetTime()));
            viewModel.likecount.setText(item.GetLikeCount() + " likes");
            viewModel.commentcount.setText(item.GetCommentCount() + " comments");
//            viewModel.comment.setText(item.GetTag());
            if(item.GetIslike().equals("1")){
                viewModel.imageViewLikes.setBackgroundDrawable(getResources().getDrawable(R.drawable.liked_3x));
            }else{
                viewModel.imageViewLikes.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_3x));
            }
            setImageViewSize(viewModel.imageView);
//            viewModel.imageView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.feed));

            //////////////set Image=======================================================
            Bitmap bitmap = null;
//            Bitmap thumbnail = null;
//            Drawable d = null;

            final String fileName = arrFeedBodyModel.get(i ).GetAttachURL().substring(arrFeedBodyModel.get(i).GetAttachURL().lastIndexOf('/') + 1);
            if(!checkFileExist(fileName)){
                viewModel.imageView.setVisibility(View.GONE);
                viewModel.ivIsVideo.setVisibility(View.GONE);
                viewModel.editedBy.setVisibility(View.GONE);
                download2(i, viewModel);
            }else {
                viewModel.imageView.setVisibility(View.VISIBLE);
                viewModel.editedBy.setVisibility(View.VISIBLE);
                viewModel.circleProgressView.setVisibility(View.GONE);
                if(arrFeedBodyModel.get(i).GetAttatchType().equals("2")){
                    viewModel.ivIsVideo.setVisibility(View.VISIBLE);
                    viewModel.imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.feed));
                }else {
                    viewModel.ivIsVideo.setVisibility(View.GONE);

                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(PATH + fileName, options);
                    //////
                    options.inSampleSize = 5;
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(PATH + fileName, options);
//                    thumbnail = ThumbnailUtils.extractThumbnail(bitmap, width, width);
                    viewModel.imageView.setImageBitmap(bitmap);

//                    if(PATH + fileName != null && PATH + fileName != "") //<--CHECK FILENAME IS NOT NULL
//                    {
//                        File f = new File(PATH + fileName);
//                        if(f.exists()) // <-- CHECK FILE EXISTS OR NOT
//                        {
//                            d = Drawable.createFromPath(PATH + fileName);
//                            viewModel.imageView.setImageDrawable(d);
//
//                        }
//                    }
                }

            }
            viewModel.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(item.GetAttatchType().equals("1")){

                        Intent intent = new Intent(mContext, ImageViewActivity.class);
                        intent.putExtra("url", PATH + fileName);
                        mContext.startActivity(intent);
                    }else {
                        Intent intent = new Intent(mContext, VideoViewActivity.class);
                        intent.putExtra("url", PATH + fileName);
                        mContext.startActivity(intent);
                    }

                }
            });
            viewModel.ivIsVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, VideoViewActivity.class);
                    intent.putExtra("url", PATH + fileName);
                    mContext.startActivity(intent);
                }
            });

            linearLayout.addView(v3, i + 1);
        }
    }

    private void drawMap(){
        try {
            int v = getPackageManager().getPackageInfo("com.google.android.gms.maps", 0 ).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        View v4 = null;
        try{
            v4 = getLayoutInflater().inflate(R.layout.item_profile_map, null);
        }catch (Exception e){
            e.printStackTrace();
        }

        FrameLayout frameLayout = (FrameLayout)v4.findViewById(R.id.framelayout_main);
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

//            viewMap= (View)v4.findViewById(R.id.view_map);
        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        LatLng position = new LatLng(profileModel.GetLatitude(), profileModel.GetLogitude());
        Marker hamburg = googleMap.addMarker(new MarkerOptions()
                .position(position)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.app_icon)));
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        try{
            linearLayout.addView(v4, 1);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {
//        double lat = preferences.getFloat("latitude", 0);
//        double lon = preferences.getFloat("logitude", 0);
//        LatLng position = new LatLng(lat, lon);
//        Marker hamburg = map.addMarker(new MarkerOptions()
//                .position(position)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.app_icon1)));
////                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
//        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
////        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
    private String removeUnderBar(String str){
        return str.replace("_", " ");
    }
    public void changeLike(String articleId, int position, boolean islike){
        this.l_islike  = islike;
        this.l_article_id = articleId;
        this.l_pos = position;


        new ChangeLikeTask().execute();

    }
    private void sharingAction(String str, String mediaPath, String fileType, int position){
        if(str.equals("Share to Facebook")){
            sharing(str, mediaPath, fileType);
        }else if(str.equals("Share to Twitter")){
            sharing(str, mediaPath, fileType);
        }else if(str.equals("Share to Instagram")){
            sharing(str, mediaPath, fileType);
        }else if(str.equals("Send Email")){
            sharing(str, mediaPath, fileType);
        }else if(str.equals("Save to Camera Roll")){
            if(fileType.equals("1")){
                Utilities.saveImageToLocal(mediaPath);
                Utilities.showToast(mContext, "Saved photo to your camera roll successfully!");
            }else {
                Utilities.showToast(mContext, "Saved video to your camera roll successfully!");
            }
        }else if(str.equals("Report this point")){
            Utilities.showToast(mContext, "Reported successfully. Thank you!");
        }else if(str.equals("Delete this post")){
            new DeleteArticle().execute(position);
        }
    }
    private void sharing(String str, String mediaPath, String fileType){
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        if(fileType.equals("1")){
// Set the MIME type
            share.setType("image/jpeg");

        }else {
            share.setType("video/quicktime");
        }

// Create the URI from the media
        java.io.File media = new java.io.File(mediaPath);
        Uri uri = Uri.fromFile(media);

// Add the URI and the caption to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
//            share.putExtra(Intent.EXTRA_TEXT, caption);

// Broadcast the Intent.
        mContext.startActivity(Intent.createChooser(share, "Share to"));
    }
    private String getFilename(String path){
        return path.substring(path.lastIndexOf("/") + 1);
    }
    private Bitmap getBitmap(int pos){
        Bitmap bitmap = null;
        Bitmap resizedBitmap = null;
        DisplayMetrics display = mContext.getResources().getDisplayMetrics();
        int width = display.widthPixels;
        if(arrFeedBodyModel.get(pos).GetBitmap() == null){
            if(arrFeedBodyModel.get(pos).GetAttatchType().equals("1")){
                if(state == 0){
                    resizedBitmap = ThumbnailUtils.extractThumbnail(bitmap, width / 3, width / 3);
                }else if(state == 1){
                    resizedBitmap = ThumbnailUtils.extractThumbnail(bitmap, width, width);
                }

                arrFeedBodyModel.get(pos).SetBitmap(bitmap);
            }else {

            }
        }else {
            resizedBitmap = arrFeedBodyModel.get(pos).GetBitmap();
        }


        return resizedBitmap;
    }

    private void deleteAction( int stateNum){
        if(stateNum == 0){
            int childCount = linearLayout.getChildCount();
            linearLayout.removeViews(1, childCount - 1);

            scrollView.setVerticalScrollBarEnabled(true);
            drawGrid(0);
        }else if(stateNum == 1){
            int childCount = linearLayout.getChildCount();
            linearLayout.removeViews(1, childCount - 1);

            scrollView.setVerticalScrollBarEnabled(true);
            drawList(0);
        }
    }

    private void addViewAtBottom(){
        if(state == 1) {
            drawList((offset - 1) * Constant.MAX_ARTICLE_AMOUNT_PER_PAGE);
        }else if(state == 0){
            drawGrid((offset - 1) * Constant.MAX_ARTICLE_AMOUNT_PER_PAGE);
        }else if(state == 2){
            drawMap();
        }

    }

    public void configScrollView(){
        drawBanner();
        addViewAtBottom();
    }

    public void download2(final int pos, final FeedViewModel feedViewModel){

        String url = arrFeedBodyModel.get(pos).GetAttachURL();
        ///get filename from url
        final String fileName = url.substring(url.lastIndexOf('/') + 1);
        ///creat download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        ////////set destinatin storage path
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        final long downloadId = manager.enqueue(request);
        feedViewModel.circleProgressView.setValueAnimated(0, 10);
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
                            feedViewModel.circleProgressView.setValueAnimated(percent, 100);
                            if (percent == 100) {
                                feedViewModel.circleProgressView.setVisibility(View.GONE);
                                feedViewModel.imageView.setVisibility(View.VISIBLE);

                                Bitmap bitmap = null;
                                Drawable d = null;

                                if(arrFeedBodyModel.get(pos).GetAttatchType().equals("2")){
                                    feedViewModel.ivIsVideo.setVisibility(View.VISIBLE);
                                    feedViewModel.imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.feed));
                                }else {
                                    ///get bitmap from localpath
                                    bitmap = BitmapFactory.decodeFile(PATH + fileName);
                                    Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, width , width );
                                    feedViewModel.imageView.setImageBitmap(thumbnail);

//                                    if(PATH + fileName != null && PATH + fileName != "") //<--CHECK FILENAME IS NOT NULL
//                                    {
//                                        File f = new File(PATH + fileName);
//                                        if(f.exists()) // <-- CHECK FILE EXISTS OR NOT
//                                        {
//                                            d = Drawable.createFromPath(PATH + fileName);
//                                            feedViewModel.imageView.setImageDrawable(d);
//
//                                        }
//                                    }
                                }

                            }
                        }
                    });
                    cursor.close();
                }

            }
        }).start();
    }

    public void download(final int pos, final ProfileGridViewHolder profileGridViewHolder){

        String url = arrFeedBodyModel.get(pos).GetAttachURL();
        ///get filename from url
        final String fileName = url.substring(url.lastIndexOf('/') + 1);
        ///creat download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        ////////set destinatin storage path
//        request.setDestinationInExternalPublicDir(PATH, fileName);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        final long downloadId = manager.enqueue(request);
        profileGridViewHolder.circularProgressView.setValueAnimated(0, 10);
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
                            profileGridViewHolder.circularProgressView.setValueAnimated(percent, 100);
                            if (percent == 100) {
                                profileGridViewHolder.circularProgressView.setVisibility(View.GONE);
                                profileGridViewHolder.imageView.setVisibility(View.VISIBLE);

                                Bitmap bitmap = null;
                                Drawable d = null;

                                if(arrFeedBodyModel.get(pos).GetAttatchType().equals("2")){
                                    profileGridViewHolder.ivIsVideo.setVisibility(View.VISIBLE);
                                    profileGridViewHolder.imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.feed));
                                }else {
                                    ///get bitmap from localpath
                                    bitmap = BitmapFactory.decodeFile(PATH + fileName);
                                    Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, width , width );
                                    profileGridViewHolder.imageView.setImageBitmap(thumbnail);


//                                    if(PATH + fileName != null && PATH + fileName != "") //<--CHECK FILENAME IS NOT NULL
//                                    {
//                                        File f = new File(PATH + fileName);
//                                        if(f.exists()) // <-- CHECK FILE EXISTS OR NOT
//                                        {
//                                            d = Drawable.createFromPath(PATH + fileName);
//                                            profileGridViewHolder.imageView.setImageDrawable(d);
//
//                                        }
//                                    }
                                }

                            }
                        }
                    });
                    cursor.close();
                }

            }
        }).start();
    }

    private void setRelativeLayoutSize(RelativeLayout relativeLayout){
        ViewGroup.LayoutParams layoutParams = null;
        try{
            layoutParams =  relativeLayout.getLayoutParams();
        }catch (Exception e){
            e.printStackTrace();
        }

        if(state == 0){
            layoutParams.width = width / 3;
            layoutParams.height = width / 3;
        }else if(state == 1){
            layoutParams.width = width;
            layoutParams.height = width;
        }

        relativeLayout.setLayoutParams(layoutParams);


    }

    private boolean checkFileExist(String fileName){

        File file = new File(PATH, fileName);
        if(file.exists()){
            return true;
        }else
            return false;
    }

    private void setImageViewSize(ImageView imageview){
        ///get screen size
//        Display display = getWindowManager().getDefaultDisplay();
//        DisplayMetrics display = mContext.getResources().getDisplayMetrics();
//        int width = display.widthPixels;

        if(state == 0){
            imageview.setMaxWidth((int)(width / 3));
            imageview.setMaxHeight((int)(width / 3));
        }else if(state == 1){
            imageview.setMaxWidth((int)width);
            imageview.setMaxHeight((int)width);
        }

    }

    private String countTime(int timeStamp){
        String str = "recent";
        int second = (int)(timeStamp);
        int result = second;

        if((int)(result / 60) < 60){

            return "recent";
        } else {
            result = (int)(second / 3600);
            if(result < 24){
                str = String.valueOf(result) + "h";
            }else{
                result = (int)(second /  (3600 * 24));
                if(result < 7){
                    str = String.valueOf(result) + "d";
                }else{
                    result = (int)(second / (3600 * 24 * 7));
                    if(result < 4){
                        str = String.valueOf(result) + "w";
                    }else {
                        result = (int)(second / (3600 * 24 * 30));
                        if(result < 12){
                            str = String.valueOf(result) + "mo";
                        }else {
                            result = (int)(second / (3600 * 24 * 365));
                            str = String.valueOf(result) + "y";
                        }
                    }
                }
            }
        }

        return str;
    }

    private void configureCommentLayout(LinearLayout llComment, final ArrayList<CommentModel> arrComment){
        llComment.removeAllViews();
        int count = arrComment.size();
        for(int i = 0; i < count; i ++){
            View commentView = layoutInflater.inflate(R.layout.item_feed_comment, null);
            TextView tvName = (TextView)commentView.findViewById(R.id.tv_feed_comment_email);
            Typeface custom_font_bold = Typeface.createFromAsset(mContext.getAssets(), "Calibri-Bold.ttf");
            tvName.setTypeface(custom_font_bold);
            final int finalI = i;
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext.getApplicationContext(), ProfileFriendActivity.class);
                    intent.putExtra("userid", arrComment.get(finalI).GetUserId());
                    mContext.startActivity(intent);
                }
            });
            TextView tvComment = (TextView)commentView.findViewById(R.id.tv_feed_comment_comment);

            CommentModel cm = arrComment.get(i);

            tvName.setText("@" + cm.GetName());
            tvComment.setText(getEmoji(cm.GetComment()));

            llComment.addView(commentView, i);
        }
    }

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



    class LoadProfileTask extends AsyncTask<String, Integer, String> {

        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {

            if(offset == 1 ){
                configScrollView();
            }else {
                addViewAtBottom();
            }
            hideProgress();
            this.cancel(true);
            super.onPostExecute(result);
        }
        @Override
        protected String doInBackground(String... param) {
            parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("selected_id", strUserId));
            parameters.add(new BasicNameValuePair("index", String.valueOf(offset)));
            JSONObject result =  HttpManager.getInstance().callPost(mContext, Constant.PROFILE + strUserId, parameters, true);

            if (result == null) {

                Utilities.showToast( mContext, "Plase try again...");
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

                        profileModel = new ProfileModel();

                        profileModel.SetFollowingCount(result.getString("followings_count"));
                        profileModel.SetPostCount(result.getString("post_count"));
                        profileModel.SetFollowerCount(result.getString("followers_count"));
                        profileModel.SetLogitude(result.getDouble("longitude"));
                        profileModel.SetLatitude(result.getDouble("latitude"));
                        profileModel.SetIsFriend(result.getString("is_friend"));

                        JSONObject obj = result.getJSONObject("info");

                        profileModel.SetPhotoUrl(obj.getString("photo"));
                        profileModel.SetStatus(obj.getString("status"));
                        profileModel.SetFbId(obj.getString("fb_id"));
                        profileModel.SetIsRegistered(obj.getString("isRegistered"));
                        profileModel.SetAboutMe(obj.getString("about_me"));
                        profileModel.SetDeviceType(obj.getString("deviceType"));
                        profileModel.SetId(obj.getString("id"));
                        profileModel.SetPurchased(obj.getString("purchased"));
                        profileModel.SetName(obj.getString("name"));
                        profileModel.SetEmail(obj.getString("email"));
                        profileModel.SetGender(obj.getString("gender"));


                        JSONArray jsonArray = result.getJSONArray("posts");

                        int length = jsonArray.length();
                        int max = 0;
                        offset ++;
                        if(length < Constant.MAX_ARTICLE_AMOUNT_PER_PAGE){

                            isLast = true;
                        }else{

                            isLast = false;
                        }
                        for (int i = 0; i < length; i ++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            FeedBodyModel bodyModel = new FeedBodyModel();

//                            bodyModel.SetFeedId(String.valueOf(i + sectionList.size()));
                            bodyModel.SetIslike(jsonObject.getString("is_like"));
                            bodyModel.SetPosterName(jsonObject.getString("poster_name"));
                            bodyModel.SetAttachURL(jsonObject.getString("attach_url"));
                            bodyModel.SetOwnerEmail(jsonObject.getString("owner_email"));
                            bodyModel.SetArticleId(jsonObject.getString("article_id"));
                            bodyModel.SetPosterId(jsonObject.getString("poster_id"));
                            bodyModel.SetLikeCount(jsonObject.getString("like_count"));
                            bodyModel.SetDescriptioin(jsonObject.getString("description"));
                            bodyModel.SetOwnerId(jsonObject.getString("owner_id"));
                            bodyModel.SetAttatchType(jsonObject.getString("attach_type"));
                            bodyModel.SetCommentCount(jsonObject.getString("comment_count"));
                            bodyModel.SetTag(jsonObject.getString("tag"));
                            bodyModel.SetTime(jsonObject.getInt("posted_date"));
                            bodyModel.SetOwnerPhoto(jsonObject.getString("owner_photo"));

                            JSONArray arrComment = jsonObject.getJSONArray("comments");
                            ArrayList<CommentModel> arrCM = new ArrayList<CommentModel>();
                            int n = arrComment.length();
                            for(int j = 0; j < n; j ++){
                                CommentModel cm = new CommentModel();
                                JSONObject objComment = arrComment.getJSONObject(j);
                                cm.SetComment(objComment.getString("comment"));
                                cm.SetPhotoURL(objComment.getString("photo"));
                                cm.SetUserId(objComment.getString("user_id"));
                                cm.SetName(objComment.getString("name"));

                                arrCM.add(cm);
                            }
                            bodyModel.SetArrCommentModel(arrCM);

                            arrFeedBodyModel.add(bodyModel);

//                            FeedModel feedModel = new FeedModel();
//                            feedModel.SetFeedBodyModel(bodyModel);
//                            arrFeedModel.add(feedModel);
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

    class ChangeLikeTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            this.cancel(true);
            super.onPostExecute(result);
        }


        @Override
        protected String doInBackground(String... param) {

            parameters = new ArrayList<NameValuePair>();
            String strUrl = Constant.ARTICLE_ID_LIKE + l_article_id + "/like";

            parameters.add(new BasicNameValuePair("id", l_article_id));

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

                        String str = arrFeedBodyModel.get(l_pos).GetLikeCount();
                        int likeCount = Integer.parseInt(str);
                        if(l_islike){
                            likeCount ++;
                            arrFeedBodyModel.get(l_pos).SetLikeCount(String.valueOf(likeCount));
                            arrFeedBodyModel.get(l_pos).SetIslike("1");
                        }else {
                            likeCount --;
                            arrFeedBodyModel.get(l_pos).SetLikeCount(String.valueOf(likeCount));
                            arrFeedBodyModel.get(l_pos).SetIslike("0");
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
    class DeleteArticle extends AsyncTask<Integer, Integer, String> {
        boolean flag;
        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String result) {
            hideProgress();
            if(flag){
                Utilities.showToast(mContext, "Deleted Post Successfully!");
            }
            this.cancel(true);
            super.onPostExecute(result);
        }


        @Override
        protected String doInBackground(Integer... param) {

            parameters = new ArrayList<NameValuePair>();
            String articleId = arrFeedBodyModel.get(param[0]).GetArticleId();
            String strUrl = Constant.DELETE_ARTICLE + articleId + "/delete";

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
                        arrFeedBodyModel.remove(param[0]);
                        //Deleted Post Successfully.
                        flag = true;
                        deleteAction(state);
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
                setResult(logout);
                finish();            // to close this activity
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        }
        if(globalVariable.getValue(preferences, Constant.N_REQUEST) > 0){
            tvNoticeRequest.setVisibility(View.VISIBLE);
            tvNoticeRequest.setText(String.valueOf(globalVariable.getValue(preferences, Constant.N_REQUEST)));
            tvNoticeActivity.setVisibility(View.VISIBLE);
            tvNoticeActivity.setText(String.valueOf(globalVariable.getValue(preferences, Constant.N_REQUEST)));
        }else {
        }

    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
        ////
        garbageCollect();
    }
    private void garbageCollect() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            String type = intent.getStringExtra("type");
            //do other stuff here
            if(type.equals("request")){
                tvNoticeRequest.setVisibility(View.VISIBLE);
                tvNoticeRequest.setText(globalVariable.getValue(preferences, Constant.N_REQUEST));
                tvNoticeActivity.setVisibility(View.VISIBLE);
                tvNoticeActivity.setText(globalVariable.getValue(preferences, Constant.N_REQUEST));
            }
        }
    };
}
