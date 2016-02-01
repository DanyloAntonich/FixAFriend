package com.example.rmk.mediaediting.videoEditing;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.rmk.mediaediting.GallerySet.FrameGalleryTool;
import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphic_process.DataStorage;
import com.example.rmk.mediaediting.graphics.commands.InsertImageCommand;

public class VideoFrameActivity extends Activity {

    private TextView okButton;
    private TextView cancelButton;

    private LinearLayout drawWindow;

    Paint mPaint;

    private Gallery gallery;

    private Bitmap bgImage = null;
    private DataStorage dataStorage;

    private int effectSelectFlag = 0;

    private Bitmap frameImage;
    private Boolean isframeSelected = true;
    private int editImageStatus_old = 100;

    private VideoView videoView;
    private String firstVideoPath;
    private String lastVideoPath;
    private int videoWidth;
    private int videoHeight;
    private VideoPathSetting videoPathSetting;
    private ImageView coverImage;
    private int drawW, drawH;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataStorage = DataStorage.getInstance();
        videoPathSetting = VideoPathSetting.getInstance();
        dataStorage.setVideoEditStart(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_frame);
        initializeComponents();
    }

    private void initFont(){

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Calibri-Bold.ttf");
        TextView topbar_title = (TextView)findViewById(R.id.topbar_title);
        topbar_title.setTypeface(font);

        Typeface font2 = Typeface.createFromAsset(getAssets(), "fonts/Calibri.ttf");
        TextView cancel_title = (TextView)findViewById(R.id.cancel_button);
        TextView ok_title = (TextView)findViewById(R.id.ok_button);
        cancel_title.setTypeface(font2);
        ok_title.setTypeface(font2);

    }
    private void initializeComponents() {
        dataStorage = DataStorage.getInstance();

        initFont();
        videoView = (VideoView)findViewById(R.id.image_view);
        coverImage = (ImageView)findViewById(R.id.coverImage);

        okButton = (TextView) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);

        getImageFromStorage();
        getVideoPath();
        DisplayVideoInVideoView();
        coverImagUpdate();
        ///

        frameImage = BitmapFactory.decodeResource(getResources(), R.drawable.r003);

        gallery = (Gallery) findViewById(R.id.frame_gallery);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemClickListener(listener);
        gallery.setSpacing(20);
        gallery.setSelection(2);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method4 stub
        super.onWindowFocusChanged(hasFocus);
        updateSizeInfo();
    }
    private void updateSizeInfo() {
        /// Relativelayout region width, height
        RelativeLayout drawRegion = (RelativeLayout)findViewById(R.id.drawWindow);
        drawW = drawRegion.getWidth();
        drawH = drawRegion.getHeight();
        getImageFromStorage();
        LinearLayout parentWindow = (LinearLayout)findViewById(R.id.parentWindow);
        parentWindow.setPadding((drawW - dataStorage.getVideoWidth()) / 2, (drawH - dataStorage.getVideoHeight()) / 2, (drawW - dataStorage.getVideoWidth()) / 2, (drawH - dataStorage.getVideoHeight()) / 2);
        parentWindow.invalidate();
    }


    private void getImageFromStorage(){
        bgImage = dataStorage.getCoverImageBitmap();
        bp = bgImage;
    }
    private void imageProcess() {
        if (editImageStatus_old != effectSelectFlag) {
            bp = dataStorage.getBitmap();
            editImageStatus_old = effectSelectFlag;
        }
        frameImage = BitmapFactory.decodeResource(getResources(), FrameGalleryTool.ImageIds_FrameGallery[effectSelectFlag]);
        Bitmap scaleFrameImage = Bitmap.createScaledBitmap(frameImage, bgImage.getWidth(), bgImage.getHeight(), false);
        InsertImageCommand command = new InsertImageCommand(scaleFrameImage);
        bp = command.process(bgImage);
        coverImagUpdate();
    }

    Bitmap bp = null;
    //// Gallery Adapter
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            effectSelectFlag = position;
            imageProcess();
            isframeSelected = true;
        }
    };

    public class ImageAdapter extends BaseAdapter {
        int galleryItemBackground;
        private Context context;
        private Integer[] mImageIds = FrameGalleryTool.ImageIds_FrameGallery;

        public ImageAdapter(Context c) {
            context = c;
            TypedArray attr = context
                    .obtainStyledAttributes(R.styleable.FiltersGallery);
            galleryItemBackground = attr
                    .getResourceId(
                            R.styleable.FiltersGallery_android_galleryItemBackground,
                            0);
            attr.recycle();
        }

        public int getCount() {
            return mImageIds.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(mImageIds[position]);
            imageView.setLayoutParams(new Gallery.LayoutParams(80, 80));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setBackgroundColor(Color.BLACK);
            return imageView;
        }
    }

    private void getVideoPath(){
        firstVideoPath = dataStorage.getFirstVideoFilePath();
        lastVideoPath = dataStorage.getLastVideoFilePath();
        videoWidth = dataStorage.getVideoWidth();
        videoHeight = dataStorage.getVideoHeight();
    }
    private void DisplayVideoInVideoView(){
        videoView.stopPlayback();

        videoView.setVideoPath(videoPathSetting.getOriginalVideoPath());
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                videoView.start();
            }
        });
    }

    private void coverImagUpdate(){
        coverImage.setImageBitmap(bp);
        coverImage.invalidate();
    }





    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

//            imageView.setDrawingCacheEnabled(true);
//            imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            imageView.buildDrawingCache(true);
//            Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());

            dataStorage.setCoverImageBitmap(bp);
            setResult(RESULT_OK);
            finish();

        }
    };
    private View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            setResult(RESULT_CANCELED);
            dataStorage.setisModified(0);
            finish();
        }
    };





























}
