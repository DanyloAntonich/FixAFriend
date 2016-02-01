package com.han.sticky_adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;

import com.han.activities.ChatActivity;
import com.han.activities.CommentActivity;
import com.han.activities.HashTagActivity;
import com.han.activities.ImageViewActivity;
import com.han.activities.LikeUserActivity;
import com.han.activities.VideoViewActivity;
import com.han.login.R;
import com.han.listener.OnRemoveListener;
import com.han.model.CommentModel;
import com.han.model.FeedBannerModel;
import com.han.model.FeedBodyModel;
import com.han.model.FeedModel;
import com.han.model.FeedViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 10/20/2015.
 */
public class HashBodyAdapter extends RecyclerView.Adapter<HashBodyAdapter.ViewHolder> implements OnRemoveListener {

    private  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator ;


    public List<FeedModel> items;
    //private PersonDataProvider personDataProvider;
    FeedViewModel viewModel;
    private LayoutInflater mInflater;

    Context mContext;
    //////////////////////////////
    private MediaController mController;

    MediaMetadataRetriever mMetadataRetriever;

    DisplayMetrics display ;
    int width;
    /////////////////////////////////////
    public HashBodyAdapter(Context context, ArrayList<FeedModel> mSecitonlist) {
        this.items = mSecitonlist;

        setHasStableIds(true);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        display = mContext.getResources().getDisplayMetrics();
        width = display.widthPixels;
        mMetadataRetriever = new MediaMetadataRetriever();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feed, viewGroup, false);

        return new ViewHolder(itemView, this);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).hashCode();
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final FeedBodyModel item = items.get(position).GetFeedBodyModel();
        ArrayList<CommentModel> arrComment = item.GetArrCommentModel();
        viewHolder.mesage.setText(item.GetDescription());

        viewHolder.likecount.setText(item.GetLikeCount() + " likes");
        viewHolder.commentcount.setText(item.GetCommentCount() + " comments");

        if(!item.GetTag().equals("")){
            ArrayList<String> arrHashTag = getHashTag(item.GetTag());
            configureHashTagLayout(viewHolder.llHashTag, viewHolder.llHashTag2, arrHashTag);
        }
        if(arrComment.size() > 0){
            configureCommentLayout(viewHolder.llComment, arrComment);
        }

        if(item.GetIslike().equals("1")){
//            viewModel.imageViewLikes.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.liked_2x));
            viewHolder.imageViewLikes.setImageResource(R.drawable.liked_3x);
        }else if(item.GetIslike().equals("0")){
//            viewModel.imageViewLikes.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.like_2x));
            viewHolder.imageViewLikes.setImageResource(R.drawable.like_3x);
        }
        setImageViewSize(viewHolder.imageView);
        viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.feed));
        viewHolder.imageView.setVisibility(View.VISIBLE);

        final String fileName = item.GetAttachURL().substring(item.GetAttachURL().lastIndexOf('/') + 1);
        File file = new File(PATH, fileName);
        if(file.exists()){
            if(item.GetBitmap() != null){
                viewHolder.imageView.setImageBitmap(item.GetBitmap());
            }else{
                if(item.GetAttatchType().equals("1")){

                    viewHolder.ivIsVideo.setVisibility(View.GONE);
                    Bitmap bitmap = BitmapFactory.decodeFile(PATH + fileName);
                    viewHolder.imageView.setImageBitmap(bitmap);

                }else if(item.GetAttatchType().equals("2")){
                    viewHolder.ivIsVideo.setVisibility(View.VISIBLE);
                }
            }

        }

        viewHolder.ivIsVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoViewActivity.class);
                intent.putExtra("url",  PATH + fileName);
                mContext.startActivity(intent);
            }
        });

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.GetAttatchType().equals("1")){
                    Intent intent = new Intent(mContext, ImageViewActivity.class);
                    intent.putExtra("url",  PATH + fileName);
                    mContext.startActivity(intent);
                }else {
                    Intent intent = new Intent(mContext, VideoViewActivity.class);
                    intent.putExtra("url", PATH + fileName);
                    mContext.startActivity(intent);
                }

            }
        });
        viewHolder.likecount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LikeUserActivity.class);
                String id = item.GetArticleId();
                intent.putExtra("article_id", id);

                mContext.startActivity(intent);

            }
        });
        viewHolder.commentcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                String id = item.GetArticleId();
                intent.putExtra("article_id", id);
                FeedBannerModel fbm = items.get(position).GetFeedBannerModel();
                intent.putExtra("photourl", fbm.GetPhoto());
                mContext.startActivity(intent);

            }
        });
        viewHolder.ivToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                String id = item.GetPosterId();
                intent.putExtra("id", id);

                mContext.startActivity(intent);
            }
        });
//
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    private void setImageViewSize(ImageView imageview){
        ///get screen size
//        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics display = mContext.getResources().getDisplayMetrics();
        int width = display.widthPixels;
        imageview.setMinimumWidth(width);
        imageview.setMinimumHeight(width);
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onRemove(int position) {
        notifyItemRemoved(position);
    }

    //    public void playVideo(VideoView vv, String url){
//        vv.setVideoPath(url);
//        vv.setMediaController(new MediaController(mContext));
//        vv.requestFocus();
////        vv.start();
//
//        Intent it = new Intent(Intent.ACTION_VIEW);
//        Uri uri = Uri.parse(url);
//        it.setDataAndType(uri, "video/mov");
//        mContext.startActivity(it);
//    }
    private ArrayList<String> getHashTag(String strTag) {
        ArrayList<String> arrString = new ArrayList<String >();
        String strKey = ",";
        int first = 0;
        int cutNum = 0;
        do{
            int second = strTag.indexOf(strKey);
            int length = strTag.length();
            if(second == -1){

                second = length;
                cutNum = second;
            }else{
                cutNum = second + 1;
            }
            String str3 = strTag.substring(first, second);
            arrString.add("  #" + str3);
//            first = 1;
            strTag = strTag.substring(cutNum, length);
        }while (strTag.contains(strKey));
        if(!strTag.isEmpty()){
            arrString.add("  #" + strTag);
        }

        return arrString;
    }
    private void configureHashTagLayout(LinearLayout llHashTag, LinearLayout llHashTag2, final ArrayList<String> arrHashTag){
        llHashTag.removeAllViews();
        llHashTag2.removeAllViews();
        int count = arrHashTag.size();
        int totalWidthOfLayout = 0;
        for(int i = 0; i < count; i ++){
            View hashTagView = mInflater.inflate(R.layout.item_hash_tag, null);
            TextView tvHashTag = (TextView)hashTagView.findViewById(R.id.tv_hash_tag_item);
            final int finalI = i;
            tvHashTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String strhash = arrHashTag.get(finalI).replace("  #", "");
                    Intent intent = new Intent(mContext.getApplicationContext(), HashTagActivity.class);
                    intent.putExtra("hashcode", strhash);
                    mContext.startActivity(intent);
                }
            });
            tvHashTag.setText(arrHashTag.get(i));
            //get text length ( pixel)
            Rect bounds = new Rect();
            Paint textPaint = tvHashTag.getPaint();
            textPaint.getTextBounds(arrHashTag.get(i),0,arrHashTag.get(i).length(),bounds);
            int heightOfTextView = bounds.height();
            int widthOfTextView = bounds.width();
            //compare total text length with screen width
//            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llHashTag.getLayoutParams();
            if(totalWidthOfLayout +  widthOfTextView < width){
                llHashTag.addView(hashTagView, i);
                totalWidthOfLayout += widthOfTextView;
            }else if(totalWidthOfLayout + widthOfTextView < 2 * width){
                llHashTag2.addView(hashTagView, i - llHashTag.getChildCount());
//                totalWidthOfLayout = 0;
            }

        }
    }
    private void configureCommentLayout(LinearLayout llComment, ArrayList<CommentModel> arrComment){
        int count = arrComment.size();
        for(int i = 0; i < count; i ++){
            View commentView = mInflater.inflate(R.layout.item_feed_comment, null);
            TextView tvName = (TextView)commentView.findViewById(R.id.tv_feed_comment_email);
            TextView tvComment = (TextView)commentView.findViewById(R.id.tv_feed_comment_comment);

            CommentModel cm = arrComment.get(i);

            tvName.setText("@" + cm.GetName());
            tvComment.setText(cm.GetComment());

            llComment.addView(commentView, i);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        //        public ImageView user_img;
        public ImageView imageViewLikes;
        public ImageView imageView;
        public ImageView ivToChat;
        public ImageView ivFeedShare;
        public ImageView ivIsVideo;

//        public VideoView videoView;

        public TextView username;
        public TextView mesage;
        public TextView time;
        public TextView commentcount;
        public TextView likecount;
//        public TextView comment;

        public LinearLayout llHashTag;
        public LinearLayout llHashTag2;
        public LinearLayout llComment;

        private OnRemoveListener listener;
        final Context mContext;
        public ViewHolder(final View itemView, OnRemoveListener listener) {
            super(itemView);
            //////////////////////////////////////////////////////
            mContext = itemView.getContext();

            this.listener = listener;
            imageView = (ImageView)itemView.findViewById(R.id.iv_feed_image);
            imageViewLikes = (ImageView)itemView.findViewById(R.id.iv_feed_likes_1);
//            videoView = (VideoView)itemView.findViewById(R.id.vv_feed);
            ivToChat = (ImageView)itemView.findViewById(R.id.iv_feed_comment_1);
            ivFeedShare = (ImageView)itemView.findViewById(R.id.iv_feed_share);
            ivIsVideo = (ImageView)itemView.findViewById(R.id.iv_isvideo4);

            mesage = (TextView)itemView.findViewById(R.id.tv_feed_message);
            likecount = (TextView)itemView.findViewById(R.id.tv_feed_likes);
            commentcount = (TextView)itemView.findViewById(R.id.tv_feed_comment);
//            comment = (TextView)itemView.findViewById(R.id.tv_feed_comment_body);

            llComment = (LinearLayout)itemView.findViewById(R.id.ll_feed_comment);
            llHashTag = (LinearLayout)itemView.findViewById(R.id.ll_hash_tag);
            llHashTag2 = (LinearLayout)itemView.findViewById(R.id.ll_hash_tag2);
            ivToChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    mContext.startActivity(intent);

                }
            });
            imageViewLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            ivFeedShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "FAF");
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Example text");

                    mContext.startActivity(Intent.createChooser(shareIntent, "fixafriend"));
                }
            });
//            videoView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    videoView.start();
//                }
//            });
            setFont();
            /////for test
//            imageViewLikes.setBackgroundDrawable(itemView.getResources().getDrawable(R.drawable.liked_2x));
//            imageView.setBackgroundDrawable(itemView.getResources().getDrawable(R.drawable.sample));
            ////////////////////////////////
//

//        viewModel.user_img.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.user1));

        }
        private void setFont(){

            Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(),  "Calibri.ttf");
            Typeface custom_font_bold = Typeface.createFromAsset(mContext.getAssets(),  "Calibri-Bold.ttf");
            mesage.setTypeface(custom_font_bold);
            likecount.setTypeface(custom_font);
            commentcount.setTypeface(custom_font);
//            comment.setTypeface(custom_font);

        }

        @Override
        public void onClick(View v) {

            //listener.onRemove(getPosition());
        }
    }

//    private void downloadVideo(){
//
//        String result_;
//        int begin, end;
//        String tmpstr = null;
//        try {
//            URL url=new URL("http://www.youtube.com/watch?v=y12-1miZHLs&nomobile=1");
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setRequestMethod("GET");
//            InputStream stream=con.getInputStream();
//            InputStreamReader reader=new InputStreamReader(stream);
//            StringBuffer buffer=new StringBuffer();
//            char[] buf=new char[262144];
//            int chars_read;
//            while ((chars_read = reader.read(buf, 0, 262144)) != -1) {
//                buffer.append(buf, 0, chars_read);
//            }
//            tmpstr=buffer.toString();
//
//            begin  = tmpstr.indexOf("url_encoded_fmt_stream_map=");
//            end = tmpstr.indexOf("&", begin + 27);
//            if (end == -1) {
//                end = tmpstr.indexOf("\"", begin + 27);
//            }
//            tmpstr = UtilClass.URLDecode(tmpstr.substring(begin + 27, end));
//
//        } catch (MalformedURLException e) {
//            throw new RuntimeException();
//        } catch (IOException e) {
//            throw new RuntimeException();
//        }
//
//        Vector url_encoded_fmt_stream_map = new Vector();
//        begin = 0;
//        end   = tmpstr.indexOf(",");
//
//        while (end != -1) {
//            url_encoded_fmt_stream_map.addElement(tmpstr.substring(begin, end));
//            begin = end + 1;
//            end   = tmpstr.indexOf(",", begin);
//        }
//
//        url_encoded_fmt_stream_map.addElement(tmpstr.substring(begin, tmpstr.length()));
//        String result = "";
//        Enumeration url_encoded_fmt_stream_map_enum = url_encoded_fmt_stream_map.elements();
//        while (url_encoded_fmt_stream_map_enum.hasMoreElements()) {
//            tmpstr = (String)url_encoded_fmt_stream_map_enum.nextElement();
//            begin = tmpstr.indexOf("itag=");
//            if (begin != -1) {
//                end = tmpstr.indexOf("&", begin + 5);
//
//                if (end == -1) {
//                    end = tmpstr.length();
//                }
//
//                int fmt = Integer.parseInt(tmpstr.substring(begin + 5, end));
//
//                if (fmt == 35) {
//                    begin = tmpstr.indexOf("url=");
//                    if (begin != -1) {
//                        end = tmpstr.indexOf("&", begin + 4);
//                        if (end == -1) {
//                            end = tmpstr.length();
//                        }
//                        result = UtilClass.URLDecode(tmpstr.substring(begin + 4, end));
//                        result_=result;
//                        break;
//                    }
//                }
//            }
//        }
//        try {
//            URL u = new URL(result);
//            HttpURLConnection c = (HttpURLConnection) u.openConnection();
//            c.setRequestMethod("GET");
///*              c.setRequestProperty("Youtubedl-no-compression", "True");
//              c.setRequestProperty("User-Agent", "YouTube");*/
//
//            c.setDoOutput(true);
//            c.connect();
//
//            FileOutputStream f=new FileOutputStream(new File("/sdcard/3.flv"));
//
//            InputStream in=c.getInputStream();
//            byte[] buffer=new byte[1024];
//            int sz = 0;
//            while ( (sz = in.read(buffer)) > 0 ) {
//                f.write(buffer,0, sz);
//            }
//            f.close();
//        } catch (MalformedURLException e) {
//            new RuntimeException();
//        } catch (IOException e) {
//            new RuntimeException();
//        }
//    }

}
