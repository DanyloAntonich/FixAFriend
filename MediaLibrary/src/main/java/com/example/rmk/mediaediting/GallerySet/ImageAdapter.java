package com.example.rmk.mediaediting.GallerySet;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphics.MenuTool;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by RMK on 10/29/2015.
 */
public class ImageAdapter extends BaseAdapter{

    private Activity activity;
    private static LayoutInflater inflater = null;

    private ArrayList<String> Names = new ArrayList<String>();
    private Integer[] mImageId = new Integer[30];

    public ImageAdapter(Activity a, ArrayList<String> names, Integer[] imageId){
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Names = names;
        mImageId = imageId;
    }
    @Override
    public int getCount() {
        return MenuTool.ImageIds.length;
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

    @Override
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

        final int stub_id=MenuTool.ImageIds[position];
        holder.image.setImageResource(stub_id);
        holder.text.setText(MenuTool.Names.get(position));

        return vi;
    }
}
