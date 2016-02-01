package com.example.rmk.mediaediting.graphic_process;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.rmk.mediaediting.GPUImageFilterTools;
import com.example.rmk.mediaediting.GallerySet.EffectGalleryTool;
import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphics.ImageProcessor;
import com.example.rmk.mediaediting.graphics.commands.InsertImageCommand;

import jp.co.cyberagent.android.gpuimage.GPUImageBoxBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePixelationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

public class EffectActivity extends Activity {

    private RelativeLayout drawWindow;
    private ImageView imageView;
    private TextView okButton;
    private TextView cancelButton;
    private ProgressBar progressBar;
    private ImageProcessor imageProcessor;

    private Gallery gallery;
    private SeekBar seekBar_effect_intensity;

    private Bitmap bgImage = null;
    private Bitmap bgImage_progress = null;
    private DataStorage dataStorage;
    private int editImageStatus_old = 10;
    private int editImageStatus = 0;

    private GPUImageFilter mFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
    private GPUImageView mGPUImageView;


    private int effectSelectFlag = 0;

    DrawImage view1;
    private Bitmap circleMask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataStorage = DataStorage.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effect);

        view1 = new DrawImage(this);
        drawWindow = (RelativeLayout)findViewById(R.id.imagePanel);

        circleMask = BitmapFactory.decodeResource(getResources(), R.drawable.mask);
        circleMask = Bitmap.createScaledBitmap(circleMask, circleMask.getWidth() * 3, circleMask.getHeight() * 3, false);

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
        imageView = (ImageView) findViewById(R.id.image_view);
        gallery = (Gallery) findViewById(R.id.effect_gallery);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemClickListener(listener);
        gallery.setSelection(3);
        gallery.setSpacing(20);

        seekBar_effect_intensity = (SeekBar) findViewById(R.id.seeker_effect_intensity);
        seekBar_effect_intensity.setOnSeekBarChangeListener(sliderChangeListener_intensity);
        seekBar_effect_intensity.setVisibility(View.GONE);

        okButton = (TextView) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);

        mGPUImageView = (GPUImageView) findViewById(R.id.gpuimage_effect);
        getImageFromStorage();
        handleImage();
//        imageView.setImageBitmap(bgImage);
    }

    private void getImageFromStorage(){
        bgImage = dataStorage.getBitmap();
    }
    private void handleImage(){
        mGPUImageView.setImage(bgImage);
    }

    Bitmap bp = null;
    private void imageProcess(){
//        seekBar_effect_intensity.setVisibility(View.VISIBLE);

        int value = seekBar_effect_intensity.getProgress();

        if (editImageStatus_old != effectSelectFlag){
            bp = dataStorage.getBitmap();
            if (editImageStatus_old == 1) drawWindow.removeView(view1);
            editImageStatus_old = effectSelectFlag;
        }

        switch (effectSelectFlag){
            case 0:
                mGPUImageView.setImage(bp);
                mGPUImageView.setFilter(new GPUImageBoxBlurFilter((float)0.0f));
                mGPUImageView.requestRender();
                break;
            case 1:
                mGPUImageView.setImage(bp);
                mGPUImageView.setFilter(new GPUImageBoxBlurFilter((float)0.0f));
                mGPUImageView.requestRender();
                break;
            case 2:
                mGPUImageView.setImage(bp);
                mGPUImageView.setFilter(new GPUImageHueFilter(value));
                mGPUImageView.requestRender();
                break;
            case 3:
                mGPUImageView.setImage(bp);
                mGPUImageView.setFilter(new GPUImageHighlightShadowFilter(value / 50, 0.0f));
                mGPUImageView.requestRender();
                break;
            case 4:
                mGPUImageView.setImage(bp);
                mGPUImageView.setFilter(new GPUImageGaussianBlurFilter(value / 50));
                mGPUImageView.requestRender();
                break;
            case 5:
                mGPUImageView.setImage(bp);
                mGPUImageView.setFilter(new GPUImageMonochromeFilter());
                mGPUImageView.requestRender();
                break;
            case 6:
                mGPUImageView.setImage(bp);
                mGPUImageView.setFilter(new GPUImagePosterizeFilter(value / 50));
                mGPUImageView.requestRender();
                break;
            case 7:
                mGPUImageView.setImage(bp);
                GPUImagePixelationFilter filter = new GPUImagePixelationFilter();
                GPUImageFilterTools.FilterAdjuster filterTool = new GPUImageFilterTools.FilterAdjuster(filter);
                filterTool.adjust(value);
                mGPUImageView.setFilter(filter);
                mGPUImageView.requestRender();
                break;
        }
//        imageView.setImageBitmap(bp);
//        imageView.invalidate();
    }

    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

            Bitmap bitmap = null;
            try {
                bitmap = mGPUImageView.capture();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (effectSelectFlag == 1){
                InsertImageCommand command = new InsertImageCommand(circleMask);
                bitmap = command.process(bitmap, (int)(mX-circleMask.getWidth()/2), (int)(mY - circleMask.getHeight()/2));
            }
            dataStorage.setLastResultBitmap(bitmap);
            dataStorage.setisModified(1);
            dataStorage.save();
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


    private SeekBar.OnSeekBarChangeListener sliderChangeListener_intensity = new SeekBar.OnSeekBarChangeListener() {
        public void onStopTrackingTouch(SeekBar seekBar) {
            // not used here
        }
        public void onStartTrackingTouch(SeekBar seekBar) {
            // not used here
        }
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (sliderMovedByUser(seekBar, fromUser)) {
                imageProcess();
            }
        }
        private boolean sliderMovedByUser(SeekBar seekBar, boolean fromUser) {
            return fromUser && seekBar.getId() == seekBar_effect_intensity.getId();
        }
    };
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {
//            Toast.makeText(getBaseContext(), "Processing: " + EffectGalleryTool.Names.get(position) + seekBar_effect_intensity.getProgress(), Toast.LENGTH_SHORT).show();

            effectSelectFlag = position;
            switch (EffectGalleryTool.Names.get(position)){
                ///reference : https://xjaphx.wordpress.com/learning/tutorials/
                case "None":
//                    seekBar_effect_intensity.setVisibility(View.VISIBLE);
                    break;
                case "Spot":
                    drawWindow.addView(view1);
                    break;
                case "Hue":
                    seekBar_effect_intensity.setVisibility(View.VISIBLE);
                    seekBar_effect_intensity.setProgress(100);
                    break;
                case "Hightlight":
                    seekBar_effect_intensity.setVisibility(View.VISIBLE);
                    break;
                case "Bloom":
                    seekBar_effect_intensity.setVisibility(View.VISIBLE);
                    seekBar_effect_intensity.setProgress(100);
                    break;
                case "Gloom":
                    seekBar_effect_intensity.setVisibility(View.VISIBLE);
                    seekBar_effect_intensity.setProgress(100);
                    break;
                case "Posterize":

                    break;
                case "Pixelate":
                    seekBar_effect_intensity.setVisibility(View.VISIBLE);
                    seekBar_effect_intensity.setProgress(10);
                    break;

            }
            imageProcess();
        }
    };
    public class ImageAdapter extends BaseAdapter {
        int galleryItemBackground;
        private Context context;
        private Integer[] mImageIds = EffectGalleryTool.ImageIds;

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
            imageView.setLayoutParams(new Gallery.LayoutParams(120, 120));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setBackgroundColor(Color.BLACK);
            return imageView;
        }
    }

    private float mX, mY;
    public class DrawImage extends View{

        Context context;
        private Path mCirclePath;
        private Paint mCirclePaint;
        private Paint mBitmapPaint;
        private float mViewWidth;
        private float mViewHeight;

        public DrawImage(Context c) {
            super(c);
            context = c;
            mCirclePath = new Path();
            mCirclePaint = new Paint();
            mCirclePaint.setAntiAlias(true);
            mCirclePaint.setColor(Color.WHITE);
            mCirclePaint.setStyle(Paint.Style.STROKE);
            mCirclePaint.setStrokeWidth(2f);

            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        }
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mViewHeight = h;
            mViewWidth = w;
            mX = mViewWidth / 2;
            mY = mViewHeight / 2;
        }
        @Override
        protected  void onDraw(Canvas canvas){
            super.onDraw(canvas);

            canvas.drawBitmap(circleMask, mX - circleMask.getWidth() / 2, mY - circleMask.getHeight() / 2, mCirclePaint);
            canvas.drawPath(mCirclePath, mCirclePaint);
        }



        private static final float TOUCH_TOLERANCE = 4;
        private void touch_start(float x, float y){
            mCirclePath.reset();
            mCirclePath.moveTo(x, y);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y){
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mX = x;
                mY = y;
                mCirclePath.reset();
                mCirclePath.addCircle(mX, mY, mViewWidth/4, Path.Direction.CW);
            }
        }
        private void touch_up(float x, float y){
            mCirclePath.reset();
        }
        @Override
        public boolean onTouchEvent(MotionEvent event){
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up(x, y);
                    invalidate();
                    break;
            }
            return true;
        }
    }
}
