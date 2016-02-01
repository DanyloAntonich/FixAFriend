package com.han.sticky_adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.eowise.recyclerview.stickyheaders.StickyHeadersTouchListener;
import com.han.ImageDownloadHelper.UrlImageViewCallback;
import com.han.ImageDownloadHelper.UrlImageViewHelper;
import com.han.activities.ProfileFriendActivity;
import com.han.login.R;
import com.han.main.FeedViewActivity;
import com.han.model.FeedBannerModel;
import com.han.model.FeedBodyModel;
import com.han.model.FeedModel;
import com.han.utils.Utilities;

import java.util.List;

/**
 * Created by hic on 11/3/2015.
 */
public class HeaderAdapter  implements StickyHeadersAdapter<HeaderAdapter.ViewHolder5> {

//    private List<String> items;

    private List<FeedModel> items;
    private Context mContext;


    UrlImageViewHelper urlImageViewHelper;

    public HeaderAdapter(List<FeedModel> items) {
        this.items = items;
    }

    @Override
    public ViewHolder5 onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_banner, parent, false);
        mContext = parent.getContext();



        urlImageViewHelper = new UrlImageViewHelper();

        return new ViewHolder5(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder5 headerViewHolder, final int position) {

        final FeedBannerModel objEventHeader = items.get(position).GetFeedBannerModel();

        headerViewHolder.username.setText("  " + objEventHeader.GetUsername());
        headerViewHolder.editNumber.setText(objEventHeader.GetEditNumber() + " Edits");
        headerViewHolder.time.setText(countTime(objEventHeader.GetPostedDate()));

        headerViewHolder.user_img.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.user_web));
        if(objEventHeader.GetPhoto().equals("")){
//            headerViewHolder.user_img.setImageResource(R.drawable.user_web);


        }else{

            UrlImageViewHelper.setUrlDrawable(headerViewHolder.user_img, objEventHeader.GetPhoto(), R.drawable.loading, new UrlImageViewCallback() {
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
//        headerViewHolder.user_img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext.getApplicationContext(), ProfileFriendActivity.class);
//                FeedBodyModel fbm = items.get(position).GetFeedBodyModel();
//                intent.putExtra("userid", fbm.GetPosterId());
//                mContext.startActivity(intent);
//            }
//        });
//        headerViewHolder.username.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext.getApplicationContext(), ProfileFriendActivity.class);
//                FeedBodyModel fbm = items.get(position).GetFeedBodyModel();
//                intent.putExtra("userid", fbm.GetPosterId());
//                mContext.startActivity(intent);
//            }
//        });
//        headerViewHolder.editNumber.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext.getApplicationContext(), FeedViewActivity.class);
//                FeedBodyModel fbm = items.get(position).GetFeedBodyModel();
//                intent.putExtra("mode", 2);
//                intent.putExtra("article_id", fbm.GetArticleId());
//                mContext.startActivity(intent);
//            }
//        });
        setFont(headerViewHolder);
    }


    @Override
    public long getHeaderId(int position) {
//        return items.get(position).subSequence(0, 2).hashCode();
//        return items.get(position).GetFeedBodyModel().GetFeedId().hashCode();
        return position;
    }

    private void setFont(ViewHolder5 viewHolder){

        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(), "Calibri.ttf");

        viewHolder.username.setTypeface(custom_font);
        viewHolder.time.setTypeface(custom_font);

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

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.tv_feed_edits:
//                break;
//            case R.id.iv_user_feed:
//                break;
//
//        }
//    }

    public static class ViewHolder5 extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView time, editNumber;
        public ImageView user_img;

        public ViewHolder5(View itemView) {
            super(itemView);

            user_img = (ImageView) itemView.findViewById(R.id.iv_user_feed);
            username = (TextView)itemView.findViewById(R.id.tv_feed_username);
            editNumber = (TextView)itemView.findViewById(R.id.tv_feed_edits);
            time = (TextView)itemView.findViewById(R.id.tv_time_feed);
        }



    }
}
