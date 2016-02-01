package com.han.sticky_adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.han.activities.ImageViewActivity;
import com.han.activities.InboxActivity;
import com.han.activities.VideoViewActivity;
import com.han.login.R;
import com.han.listener.OnRemoveListener;
import com.han.model.FeedViewModel;
import com.han.model.InboxModel;
import com.han.utils.Constant;
import com.han.utils.GlobalVariable;
import com.han.utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aurel on 22/09/14.
 */
public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder2> implements OnRemoveListener {

    private  String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator ;

    public List<InboxModel> items;
    FeedViewModel viewModel;
    DisplayMetrics display ;
    int width;
    Context mContext;
    int num ;
    GlobalVariable globalVariable ;
    SharedPreferences preferences;

    public InboxAdapter(Context context, ArrayList<InboxModel> mSecitonlist) {
        this.items = mSecitonlist;
        mContext = context;
        display = mContext.getResources().getDisplayMetrics();
        width = display.widthPixels;
        num = -1;

        globalVariable = new GlobalVariable();
        preferences = Utilities.getSharedPreferences(context);

        setHasStableIds(true);

    }


    @Override
    public ViewHolder2 onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_inbox, viewGroup, false);
        return new ViewHolder2(itemView, this);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).hashCode();
    }

    @Override
    public void onBindViewHolder(ViewHolder2 viewHolder, int position) {


        final InboxModel item = items.get(position);
        setRelativeLayoutSize(viewHolder.rlProgress);
        setImageViewSize(viewHolder.imageView);
        viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.feed));

        final String fileName = item.GetArticleAttach().substring(item.GetArticleAttach().lastIndexOf('/') + 1);
        File file = new File(PATH, fileName);

        viewHolder.rlEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalVariable.decreaseValue(preferences, Constant.N_INVITATION);
                Intent intent = null;
                try {
                    intent = new Intent(mContext, Class.forName("com.example.rmk.mediaediting.EditorActivity"));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                intent.putExtra("url", item.GetArticleAttach());
                intent.putExtra("invitationId", item.GetInvitationId());
                mContext.startActivity(intent);
            }
        });

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext.getApplicationContext(), ImageViewActivity.class);
                intent.putExtra("url",  PATH + fileName);
                mContext.startActivity(intent);
            }
        });

        viewHolder.rlPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext.getApplicationContext(), VideoViewActivity.class);
                intent.putExtra("url",  PATH + fileName);
                mContext.startActivity(intent);
            }
        });

        viewHolder.rlEditVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalVariable.decreaseValue(preferences, Constant.N_INVITATION);

                Intent intent = null;
                try {
                    intent = new Intent(mContext, Class.forName("com.example.rmk.mediaediting.VideoEditorActivity"));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                intent.putExtra("path", PATH + fileName);
                intent.putExtra("invitationId", item.GetInvitationId());
                mContext.startActivity(intent);
            }
        });
        ////////////set Image ======================================================================

        if(!file.exists()){
            if (mContext instanceof InboxActivity) {

                viewHolder.imageView.setVisibility(View.GONE);
                viewHolder.ivEditImage.setVisibility(View.GONE);
                viewHolder.ivEditVideo.setVisibility(View.GONE);
                viewHolder.ivPlayVideo.setVisibility(View.GONE);
                viewHolder.rlProgress.setVisibility(View.VISIBLE);


//                downloadQueueManager.insert(position, viewHolder.llCircleProgress);
//                downloadTask(position);
                if(num < position){
                    num = position;
                    ((InboxActivity) mContext).download(num, viewHolder.rlProgress);

                }
            }
        }else {
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.rlProgress.setVisibility(View.GONE);
            if(item.GetArticleType().equals("1")){
                viewHolder.ivEditImage.setVisibility(View.VISIBLE);
                viewHolder.ivEditVideo.setVisibility(View.GONE);
                viewHolder.ivPlayVideo.setVisibility(View.GONE);

                if(item.GetBitmap() == null){

                    Bitmap bitmap = BitmapFactory.decodeFile(PATH + fileName);
                    viewHolder.imageView.setImageBitmap(bitmap);

                    items.get(position).SetBitmap(bitmap);
                }else {
                    viewHolder.imageView.setImageBitmap(item.GetBitmap());
                }
            }else {
                viewHolder.ivEditVideo.setVisibility(View.VISIBLE);
                viewHolder.ivPlayVideo.setVisibility(View.VISIBLE);
                viewHolder.ivEditImage.setVisibility(View.GONE);
                viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.feed));
            }
        }
        /////////////////////////////////////////


        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }
    private void setImageViewSize(ImageView imageview){
        ///get screen size
        imageview.setMinimumWidth(width);
        imageview.setMinimumHeight(width);

    }

    private void setRelativeLayoutSize(RelativeLayout relativeLayout){
        ViewGroup.LayoutParams layoutParams = null;
        try{
            layoutParams =  relativeLayout.getLayoutParams();
        }catch (Exception e){
            e.printStackTrace();
        }

        layoutParams.width = width;
        layoutParams.height = width;
        relativeLayout.setLayoutParams(layoutParams);


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onRemove(int position) {
        notifyItemRemoved(position);
    }


    public class ViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public ImageView ivPlayVideo;
        public ImageView ivEditImage;
        public ImageView ivEditVideo;

        public TextView username;
        public TextView mesage;
        public TextView comment;

//        public RelativeLayout relativeLayout;
        public RelativeLayout rlPlayVideo;
        public RelativeLayout rlEditVideo;
        public RelativeLayout rlEditPhoto;
        public RelativeLayout rlProgress;

        private OnRemoveListener listener;

        public Context mContext;

        public ViewHolder2(final View itemView, OnRemoveListener listener) {
            super(itemView);
            //////////////////////////////////////////////////////
           mContext = itemView.getContext();

            this.listener = listener;
            imageView = (ImageView)itemView.findViewById(R.id.iv_inbox_image);
            ivPlayVideo = (ImageView)itemView.findViewById(R.id.iv_inbox_play_video);
            ivEditImage = (ImageView)itemView.findViewById(R.id.iv_inbox_edit_image);
            ivEditVideo = (ImageView)itemView.findViewById(R.id.iv_inbox_edit_video);
//            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.rl_inbox);
            rlEditPhoto = (RelativeLayout)itemView.findViewById(R.id.rl_editphoto);
            rlEditVideo = (RelativeLayout)itemView.findViewById(R.id.rl_editvideo);
            rlPlayVideo = (RelativeLayout)itemView.findViewById(R.id.rl_playvideo);
            rlProgress = (RelativeLayout)itemView.findViewById(R.id.rl_progress);
          }

        private void setFont(){

            Typeface custom_font = Typeface.createFromAsset(mContext.getAssets(), "Calibri.ttf");

            mesage.setTypeface(custom_font);
            comment.setTypeface(custom_font);
        }
        @Override
        public void onClick(View v) {

            //listener.onRemove(getPosition());
        }
    }


}
