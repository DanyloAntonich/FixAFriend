package com.han.adapter;
/**
 * Created by Administrator on 9/29/2015.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.han.ImageDownloadHelper.UrlImageViewCallback;
import com.han.ImageDownloadHelper.UrlImageViewHelper;
import com.han.login.R;
import com.han.main.SearchActivity;
import com.han.model.UserModel;
import com.han.widget.CircularImageView;

import java.io.File;
import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter {
    private static ArrayList<UserModel> listContact = new ArrayList<UserModel>();

    private  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator ;

    Context mContext;
    private LayoutInflater mInflater;

    int num;

    public SearchAdapter(Context searchFragment, ArrayList<UserModel> results){
        listContact = results;
        mInflater = LayoutInflater.from(searchFragment);

        mContext = searchFragment;

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


    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stubbybersAdapter.java
        View v = convertView;
        if(v == null){
            v = mInflater.inflate(R.layout.item_search, null);
        }

        final UserModel item = listContact.get(position);
//        CircularImageView product_img = (CircularImageView) v.findViewById(R.id.civ_search);
//        imageLoader.displayImage(Constant.BASE_URL + item.GetPhoto(), product_img, options);
        final RelativeLayout rlSearch = (RelativeLayout)v.findViewById(R.id.rl_search);
        CircularImageView imageView = (CircularImageView)v.findViewById(R.id.civ_search);
        TextView user_name = (TextView)v.findViewById(R.id.tv_username);

        final TextView btn_follow = (TextView)v.findViewById(R.id.follow);

        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.GetIsFollowing().equals("1")) {
                    if(mContext instanceof SearchActivity){
                        ((SearchActivity) mContext).changeFollow(item, position, false);
                   }

                } else {
                    if(mContext instanceof SearchActivity){
                        ((SearchActivity) mContext).changeFollow(item, position, true);
                    }

                }
            }
        });

        user_name.setText(item.GetName().replace("_", " "));

        ////Set user image=========================================================
//        final String fileName = item.GetPhoto().substring(item.GetPhoto().lastIndexOf('/') + 1);

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

        ////////=================================================================================
        if(item.GetIsFollowing().equals("1")){
            btn_follow.setTextColor(v.getResources().getColor(R.color.green));
            btn_follow.setText("Following");
            rlSearch.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.follow));
        } else {
            btn_follow.setTextColor(v.getResources().getColor(R.color.black_color));
            btn_follow.setText("Follow");
            rlSearch.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.unfollow));;
        }

        return v;
    }

    public void setBitmap(Bitmap bitmap, int position){
        if(bitmap != null){
            listContact.get(position).SetBitmap(bitmap);
        }
    }

}
