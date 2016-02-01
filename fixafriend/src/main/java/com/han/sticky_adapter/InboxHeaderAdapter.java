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
import com.han.model.InboxModel;

import java.util.List;

/**
 * Created by Administrator on 10/9/2015.
 */
public class InboxHeaderAdapter implements StickyHeadersAdapter<InboxHeaderAdapter.ViewHolder1> {
    private List<InboxModel> items;
    private Context mContext;


    public InboxHeaderAdapter(List<InboxModel> items) {
        this.items = items;
    }

    @Override
    public ViewHolder1 onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inbox_banner, parent, false);
        mContext = parent.getContext();

        return new ViewHolder1(itemView);
    }



    @Override
    public void onBindViewHolder(ViewHolder1 headerViewHolder, int position) {
        InboxModel inboxHeader = items.get(position);
        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(), "Calibri.ttf");

        headerViewHolder.username.setText(inboxHeader.GetName().replace("_", " "));
        headerViewHolder.username.setTypeface(custom_font);
        headerViewHolder.time.setText(countTime(inboxHeader.GetLimite(), inboxHeader.GetSendDate()));
        headerViewHolder.time.setTypeface(custom_font);
        if(inboxHeader.GetPhotoURL().equals("")){
            headerViewHolder.user_img.setImageResource(R.drawable.user_web);
//            imageView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.user_web));
        }else{
            UrlImageViewHelper.setUrlDrawable( headerViewHolder.user_img, inboxHeader.GetPhotoURL(), R.drawable.loading, new UrlImageViewCallback() {
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
//        imageLoader.displayImage(inboxHeader.GetPhotoURL(), headerViewHolder.user_img, options);
    }

    @Override
    public long getHeaderId(int position) {
        return (long)position;
    }
    private String countTime(int limit, int send_date){
        int leftTime = limit - send_date;
        int second = leftTime % 60;
        int minute = ((int)(leftTime / 60)) % 60;
        int hour = (int)(leftTime / 3600);

        String str = String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(second) + "  Remain";

        return str;
    }
    public static class ViewHolder1 extends RecyclerView.ViewHolder {

        public ImageView user_img;
        public TextView username;
        public TextView time;

        public ViewHolder1(View itemView) {
            super(itemView);

            user_img = (ImageView) itemView.findViewById(R.id.iv_user_inbox);
            username = (TextView)itemView.findViewById(R.id.tv_inbox_username);
            time = (TextView)itemView.findViewById(R.id.tv_time_inbox);
        }
    }
}
