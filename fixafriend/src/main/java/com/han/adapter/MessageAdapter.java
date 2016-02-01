package com.han.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.han.ImageDownloadHelper.UrlImageViewCallback;
import com.han.ImageDownloadHelper.UrlImageViewHelper;
import com.han.login.R;
import com.han.model.MessageModel;

import java.util.ArrayList;

/**
 * Created by Administrator on 9/29/2015.
 */
public class MessageAdapter extends BaseAdapter{
    private static ArrayList<MessageModel> listContact = new ArrayList<MessageModel>();

    private LayoutInflater mInflater;

    Context mContext;

    public MessageAdapter(Context photosFragment, ArrayList<MessageModel> results){
        listContact = results;
        mContext = photosFragment;
        mInflater = LayoutInflater.from(photosFragment);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listContact.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return listContact.get(arg0);
//			 return new Windows();
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stubbybersAdapter.java
        View v = convertView;
        if(v == null){
            v = mInflater.inflate(R.layout.item_message, null);
        }
        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(),  "Calibri.ttf");
        MessageModel item = listContact.get(position);
//        CircularImageView product_img = (CircularImageView) v.findViewById(R.id.civ_message);
//        imageLoader.displayImage(Constant.BASE_URL + item.GetPhoto(), product_img, options);
        ImageView imageView = (ImageView)v.findViewById(R.id.civ_message);
        ImageView ivGo = (ImageView)v.findViewById(R.id.iv_message_go);
        TextView username = (TextView)v.findViewById(R.id.tv_username_message);
        username.setTypeface(custom_font);
        TextView message = (TextView)v.findViewById(R.id.tv_message);
        message.setTypeface(custom_font);
        TextView notifyNum = (TextView)v.findViewById(R.id.txt_invitation_cnt_message);
        notifyNum.setTypeface(custom_font);


        username.setText(item.GetName().replace("-", " "));
        message.setText(item.GetMessage());
//        imageView.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.user1));
        if(item.GetPhotoURL().equals("")){
//            imageView.setImageResource(R.drawable.user_web);
            imageView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.user_web));
        }else{
//            imageLoader.displayImage(item.GetPhotoURL(), imageView);
//            new ImageLoad().loadImage(item.GetPhoto(), imageView);
            if(item.GetBitmap() != null){
                imageView.setImageBitmap(item.GetBitmap());
            }else {

                UrlImageViewHelper.setUrlDrawable(imageView, item.GetPhotoURL(), R.drawable.loading, new UrlImageViewCallback() {
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
        }
        int notificationNum = Integer.valueOf(item.GetUnreadCount());
        if(notificationNum > 0){
            ivGo.setVisibility(View.INVISIBLE);
            notifyNum.setVisibility(View.VISIBLE);
            notifyNum.setText(item.GetUnreadCount());
        }
//        imageLoader.displayImage(item.GetPhotoURL(), imageView, options);
        return v;
    }
//    private Bitmap getBitmap(int pos){
//        Bitmap bitmap = urlImageViewHelper.loadBitmapFromStream(mContext, listContact.get(pos).GetPhotoURL(), "userImage.jpg", 60, 60);
////        bitmap =  imageLoader.loadImageSync(listContact.get(pos).GetPhotoURL());
//        listContact.get(pos).SetBitmap(bitmap);
//        return bitmap;
//    }

}
