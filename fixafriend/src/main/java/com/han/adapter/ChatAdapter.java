package com.han.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.han.ImageDownloadHelper.UrlImageViewCallback;
import com.han.ImageDownloadHelper.UrlImageViewHelper;
import com.han.login.R;
import com.han.model.ChatModel;
import com.han.utils.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Administrator on 9/29/2015.
 */
public class ChatAdapter extends ArrayAdapter<ChatModel> {
    private ArrayList<ChatModel> itemList;
    private Context context;

    private LayoutInflater mInflater;


    String id = "";

    public ChatAdapter(Context chatActivity, ArrayList<ChatModel> historyChatList_tmp) {

        // TODO Auto-generated constructor stub
        super(chatActivity, android.R.layout.simple_list_item_1, historyChatList_tmp);
//			this.itemList = itemList;
        this.itemList = historyChatList_tmp;
        this.context = chatActivity;

        SharedPreferences pref = Utilities.getSharedPreferences(context);
        id = pref.getString("user_id", null);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.item_chat, null);
        }
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "Calibri.ttf");

        final ChatModel item = itemList.get(position);

        ImageView iv_receivemessage = (ImageView) convertView.findViewById(R.id.iv_chat_item);
//        ImageView iv_receivemessage = (ImageView)convertView.findViewById(R.id.iv_chat_item);
        TextView txt_receivemessage = (TextView ) convertView.findViewById(R.id.tv_from);
        txt_receivemessage.setTypeface(custom_font);
        TextView txt_sendmessage = (TextView) convertView.findViewById(R.id.tv_to);
        txt_sendmessage.setTypeface(custom_font);
        TextView txt_send_time = (TextView) convertView.findViewById(R.id.tv_time);
        txt_send_time.setTypeface(custom_font);

        iv_receivemessage.setVisibility(View.GONE);
        txt_receivemessage.setVisibility(View.GONE);
        txt_sendmessage.setVisibility(View.GONE);


        ///i sent
        if (item.GetSenderId().equals(id)) {

            txt_sendmessage.setText(getEmoji(item.GetMessage()));

            txt_send_time.setText(getTime(item.GetTime()));

            txt_sendmessage.setVisibility(View.VISIBLE);


//            CircularImageView product_img = (CircularImageView) convertView.findViewById(R.id.civ_comment);
//            imageLoader.displayImage(Constant.BASE_URL + data.GetPhoto(), product_img, options);


        } else { // Received message

//            iv_receivemessage.setBackgroundDrawable(convertView.getResources().getDrawable(R.drawable.user1));

            txt_receivemessage.setText(getEmoji(item.GetMessage()));

            txt_send_time.setText(getTime(item.GetTime()));

            iv_receivemessage.setVisibility(View.VISIBLE);
            txt_receivemessage.setVisibility(View.VISIBLE);


//            CircularImageView product_img = (CircularImageView) convertView.findViewById(R.id.civ_comment);
            if(item.GetPhotoURL().equals("")){
                iv_receivemessage.setImageResource(R.drawable.user_web);
//            imageView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.user_web));
            }else{
//                imageLoader.displayImage(item.GetPhotoURL(), iv_receivemessage);
//            new ImageLoad().loadImage(item.GetPhoto(), imageView);
                if(item.GetBitmap() != null){
                    iv_receivemessage.setImageBitmap(item.GetBitmap());
                }else {
                    UrlImageViewHelper.setUrlDrawable(iv_receivemessage, item.GetPhotoURL(), R.drawable.loading, new UrlImageViewCallback() {
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
//                    getBitmap(position);
//                    iv_receivemessage.setImageBitmap(item.GetBitmap());
                }
            }
//            imageLoader.displayImage(item.GetPhotoURL(), iv_receivemessage, options);


        }

        return convertView;
    }

    private String getTime(int stamp){
        long millis =stamp * 1000;
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("EE, hh:mm:ss,a",Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(date);
        return  formattedDate;
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
