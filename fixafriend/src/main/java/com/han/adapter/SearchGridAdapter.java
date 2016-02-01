package com.han.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.DisplayMetrics;
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
import com.han.main.FeedViewActivity;
import com.han.main.SearchActivity;
import com.han.model.FeedBodyModel;
import com.han.model.FeedModel;
import com.han.model.SearchGridViewModel;
import com.han.model.UserModel;
import com.han.widget.CircularImageView;

import java.io.File;
import java.util.ArrayList;

import at.grabner.circleprogress.CircleProgressView;

/**
 * Created by hic on 2015-11-20.
 */
public class SearchGridAdapter extends BaseAdapter {
    private static ArrayList<FeedModel> listContact = new ArrayList<FeedModel>();

    private  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator ;

    Context mContext;
    private LayoutInflater mInflater;

    int num;
    private int screenWidth = 0;

    public SearchGridAdapter(Context searchFragment, ArrayList<FeedModel> results) {
        listContact = results;
        mInflater = LayoutInflater.from(searchFragment);

        mContext = searchFragment;

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
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
            v = mInflater.inflate(R.layout.item_search_grid, null);
        }

        final FeedBodyModel item = listContact.get(position).GetFeedBodyModel();

        SearchGridViewModel searchGridViewModel = new SearchGridViewModel();

        searchGridViewModel.circularImageView = (CircularImageView)v.findViewById(R.id.civ_search_grid);
        setImageViewSize(searchGridViewModel.circularImageView);

        searchGridViewModel.ivIsVideo = (ImageView)v.findViewById(R.id.iv_isvideo_search);

        searchGridViewModel.relativeLayout = (RelativeLayout)v.findViewById(R.id.rl_search_article);
        setRelativeLayoutSize(searchGridViewModel.relativeLayout);

        searchGridViewModel.circleProgressView = (CircleProgressView)v.findViewById(R.id.kcjCircleView0);



        ////Set  image=========================================================
//        final String fileName = item.GetPhoto().substring(item.GetPhoto().lastIndexOf('/') + 1);

        ///set image
        Bitmap   bitmap = null;
//        Bitmap thumbnail = null;
//        Drawable d = null;
//        searchGridViewModel.circularImageView.setImageDrawable(d);

        final String fileName = listContact.get(position).GetFeedBodyModel().GetAttachURL().substring(listContact.get(position).GetFeedBodyModel().GetAttachURL().lastIndexOf('/') + 1);
        if(!checkFileExist(fileName)){
            searchGridViewModel.circleProgressView.setVisibility(View.VISIBLE);
            searchGridViewModel.circularImageView.setVisibility(View.GONE);
            searchGridViewModel.ivIsVideo.setVisibility(View.GONE);
            try{
                ((SearchActivity) mContext).download(position, searchGridViewModel);
            }catch (Exception e){
                e.printStackTrace();
            }

        }else {
            searchGridViewModel.circularImageView.setVisibility(View.VISIBLE);
            searchGridViewModel.circleProgressView.setVisibility(View.GONE);
            if(listContact.get(position).GetFeedBodyModel().GetAttatchType().equals("2")){
                searchGridViewModel.ivIsVideo.setVisibility(View.VISIBLE);
                /////make thumbnail and
//                    searchGridViewModel.circularImageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle2));
                searchGridViewModel.circularImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.circle2));
            }else {
                searchGridViewModel.ivIsVideo.setVisibility(View.GONE);

                try{
                    ///get bitmap from local storeage

                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(PATH + fileName, options);
                    //////
                    options.inSampleSize = 7;
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(PATH + fileName, options);

                }catch (Exception e){
                    e.printStackTrace();
                }

                if(bitmap != null){
                    searchGridViewModel.circularImageView.setImageBitmap(bitmap);

                }else {
                    searchGridViewModel.circularImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.circle2));
                }
                /////////////////////////////////////
//                if(PATH + fileName != null && PATH + fileName != "") //<--CHECK FILENAME IS NOT NULL
//                {
//                    File f = new File(PATH + fileName);
//                    if(f.exists()) // <-- CHECK FILE EXISTS OR NOT
//                    {
//                        d = Drawable.createFromPath(PATH + fileName);
//                        searchGridViewModel.circularImageView.setImageDrawable(d);
//
//                    } else {
//                        searchGridViewModel.circularImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.circle2));
//                    }
//                }
            }
        }
        searchGridViewModel.circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FeedViewActivity.class);
                intent.putExtra("mode", 1);
                intent.putExtra("article_id", listContact.get(position).GetFeedBodyModel().GetArticleId());
                mContext.startActivity(intent);
            }
        });

        return v;
    }

    private boolean checkFileExist(String fileName){

        File file = new File(PATH, fileName);
        if(file.exists()){
            return true;
        }else
            return false;
    }
    private void setImageViewSize(CircularImageView imageview){

        imageview.setMaxWidth((int)(screenWidth / 3) - 5);
        imageview.setMaxHeight((int) (screenWidth / 3) - 5);

    }

    private void setRelativeLayoutSize(RelativeLayout relativeLayout){
        ViewGroup.LayoutParams layoutParams = null;
        try{
            layoutParams =  relativeLayout.getLayoutParams();
        }catch (Exception e){
            e.printStackTrace();
        }
        layoutParams.width = screenWidth / 3;
        layoutParams.height = screenWidth / 3;


        relativeLayout.setLayoutParams(layoutParams);


    }


}
