package com.example.rmk.mediaediting.GallerySet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rmk.mediaediting.R;
import java.util.ArrayList;

/**
 * Created by RMK on 10/30/2015.
 */
public class GeneralGalleryAdapter extends BaseAdapter {
    private Activity activity;
    private static LayoutInflater inflater = null;

    private String[] Names = new String[5];
    private Bitmap[] mImageId = new Bitmap[5];

    public GeneralGalleryAdapter(Activity a, String[] names, Bitmap[] imageId){
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Names = names;
        mImageId = imageId;
    }
    @Override
    public int getCount() {
        return mImageId.length;
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public TextView text;
        public ImageView image;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ViewHolder holder;
        if(convertView==null){
            vi = inflater.inflate(R.layout.gallerycell, null);
            holder=new ViewHolder();
            holder.text=(TextView)vi.findViewById(R.id.gallery_text);
            holder.image=(ImageView)vi.findViewById(R.id.gallery_image);
            vi.setTag(holder);
        }
        else
            holder=(ViewHolder)vi.getTag();

//        final int stub_id= mImageId[position];
        holder.image.setImageBitmap(mImageId[position]);
        holder.text.setText(Names[position]);

        return vi;
    }
}
