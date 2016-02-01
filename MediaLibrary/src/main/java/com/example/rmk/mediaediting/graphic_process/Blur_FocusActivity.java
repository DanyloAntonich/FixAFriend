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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rmk.mediaediting.GallerySet.BlurFocusGallleryTool;
import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphics.commands.CropCircularImage;
import com.example.rmk.mediaediting.graphics.commands.CropCommand;
import com.example.rmk.mediaediting.graphics.commands.InsertImageCommand;

import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;


public class Blur_FocusActivity extends Activity {
    private GPUImageView gpuImageView;
    private TextView okButton;
    private TextView cancelButton;
    private Gallery gallery;
    private SeekBar slider_adjustment_blur;

    private Bitmap bgImage = null;
    private Bitmap bgImage_progress = null;
    private DataStorage dataStorage;
    private int editImageStatus_old = 100;


    private int effectSelectFlag = 0;
    // mask iamge
    private Bitmap mMask1, mMask2;
    Paint mPaint;
    private Path    mPath;
    private int drawW, drawH;
    DrawImage view1;
    private Bitmap progressImage;

    Bitmap bp = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataStorage = DataStorage.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur__focus);
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
        gpuImageView = (GPUImageView)findViewById(R.id.image_view);

        okButton = (TextView) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);
        slider_adjustment_blur = (SeekBar) findViewById(R.id.slider_blur_adjustment);
        slider_adjustment_blur.setOnSeekBarChangeListener(sliderChageListener);


        getImageFromStorage();
        handleImage();
//        imageView.setImageBitmap(bgImage);
//        bp = bgImage;
//        progressImage = bgImage;
        gallery = (Gallery) findViewById(R.id.filter_gallery);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemClickListener(listener);
        gallery.setSelection(2);
        gallery.setSpacing(20);

        view1 =new DrawImage(this);
        RelativeLayout drawRegion = (RelativeLayout)findViewById(R.id.imagePanel);
        drawRegion.addView(view1);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(10);

        mMask1 = BitmapFactory.decodeResource(getResources(), R.drawable.band);
        //test
//        mMask2 = BitmapFactory.decodeResource(getResources(), R.drawable.center_blackcircle);

    }

    private void handleImage() {
        gpuImageView.setImage(bgImage);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        updateSizeInfo();
    }
    private void updateSizeInfo() {
        /// Relativelayout region width, height
        RelativeLayout drawRegion = (RelativeLayout)findViewById(R.id.imagePanel);
        drawW = drawRegion.getWidth();
        drawH = drawRegion.getHeight();
        getImageFromStorage();
        LinearLayout parentWindow = (LinearLayout)findViewById(R.id.parentWindow);
        parentWindow.setPadding((drawW - bgImage.getWidth()) / 2, (drawH - bgImage.getHeight()) / 2, (drawW - bgImage.getWidth()) / 2, (drawH - bgImage.getHeight()) / 2);
        parentWindow.invalidate();
    }


    private void getImageFromStorage(){
        bgImage = dataStorage.getBitmap();
    }

    private void imageProcess() {
        int blurStrength = slider_adjustment_blur.getProgress();
        bp = dataStorage.getBitmap();

        if (editImageStatus_old != effectSelectFlag){
            bp = dataStorage.getBitmap();
            editImageStatus_old = effectSelectFlag;
        }

        gpuImageView.setImage(bp);
        gpuImageView.setFilter(new GPUImageGaussianBlurFilter(blurStrength / 50));
        gpuImageView.requestRender();

        switch (effectSelectFlag) {
            case 0:
                break;
            case 1://band

                break;
            case 2://circle

                break;
        }

        view1.invalidate();
    }


    private float mX, mY;
    public class DrawImage extends View{

        Context context;
        private Path mCirclePath;
        private Path mRectanglePath;
        private Paint mCirclePaint;


        private Paint mBitmapPaint;
        public float mViewWidth;
        public float mViewHeight;

        public DrawImage(Context c) {
            super(c);
            context = c;
            mCirclePath = new Path();
            mRectanglePath = new Path();
            mCirclePaint = new Paint();

            mCirclePaint.setAntiAlias(true);
            mCirclePaint.setColor(Color.WHITE);
            mCirclePaint.setStyle(Paint.Style.STROKE);
            mCirclePaint.setStrokeWidth(2f);
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

            if (effectSelectFlag == 1) {
                if ((mY + mViewHeight * 0.125f) < mViewHeight){
                    CropCommand command = new CropCommand(0, (int)(mY - mViewHeight * 0.125F),(int)mViewWidth, (int)(mViewHeight * 0.25F));
                    mMask1 = command.process(bgImage);
                    canvas.drawBitmap(mMask1,  0, (int)(mY - mViewHeight * 0.125F), mCirclePaint);
                }
            }

            if (effectSelectFlag == 2)
            {
                CropCommand command1 = new CropCommand((int)(mX - mViewWidth/4), (int)(mY - mViewWidth/4), (int)(mViewWidth/2), (int)(mViewHeight/2));
                mMask2 = command1.process(bgImage);
                CropCircularImage command2 = new CropCircularImage();
                mMask2 = command2.getCroppedBitmap(mMask2);
                canvas.drawBitmap(mMask2, mX - mViewWidth/4, mY - mViewWidth/4, mCirclePaint);
            }
            if (effectSelectFlag == 2) canvas.drawPath(mCirclePath, mCirclePaint);
            if (effectSelectFlag == 1) canvas.drawPath(mRectanglePath, mCirclePaint);
        }

        private static final float TOUCH_TOLERANCE = 4;
        private void touch_start(float x, float y){
            mCirclePath.reset();
            mRectanglePath.reset();
            mCirclePath.moveTo(x, y);
            mRectanglePath.moveTo(0, y);
            mCirclePath.reset();
            mCirclePath.addCircle(mX, mY, mViewWidth / 4, Path.Direction.CW);
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
                mRectanglePath.reset();
                mCirclePath.addCircle(mX, mY, mViewWidth / 4, Path.Direction.CW);
                mRectanglePath.addRect(-1000, mY - (int)(mViewHeight * 0.125F), 1000, (int)(mY + mViewHeight * 0.125F), Path.Direction.CW );
            }
        }
        private void touch_up(float x, float y){
            mCirclePath.reset(); mRectanglePath.reset();
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

















    private SeekBar.OnSeekBarChangeListener sliderChageListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            imageProcess();
        }
        private boolean sliderMovedByUser(SeekBar seekBar, boolean fromUser){
            return fromUser && seekBar.getId() == slider_adjustment_blur.getId();
        }
    };
    public class ImageAdapter extends BaseAdapter {
        int galleryItemBackground;
        private Context context;
        private Integer[] mImageIds = BlurFocusGallleryTool.ImageIds_BlurFocus;

        public ImageAdapter(Context c) {
            context = c;
            TypedArray attr = context.obtainStyledAttributes(R.styleable.FiltersGallery);
            galleryItemBackground = attr.getResourceId(R.styleable.FiltersGallery_android_galleryItemBackground, 0);
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
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {
            Toast.makeText(getBaseContext(), "filter: " + position, Toast.LENGTH_SHORT).show();

            effectSelectFlag = position;

            imageProcess();
        }
    };

    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

            Bitmap bitmap = null;
            try {
                bitmap = gpuImageView.capture();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (effectSelectFlag == 1){
                InsertImageCommand command = new InsertImageCommand(mMask1);
                bitmap = command.process(bitmap, (int)(mX- mMask1.getWidth()/2), (int)(mY - mMask1.getHeight()/2));
            }
            if (effectSelectFlag == 2){
                InsertImageCommand command = new InsertImageCommand(mMask2);
                bitmap = command.process(bitmap, (int)(mX-mMask2.getWidth()/2), (int)(mY - mMask2.getHeight()/2));
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









    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_blur__focus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


// how to use filter JH Labs
//mOrigBitmap = (Bitmap) data.getExtras().get("data");
//
//        // Filter image
//        //InvertFilter invertFilter = new InvertFilter();
//        BlurFilter filter = new SolarizeFilter();
//        int[] src = AndroidUtils.bitmapToIntArray(mOrigBitmap);
//        int width = mOrigBitmap.getWidth();
//        int height = mOrigBitmap.getHeight();
//        //int[] dest = invertFilter.filter(src, width, height);
//        int[] dest = solarFilter.filter(src, width, height);
//
//        Bitmap destBitmap = Bitmap.createBitmap(dest, width, height, Bitmap.Config.ARGB_8888);
//        mPicView.setImageBitmap(destBitmap);