package com.han.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.han.ImageDownloadHelper.UrlImageViewCallback;
import com.han.ImageDownloadHelper.UrlImageViewHelper;
import com.han.activities.ChatActivity;
import com.han.activities.CommentActivity;
import com.han.activities.HashTagActivity;
import com.han.activities.ImageViewActivity;
import com.han.activities.LikeUserActivity;
import com.han.activities.ProfileFriendActivity;
import com.han.activities.VideoViewActivity;
import com.han.login.R;
import com.han.main.FeedViewActivity;
import com.han.main.ProfileActivity;
import com.han.model.ChatModel;
import com.han.model.CommentModel;
import com.han.model.FeedBannerModel;
import com.han.model.FeedBodyModel;
import com.han.model.FeedModel;
import com.han.model.FeedViewModel;
import com.han.utils.Utilities;
import com.han.widget.CircularImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by hic on 2015-11-19.
 */
public class FeedAdapter extends ArrayAdapter<FeedModel> {

    private  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator ;

    private ArrayList<FeedModel> items;
    private Context mContext;

    private LayoutInflater mInflater;
    UrlImageViewHelper urlImageViewHelper;
    DisplayMetrics display ;
    int width;

    int num ;


    public FeedAdapter(Context chatActivity, ArrayList<FeedModel> historyChatList_tmp) {

        // TODO Auto-generated constructor stub
        super(chatActivity, android.R.layout.simple_list_item_1, historyChatList_tmp);
        this.items = historyChatList_tmp;
        this.mContext = chatActivity;

        urlImageViewHelper = new UrlImageViewHelper();
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        /////////
        display = mContext.getResources().getDisplayMetrics();
        width = display.widthPixels;
        num = -1;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.item_feed_2, null);
        }
        //get feed body & banner model form array
        final FeedBodyModel item = items.get(position).GetFeedBodyModel();
        final FeedBannerModel item_banner = items.get(position).GetFeedBannerModel();
        //create feedviewmodel
        FeedViewModel feedViewModel  = new FeedViewModel();
        ///configure banner items
        feedViewModel.user_img_feed =(CircularImageView) convertView.findViewById(R.id.iv_user_feed);
        feedViewModel.username = (TextView)convertView.findViewById(R.id.tv_feed_username);
        feedViewModel.editedCount = (TextView)convertView.findViewById(R.id.tv_feed_edits);
        feedViewModel.time = (TextView)convertView.findViewById(R.id.tv_time_feed);
        /// set data on banner
        feedViewModel.username.setText("  " + item_banner.GetUsername().replace("_", " "));
        feedViewModel.editedCount.setText(item_banner.GetEditNumber() + " Edits");
        feedViewModel.time.setText(countTime(item_banner.GetPostedDate()));


        if(item_banner.GetPhoto().equals("")){
            feedViewModel.user_img_feed.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.user_web));

        }else{

            UrlImageViewHelper.setUrlDrawable(feedViewModel.user_img_feed, item_banner.GetPhoto(), R.drawable.loading, new UrlImageViewCallback() {
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
        ///create body items
        feedViewModel.imageView = (ImageView)convertView.findViewById(R.id.iv_feed_image);
        feedViewModel.imageViewLikes = (ImageView)convertView.findViewById(R.id.iv_feed_likes_1);
        feedViewModel.ivToChat = (ImageView)convertView.findViewById(R.id.iv_feed_comment_1);
        feedViewModel.ivFeedShare = (ImageView)convertView.findViewById(R.id.iv_feed_share);
        feedViewModel.ivIsVideo = (ImageView)convertView.findViewById(R.id.iv_isvideo4);

        feedViewModel.mesage = (TextView)convertView.findViewById(R.id.tv_feed_message);
        feedViewModel.likecount = (TextView)convertView.findViewById(R.id.tv_feed_likes);
        feedViewModel.commentcount = (TextView)convertView.findViewById(R.id.tv_feed_comment);

        feedViewModel.editedBy = (TextView)convertView.findViewById(R.id.tv_feed_edited_by);

        feedViewModel.llComment = (LinearLayout)convertView.findViewById(R.id.ll_feed_comment);
        feedViewModel.llHashTag = (LinearLayout)convertView.findViewById(R.id.ll_hash_tag);
        feedViewModel.llHashTag2 = (LinearLayout)convertView.findViewById(R.id.ll_hash_tag2);
        feedViewModel.relativeLayout = (RelativeLayout)convertView.findViewById(R.id.relativeLayout);
        feedViewModel.llCircleProgress = (LinearLayout)convertView.findViewById(R.id.linear_circle_progress);

        ///set data on body
        ArrayList<CommentModel> arrComment = item.GetArrCommentModel();
        feedViewModel.mesage.setText(getEmoji(item.GetDescription()));

        String strLikeCount = item.GetLikeCount() + " likes";
        feedViewModel.likecount.setText(strLikeCount);

        String strCommentCount = item.GetCommentCount() + " comments";
        feedViewModel.commentcount.setText(strCommentCount);
        String strEditedBy = "Edited by " + removeUnderBar(item.GetPosterName());
        feedViewModel.editedBy.setText(strEditedBy);
        feedViewModel.editedBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = Utilities.getSharedPreferences(mContext);
                String userId = preferences.getString("user_id", null);
                if(userId.equals(item.GetPosterId())){
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    mContext.startActivity(intent);

                }else {
                    Intent intent = new Intent(mContext.getApplicationContext(), ProfileFriendActivity.class);
                    FeedBodyModel fbm = items.get(position).GetFeedBodyModel();
                    intent.putExtra("userid", fbm.GetPosterId());
                    mContext.startActivity(intent);
                }
            }
        });
        ///////////config tags==================================================

        if(!item.GetTag().equals("")){
            ArrayList<String> arrHashTag = getHashTag(item.GetTag());
            configureHashTagLayout(feedViewModel.llHashTag,feedViewModel.llHashTag2, arrHashTag);
        }

        //////////config comments==================================================

        if(arrComment.size() > 0){
            configureCommentLayout(feedViewModel.llComment, arrComment);
        }

        ////////////config like or dislike==================================================

        if(item.GetIslike().equals("1")){
//            viewModel.imageViewLikes.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.liked_2x));
            feedViewModel.imageViewLikes.setImageResource(R.drawable.liked_3x);
        }else if(item.GetIslike().equals("0")){
//            viewModel.imageViewLikes.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.like_2x));
            feedViewModel.imageViewLikes.setImageResource(R.drawable.like_3x);
        }
        feedViewModel.imageViewLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof FeedViewActivity) {
                    String articleId = item.GetArticleId();

                    if (item.GetIslike().equals("0")) {
                        ((FeedViewActivity) mContext).changeLike(articleId, position, true);
                    } else {
                        ((FeedViewActivity) mContext).changeLike(articleId, position, false);
                    }
                }
            }

        });
        ///set font
        setFont(feedViewModel);

        ////////////set article image ==============================================================

//        feedViewModel.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.feed));
        setImageViewSize(feedViewModel.imageView);

        final String fileName = item.GetAttachURL().substring(item.GetAttachURL().lastIndexOf('/') + 1);
        File file = new File(PATH, fileName);
        Bitmap bitmap = null;
        Drawable d = null;
        if(!file.exists()){
            if (mContext instanceof FeedViewActivity) {

                feedViewModel.imageView.setVisibility(View.GONE);
                feedViewModel.ivIsVideo.setVisibility(View.GONE);
                feedViewModel.editedBy.setVisibility(View.GONE);
                feedViewModel.llCircleProgress.setVisibility(View.VISIBLE);

                setLinearLayoutSize(feedViewModel.llCircleProgress);
//                downloadQueueManager.insert(position, feedViewModel.llCircleProgress);
//                downloadTask(position);
                if(num < position){
                    num = position;
                    ((FeedViewActivity) mContext).download(num, feedViewModel.llCircleProgress);

                }
            }
        }else {
            feedViewModel.imageView.setVisibility(View.VISIBLE);
            feedViewModel.editedBy.setVisibility(View.VISIBLE);
            feedViewModel.llCircleProgress.setVisibility(View.GONE);


            if(item.GetAttatchType().equals("1")){
                feedViewModel.ivIsVideo.setVisibility(View.GONE);

                if(item.GetBitmap() == null){

//                    BitmapFactory.Options bfOptions=new BitmapFactory.Options();
//                    bfOptions.inDither=false;                     //Disable Dithering mode
//                    bfOptions.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
//                    bfOptions.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
//                    bfOptions.inTempStorage=new byte[32 * 1024];
//
//                     bitmap = BitmapFactory.decodeFile(PATH + fileName, bfOptions);
//                    feedViewModel.imageView.setImageBitmap(bitmap);


                    if(PATH + fileName != null && PATH + fileName != "") //<--CHECK FILENAME IS NOT NULL
                    {
                        File f = new File(PATH + fileName);
                        if(f.exists()) // <-- CHECK FILE EXISTS OR NOT
                        {
                            d = Drawable.createFromPath(PATH + fileName);
                            feedViewModel.imageView.setImageDrawable(d);

                        }
                    }

                }else {
                    feedViewModel.imageView.setImageBitmap(item.GetBitmap());
                }
            }else {
                feedViewModel.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.feed));
                feedViewModel.ivIsVideo.setVisibility(View.VISIBLE);

            }
        }

        ///////////////set click listener==============================================================

        ///banner
        feedViewModel.user_img_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext.getApplicationContext(), ProfileFriendActivity.class);
                FeedBodyModel fbm = items.get(position).GetFeedBodyModel();
                intent.putExtra("userid", fbm.GetOwnerId());
                mContext.startActivity(intent);
            }
        });
//        feedViewModel.username.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext.getApplicationContext(), ProfileFriendActivity.class);
//                FeedBodyModel fbm = items.get(position).GetFeedBodyModel();
//                intent.putExtra("userid", fbm.GetPosterId());
//                mContext.startActivity(intent);
//            }
//        });
        feedViewModel.editedCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext.getApplicationContext(), FeedViewActivity.class);
                FeedBodyModel fbm = items.get(position).GetFeedBodyModel();

                intent.putExtra("mode", 2);
                intent.putExtra("article_id", item.GetOwnerId());
                mContext.startActivity(intent);
            }
        });
        ///body
        feedViewModel.ivIsVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoViewActivity.class);
//                intent.putExtra("url", item.GetAttachURL());
                intent.putExtra("url", PATH + fileName);
                mContext.startActivity(intent);
            }
        });

        feedViewModel.imageView.setOnClickListener(new View.OnClickListener() {
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
        feedViewModel.likecount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LikeUserActivity.class);
                String id = item.GetArticleId();
                intent.putExtra("article_id", id);

                mContext.startActivity(intent);

            }
        });
        feedViewModel.commentcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);

                intent.putExtra("article_id", item.GetArticleId());
                FeedBannerModel fbm = items.get(position).GetFeedBannerModel();
                mContext.startActivity(intent);

            }
        });
        feedViewModel.ivToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                String id = item.GetPosterId();
                intent.putExtra("id", id);

                mContext.startActivity(intent);

            }
        });
        feedViewModel.ivFeedShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder conductor = new AlertDialog.Builder(mContext);
                conductor.setTitle("What do you want?");

                int resId = mContext.getResources().getIdentifier("what_u_want",
                        "array", mContext.getPackageName());
                conductor.setItems(resId, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int index) {
                        int resId1 = mContext.getResources().getIdentifier("what_u_want", "array",
                                mContext.getPackageName());
                        String subject = mContext.getResources().getStringArray(resId1)[index];
                        sharingAction(subject, PATH + fileName, item.GetAttatchType());
                    }
                });
                AlertDialog alert = conductor.create();
                alert.show();
            }
        });


//        bitmap.recycle();    //This will tell Dalvik you no longer need this bitmap.
        return convertView;
    }
    private void setFont(FeedViewModel feedViewModel){

        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(), "Calibri.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(mContext.getAssets(), "Calibri-Bold.ttf");

        feedViewModel.username.setTypeface(custom_font);
        feedViewModel.time.setTypeface(custom_font);

        feedViewModel.mesage.setTypeface(custom_font_bold);
        feedViewModel.likecount.setTypeface(custom_font);
        feedViewModel.commentcount.setTypeface(custom_font);
        feedViewModel.editedBy.setTypeface(custom_font);

    }
    private String countTime(int timeStamp){
        if(timeStamp < 60){
            return String.valueOf(timeStamp) + "s";
        }
        String str = "0s";
        int second = (int)(timeStamp);
        int result = second;

        if((int)(result / 60) < 60){
            str = String.valueOf((int)(result / 60)) + "m";

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
                            str = String.valueOf(result) + "m";
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

    private void sharingAction(String str, String mediaPath, String fileType){
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
        }else if(str.equals("Cancel")){

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

   
    private ArrayList<String> getHashTag(String strTag) {
        ArrayList<String> arrString = new ArrayList<String >();
        String strKey = ",";
        int first = 0;
        int cutNum = 0;
        do{
            int second = strTag.indexOf(strKey);
            int length = strTag.length();
            if(second == -1){

                second = length;
                cutNum = second;
            }else{
                cutNum = second + 1;
            }
            String str3 = strTag.substring(first, second);
            arrString.add("  #" + str3);
//            first = 1;
            strTag = strTag.substring(cutNum, length);
        }while (strTag.contains(strKey));
        if(!strTag.isEmpty()){
            arrString.add("  #" + strTag);
        }

        return arrString;
    }

    private void configureHashTagLayout(LinearLayout llHashTag, LinearLayout llHashTag2, final ArrayList<String> arrHashTag){
        llHashTag.removeAllViews();
        llHashTag2.removeAllViews();
        int count = arrHashTag.size();
        int totalWidthOfLayout = 0;
        for(int i = 0; i < count; i ++){
            View hashTagView = mInflater.inflate(R.layout.item_hash_tag, null);
            TextView tvHashTag = (TextView)hashTagView.findViewById(R.id.tv_hash_tag_item);
            final int finalI = i;
            tvHashTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String strhash = arrHashTag.get(finalI).replace("  #", "");
                    Intent intent = new Intent(mContext.getApplicationContext(), HashTagActivity.class);
                    intent.putExtra("hashcode", strhash);
                    mContext.startActivity(intent);
                }
            });
            tvHashTag.setText(arrHashTag.get(i));
            //get text length ( pixel)
            Rect bounds = new Rect();
            Paint textPaint = tvHashTag.getPaint();
            textPaint.getTextBounds(arrHashTag.get(i),0,arrHashTag.get(i).length(),bounds);
            int heightOfTextView = bounds.height();
            int widthOfTextView = bounds.width();
            //compare total text length with screen width
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llHashTag.getLayoutParams();
            if(totalWidthOfLayout +  widthOfTextView < width){
                llHashTag.addView(hashTagView, i);
                totalWidthOfLayout += widthOfTextView;
            }else if(totalWidthOfLayout + widthOfTextView < 2 * width){
                llHashTag2.addView(hashTagView, i - llHashTag.getChildCount());
//                totalWidthOfLayout = 0;
            }

        }
    }

    private void configureCommentLayout(LinearLayout llComment, final ArrayList<CommentModel> arrComment){
        llComment.removeAllViews();
        int count = arrComment.size();
        for(int i = 0; i < count; i ++){
            View commentView = mInflater.inflate(R.layout.item_feed_comment, null);
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
    private String removeUnderBar(String str){
        return str.replace("_", " ");
    }
    private void setImageViewSize(ImageView imageview){
        ///get screen size
        imageview.setMinimumWidth(width);
        imageview.setMinimumHeight(width);

    }

    private void setLinearLayoutSize(LinearLayout linearLayout){
        ViewGroup.LayoutParams layoutParams = null;
        try{
            layoutParams =  linearLayout.getLayoutParams();
        }catch (Exception e){
            e.printStackTrace();
        }

        layoutParams.width = width;
        layoutParams.height = width;
        linearLayout.setLayoutParams(layoutParams);


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
}
