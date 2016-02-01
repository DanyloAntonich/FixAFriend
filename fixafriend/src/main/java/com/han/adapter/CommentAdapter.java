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
import com.han.model.CommentModel;

import java.util.ArrayList;

/**
 * Created by Administrator on 9/29/2015.
 */
public class CommentAdapter extends BaseAdapter {
    private static ArrayList<CommentModel> listContact = new ArrayList<CommentModel>();

    private LayoutInflater mInflater;

    Context mContext;
    public CommentAdapter(Context photosFragment, ArrayList<CommentModel> results){
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
            v = mInflater.inflate(R.layout.item_comment, null);
        }
        Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(),  "Calibri.ttf");
        CommentModel item = listContact.get(position);
//        CircularImageView product_img = (CircularImageView) v.findViewById(R.id.civ_comment);
//        imageLoader.displayImage(Constant.BASE_URL + item.GetPhoto(), product_img, options);
        ImageView imageView = (ImageView)v.findViewById(R.id.civ_comment);
        TextView user_name = (TextView)v.findViewById(R.id.tv_username_comment);
        user_name.setTypeface(custom_font);
        TextView mesage = (TextView)v.findViewById(R.id.tv_comment_message);
        mesage.setTypeface(custom_font);
        TextView time = (TextView)v.findViewById(R.id.tv_comment_time);
        time.setTypeface(custom_font);

//        imageView.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.user1));
        if(item.GetPhotoURL().equals("")){
//            imageView.setImageResource(R.drawable.user_web);
            imageView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.user_web));
        }else{
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
        user_name.setText(item.GetName().replace("_", " "));
        mesage.setText(getEmoji(item.GetComment()));
        time.setText(countTime(item.GetTime()));
        return v;
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
//    public String getEmoji1(String str) {
//        String strKey = "\\\\ud83d";
//        while(str.contains(strKey)) {
//
//            int start = str.indexOf(strKey);
//            String str3 = str.substring(start , start + 14);
//            ///get emoji from string
//            String str4 = getUnicode1(str3);
//            //replace string with emoji
//            String result = str.replace(str3, str4);
//            str = result;
//
//        }
//        return str;
//    }
//    public String getUnicode1(String myString) {
//        String str = myString.split(" ")[0];
//        str = str.replace("\\\\","");
//        String[] arr = str.split("u");
//        String text = "";
//        for(int i = 1; i < arr.length; i++){
//            int hexVal = Integer.parseInt(arr[i], 16);
//            text += (char)hexVal;
//        }
//        return text;
//    }
}
