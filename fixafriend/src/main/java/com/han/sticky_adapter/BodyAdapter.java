package com.han.sticky_adapter;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.han.activities.ChatActivity;
import com.han.activities.CommentActivity;
import com.han.activities.HashTagActivity;
import com.han.activities.ImageViewActivity;
import com.han.activities.LikeUserActivity;
import com.han.activities.ProfileFriendActivity;
import com.han.activities.VideoViewActivity;
import com.han.listener.OnEditListener;
import com.han.listener.OnRemoveListener;
import com.han.login.R;
import com.han.main.FeedViewActivity;
import com.han.main.ProfileActivity;
import com.han.model.CommentModel;
import com.han.model.FeedBannerModel;
import com.han.model.FeedBodyModel;
import com.han.model.FeedModel;
import com.han.utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;

/**
 * Created by hic on 11/3/2015.
 */
public class BodyAdapter extends RecyclerView.Adapter<BodyAdapter.ViewHolder> implements OnRemoveListener, OnEditListener {

    private  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator ;

    public List<FeedModel> items;
    private Context mContext;
    private LayoutInflater mInflater;
    private DownloadManager manager ;
    DisplayMetrics display ;
    int width;
    CircleProgressView progressView;

    int num ;

    public BodyAdapter(Context context, ArrayList<FeedModel> mSecitonlist) {
        this.mContext = context;
        this.items = mSecitonlist;
        mInflater = LayoutInflater.from(context);
        ///////
        manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

        /////////
        display = mContext.getResources().getDisplayMetrics();
        width = display.widthPixels;
        setHasStableIds(true);

        num = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feed, viewGroup, false);

        return new ViewHolder(itemView, this);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).hashCode();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        final FeedBodyModel item = items.get(position).GetFeedBodyModel();
        ArrayList<CommentModel> arrComment = item.GetArrCommentModel();
        viewHolder.mesage.setText(getEmoji(item.GetDescription()));

        String strLikeCount = item.GetLikeCount() + " likes";
        viewHolder.likecount.setText(strLikeCount);

        String strCommentCount = item.GetCommentCount() + " comments";
        viewHolder.commentcount.setText(strCommentCount);
        String strEditedBy = "Edited by " + removeUnderBar(item.GetPosterName());
        viewHolder.editedBy.setText(strEditedBy);
        viewHolder.editedBy.setOnClickListener(new View.OnClickListener() {
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
            configureHashTagLayout(viewHolder.llHashTag,viewHolder.llHashTag2, arrHashTag);
        }

        //////////config comments==================================================

        if(arrComment.size() > 0){
            configureCommentLayout(viewHolder.llComment, arrComment);
        }

        ////////////config like or dislike==================================================

        if(item.GetIslike().equals("1")){
//            viewModel.imageViewLikes.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.liked_2x));
            viewHolder.imageViewLikes.setImageResource(R.drawable.liked_3x);
        }else if(item.GetIslike().equals("0")){
//            viewModel.imageViewLikes.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.like_2x));
            viewHolder.imageViewLikes.setImageResource(R.drawable.like_3x);
        }
        viewHolder.imageViewLikes.setOnClickListener(new View.OnClickListener() {
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
        ////////////set article image ==============================================================

//        viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.feed));
        setImageViewSize(viewHolder.imageView);

        final String fileName = item.GetAttachURL().substring(item.GetAttachURL().lastIndexOf('/') + 1);
        File file = new File(PATH, fileName);
        if(!file.exists()){
            if (mContext instanceof FeedViewActivity) {

                viewHolder.imageView.setVisibility(View.GONE);
                viewHolder.ivIsVideo.setVisibility(View.GONE);
                viewHolder.editedBy.setVisibility(View.GONE);
                viewHolder.llCircleProgress.setVisibility(View.VISIBLE);

                setLinearLayoutSize(viewHolder.llCircleProgress);
//                downloadQueueManager.insert(position, viewHolder.llCircleProgress);
//                downloadTask(position);
               if(num < position){
                   num = position;
                   ((FeedViewActivity) mContext).download(num, viewHolder.llCircleProgress);

               }
            }
        }else {
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.editedBy.setVisibility(View.VISIBLE);
            viewHolder.llCircleProgress.setVisibility(View.GONE);


            if(item.GetAttatchType().equals("1")){
                viewHolder.ivIsVideo.setVisibility(View.GONE);

                if(item.GetBitmap() == null){

                    Bitmap bitmap = BitmapFactory.decodeFile(PATH + fileName);
                    viewHolder.imageView.setImageBitmap(bitmap);

                    items.get(position).GetFeedBodyModel().SetBitmap(bitmap);
                    items.get(position).GetFeedBodyModel().SetFlag(true);
                }else {
                         viewHolder.imageView.setImageBitmap(item.GetBitmap());
                }
            }else {
                viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.feed));
                viewHolder.ivIsVideo.setVisibility(View.VISIBLE);

            }
        }

        ///////////////set click listener==============================================================
        viewHolder.ivIsVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoViewActivity.class);
//                intent.putExtra("url", item.GetAttachURL());
                intent.putExtra("url", PATH + fileName);
                mContext.startActivity(intent);
            }
        });

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
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
        viewHolder.likecount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LikeUserActivity.class);
                String id = item.GetArticleId();
                intent.putExtra("article_id", id);

                mContext.startActivity(intent);

            }
        });
        viewHolder.commentcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);

                intent.putExtra("article_id", item.GetArticleId());
                FeedBannerModel fbm = items.get(position).GetFeedBannerModel();
                mContext.startActivity(intent);

            }
        });
        viewHolder.ivToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                String id = item.GetPosterId();
                intent.putExtra("id", id);

                mContext.startActivity(intent);

            }
        });
        viewHolder.ivFeedShare.setOnClickListener(new View.OnClickListener() {
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

    public void setBitmap(Bitmap bitmap, int position){
        if(bitmap != null){
            items.get(position).GetFeedBodyModel().SetBitmap(bitmap);
        }
        items.get(position).GetFeedBodyModel().SetFlag(true);
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

            Rect bounds = new Rect();
            Paint textPaint = tvHashTag.getPaint();
            textPaint.getTextBounds(arrHashTag.get(i),0,arrHashTag.get(i).length(),bounds);
            int heightOfTextView = bounds.height();
            int widthOfTextView = bounds.width();

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llHashTag.getLayoutParams();
            if(totalWidthOfLayout +  widthOfTextView< width){
                llHashTag.addView(hashTagView, i);
                totalWidthOfLayout += widthOfTextView;
            }else {
                llHashTag2.addView(hashTagView);
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
            final int finalI = i;
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent  = new Intent(mContext.getApplicationContext(), ProfileFriendActivity.class);
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
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onRemove(int position) {
//        personDataProvider.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onEdit(int position) {

    }

//    @Override
//    public void onEdit(final int position) {
//        final EditText edit = new EditText(mContext);
//        edit.setTextColor(Color.BLACK);
////        edit.setText(personDataProvider.getItems().get(position));
//        new AlertDialog.Builder(mContext).setTitle(R.string.ap).setView(edit).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String name = edit.getText().toString();
////                personDataProvider.update(position, name);
//                notifyItemChanged(position);
//            }
//        }).create().show();
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private OnRemoveListener removeListener;
        private OnEditListener editListener;

        public ImageView imageViewLikes;
        public ImageView imageView;
        public ImageView ivToChat;
        public ImageView ivFeedShare;
        public ImageView ivIsVideo;

        public TextView username;
        public TextView mesage;
        public TextView time;
        public TextView commentcount;
        public TextView likecount;
        public TextView editedBy;

        public LinearLayout llHashTag;
        public LinearLayout llHashTag2;
        public LinearLayout llComment;
        public RelativeLayout relativeLayout;
        public LinearLayout llCircleProgress;

        private OnRemoveListener listener;
        final Context mContext;

        public ViewHolder(View itemView, BodyAdapter personAdapter) {
            super(itemView);
//            this.label = (TextView) itemView.findViewById(R.id.name);
            this.removeListener = personAdapter;
            this.editListener = personAdapter;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mContext = itemView.getContext();

            this.listener = listener;

            imageView = (ImageView)itemView.findViewById(R.id.iv_feed_image);
            imageViewLikes = (ImageView)itemView.findViewById(R.id.iv_feed_likes_1);
            ivToChat = (ImageView)itemView.findViewById(R.id.iv_feed_comment_1);
            ivFeedShare = (ImageView)itemView.findViewById(R.id.iv_feed_share);
            ivIsVideo = (ImageView)itemView.findViewById(R.id.iv_isvideo4);

            mesage = (TextView)itemView.findViewById(R.id.tv_feed_message);
            likecount = (TextView)itemView.findViewById(R.id.tv_feed_likes);
            commentcount = (TextView)itemView.findViewById(R.id.tv_feed_comment);

            editedBy = (TextView)itemView.findViewById(R.id.tv_feed_edited_by);

            llComment = (LinearLayout)itemView.findViewById(R.id.ll_feed_comment);
            llHashTag = (LinearLayout)itemView.findViewById(R.id.ll_hash_tag);
            llHashTag2 = (LinearLayout)itemView.findViewById(R.id.ll_hash_tag2);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
            llCircleProgress = (LinearLayout)itemView.findViewById(R.id.linear_circle_progress);


            ivFeedShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "FAF");
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Example text");

                    mContext.startActivity(Intent.createChooser(shareIntent, "fixafriend"));
                }
            });

            setFont();
        }
        private void setFont(){

            Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(),  "Calibri.ttf");
            Typeface custom_font_bold = Typeface.createFromAsset(mContext.getAssets(), "Calibri-Bold.ttf");
            mesage.setTypeface(custom_font_bold);
            likecount.setTypeface(custom_font);
            commentcount.setTypeface(custom_font);
            editedBy.setTypeface(custom_font);
//            comment.setTypeface(custom_font);

        }

        @Override
        public void onClick(View v) {
//            removeListener.onRemove(getPosition());
        }

        @Override
        public boolean onLongClick(View v) {
//            editListener.onEdit(getPosition());
            return true;
        }
    }
}
