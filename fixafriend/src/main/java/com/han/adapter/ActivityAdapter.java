package com.han.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Environment;
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
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 9/30/2015.
 */
public class ActivityAdapter extends BaseAdapter {

    private  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator ;

    private static ArrayList<UserModel> listContact = new ArrayList<UserModel>();
    private LayoutInflater mInflater;

    Context mContext;

    int num;

    public ActivityAdapter(Context photosFragment, ArrayList<UserModel> results){
        listContact = results;
        mContext = photosFragment;
        mInflater = LayoutInflater.from(photosFragment);



        num = -1;

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
            v = mInflater.inflate(R.layout.item_acitivity, null);
        }
        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(),  "Calibri.ttf");
        UserModel item = listContact.get(position);
//        CircularImageView product_img = (CircularImageView) v.findViewById(R.id.civ_message);
//        imageLoader.displayImage(Constant.BASE_URL + item.GetPhoto(), product_img, options);
        ImageView imageView = (ImageView)v.findViewById(R.id.civ_activity);
        TextView username = (TextView)v.findViewById(R.id.tv_activity_username);
        username.setTypeface(custom_font);
        TextView message = (TextView)v.findViewById(R.id.tv_activity_message);
        message.setTypeface(custom_font);
        TextView time = (TextView)v.findViewById(R.id.tv_activity_time);
        time.setTypeface(custom_font);

        username.setText(item.GetName().replace("_", " "));
        message.setText(item.GetMessage());
        time.setText(countTime(item.GetTime()));

        ////Set user image=========================================================

        if(item.GetPhoto().equals("")){
//            imageView.setImageResource(R.drawable.user_web);
            imageView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.user_web));
        }else{
            UrlImageViewHelper.setUrlDrawable(imageView, item.GetPhoto(), R.drawable.loading, new UrlImageViewCallback() {
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
        if(item.GetMessage().contains("request")){
            v.setBackgroundColor(mContext.getResources().getColor(R.color.light_green));
        }else {
            v.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }

        ////////=================================================================================
//
        return v;
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
}
