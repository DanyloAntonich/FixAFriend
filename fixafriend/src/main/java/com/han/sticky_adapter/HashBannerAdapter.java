package com.han.sticky_adapter;

import android.content.Context;
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
import com.han.ImageDownloadHelper.UrlImageViewCallback;
import com.han.ImageDownloadHelper.UrlImageViewHelper;
import com.han.login.R;
import com.han.model.FeedBannerModel;
import com.han.model.FeedModel;

import java.util.List;

/**
 * Created by Administrator on 10/20/2015.
 */
public class HashBannerAdapter implements StickyHeadersAdapter<HashBannerAdapter.ViewHolder2> {
    private List<FeedModel> items;
    private Context mContext;


    public HashBannerAdapter(List<FeedModel> items) {
        this.items = items;

    }

    @Override
    public ViewHolder2 onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_banner, parent, false);
        mContext = parent.getContext();

        return new ViewHolder2(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder2 headerViewHolder, int position) {
        FeedBannerModel objEventHeader = items.get(position).GetFeedBannerModel();

//        headerViewHolder.user_img.setBackgroundDrawable(convertView.getResources().getDrawable(R.drawable.user1));
        headerViewHolder.username.setText(objEventHeader.GetUsername().replace("_", " "));
        headerViewHolder.editNumber.setText(objEventHeader.GetEditNumber() + " edits");
        headerViewHolder.time.setText(countTime(objEventHeader.GetPostedDate()));
        if(objEventHeader.GetPhoto().equals("")){
//            headerViewHolder.user_img.setImageResource(R.drawable.user_web);
            headerViewHolder.user_img.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.user_web));
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
//        imageLoader.displayImage(objEventHeader.GetPhoto(), headerViewHolder.user_img, options);
        setFont(headerViewHolder);
//        headerViewHolder.title.setBackgroundResource(objEventHeader.getImages());
//        headerViewHolder.llmainbg.setBackgroundColor(objEventHeader.getBgColor());
    }

    @Override
    public long getHeaderId(int position) {
//        return (long)position;
        return items.get(position).GetFeedBodyModel().GetFeedId().hashCode();
    }
    private void setFont(ViewHolder2 viewHolder){

        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(),  "Calibri.ttf");

        viewHolder.username.setTypeface(custom_font);
        viewHolder.time.setTypeface(custom_font);

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
    public static class ViewHolder2 extends RecyclerView.ViewHolder {

        //        public ImageView user_img;
        public TextView username;
        public TextView time, editNumber;
        public ImageView user_img;

        public ViewHolder2(View itemView) {
            super(itemView);

            user_img = (ImageView) itemView.findViewById(R.id.iv_user_feed);
//            user_img = (ImageView) itemView.findViewById(R.id.iv_user_feed);
            username = (TextView)itemView.findViewById(R.id.tv_feed_username);
            editNumber = (TextView)itemView.findViewById(R.id.tv_feed_edits);
            time = (TextView)itemView.findViewById(R.id.tv_time_feed);
//
        }

    }

}
