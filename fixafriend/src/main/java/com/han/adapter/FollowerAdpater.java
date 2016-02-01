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
import com.han.model.UserModel;

import java.util.ArrayList;

/**
 * Created by Administrator on 10/19/2015.
 */
public class FollowerAdpater extends BaseAdapter {
    private static ArrayList<UserModel> listContact = new ArrayList<UserModel>();

    private LayoutInflater mInflater;

    Context mContext;
    public FollowerAdpater(Context photosFragment, ArrayList<UserModel> results){
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
            v = mInflater.inflate(R.layout.item_like_user, null);
        }
        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(),  "Calibri.ttf");
        UserModel item = listContact.get(position);
//        CircularImageView product_img = (CircularImageView) v.findViewById(R.id.civ_like_user);
//        imageLoader.displayImage(Constant.BASE_URL + item.GetPhoto(), product_img, options);

        ImageView user_img = (ImageView) v.findViewById(R.id.civ_likeuser);

        TextView user_name = (TextView)v.findViewById(R.id.tv_likeuser_username);
        user_name.setTypeface(custom_font);


//        imageLoader.displayImage(item.GetPhoto(), user_img, options);
        if(item.GetPhoto().equals("")){
//            user_img.setImageResource(R.drawable.user_web);
            user_img.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.user_web));
        }else{
//            imageLoader.displayImage(item.GetPhoto(), user_img);
//            new ImageLoad().loadImage(item.GetPhoto(), imageView);
            if(item.GetBitmap() != null){
                user_img.setImageBitmap(item.GetBitmap());
            }else {
                UrlImageViewHelper.setUrlDrawable(user_img, item.GetPhoto(), R.drawable.loading, new UrlImageViewCallback() {
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

        user_name.setText(item.GetName().replace("_", " "));

        return v;
    }

}
