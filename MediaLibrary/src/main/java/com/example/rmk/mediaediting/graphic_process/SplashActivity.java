package com.example.rmk.mediaediting.graphic_process;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphics.commands.CropPathCommand;
import com.example.rmk.mediaediting.graphics.commands.DrawCircleCommand;
import com.example.rmk.mediaediting.graphics.commands.GetBitmapFromCanvasCommand;
import com.example.rmk.mediaediting.graphics.commands.GrayscaleCommand;
import com.example.rmk.mediaediting.graphics.commands.SaveMaskEffect;

public class SplashActivity extends Activity {

    private ImageView imageView;
    private ImageView imageViewCircle;
    private TextView okButton;
    private TextView cancelButton;
    private SeekBar slider_adjustment_stroke;

    private Bitmap saveImage_test;
    private Bitmap bgImage = null;
    private Bitmap bgProgressImage = null;
    private Bitmap bmCircle = null;
    private DataStorage dataStorage;
    private Bitmap progressImage;
    Bitmap emptyBitmap;

    Paint mMaskPaint;
    private Path    mPath;
    private int drawW, drawH;
    DrawImage view1;
    Bitmap bp = null;
    private Bitmap preSaveImage;
    private GetBitmapFromCanvasCommand saveCommand;
    private Bitmap mMask1 = null;
    private Bitmap empty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataStorage = DataStorage.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
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

        imageViewCircle = (ImageView)findViewById(R.id.image_view_circle);
        bmCircle = BitmapFactory.decodeResource(getResources(), R.drawable.back);
        imageViewCircle.setImageBitmap(bmCircle);

        okButton = (TextView) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);
        emptyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.transparentbackground);

        getImageFromStorage();
        imageView.setImageBitmap(bgImage);
        bp = bgImage;
        progressImage = bgImage;
        ImageView rToolPanel = (ImageView)findViewById(R.id.image_view_circle);
        rToolPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapDrawingMethod();

            }
        });

        slider_adjustment_stroke = (SeekBar) findViewById(R.id.slider_adjustment_RGB);
        slider_adjustment_stroke.setProgress(10);

        slider_adjustment_stroke.setOnSeekBarChangeListener(sliderChangeListener_Stroke);


        //test

        mMask1 = BitmapFactory.decodeResource(getResources(), R.drawable.center_blackband);
//
//        empty = BitmapFactory.decodeResource(getResources(), R.drawable.transparentbackground);

        view1 = new DrawImage(this);
        RelativeLayout drawRegion = (RelativeLayout)findViewById(R.id.imagePanel);
        drawRegion.addView(view1);

        drawSetting();
        initialGrayEffect();
        saveCommand = new GetBitmapFromCanvasCommand();

        mMask1 = Bitmap.createScaledBitmap(mMask1, bgImage.getWidth(), (int) (bgImage.getHeight()), false);
    }

    String drawMode = "draw";
    private void swapDrawingMethod() {
        Bitmap rBitmapEraser = BitmapFactory.decodeResource(getResources(), R.drawable.btn_eraser);
        Bitmap rBitmapEmpty = BitmapFactory.decodeResource(getResources(), R.drawable.transparentbackground);
        imageViewCircle.setImageBitmap((drawMode.equals("draw")) ? rBitmapEraser : bmCircle);
        imageViewCircle.invalidate();
        drawMode = (drawMode.equals("draw")) ? "delete" : "draw";
        mPathOfMask.reset();
        mPath.reset();
        view1.invalidate();
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
    private void initialGrayEffect(){
        GrayscaleCommand command = new GrayscaleCommand();
        bp = command.process(bp);
        progressImage = bp;
        imageView.setImageBitmap(bp);
        imageView.invalidate();
    }
    private void drawSetting(){

        int radius = slider_adjustment_stroke.getProgress();

        int i = 0;

        float a = 255;
        float r = 0;
        float g = 0;
        float b = 0;

        mMaskPaint.setColor(Color.argb((int) a, (int) r, (int) g, (int) b));
        mMaskPaint.setStrokeWidth(radius);

        DrawCircleCommand command = new DrawCircleCommand(radius / 2 , 0.5F, 0.5F, Color.argb((int) a, (int) r, (int) g, (int) b));
        bmCircle = command.process(bmCircle);
        imageViewCircle.setImageBitmap(bmCircle);
        imageViewCircle.invalidate();

    }

    private void getImageFromStorage(){
        bgImage = dataStorage.getBitmap();
        bgProgressImage = dataStorage.getBitmap();
    }


    // for erasing part
    Path mPathOfMask;
    SaveMaskEffect command = new SaveMaskEffect();
    // end erasing part



    Bitmap image_save_fromCache = null;
    private boolean touchUP = false;
    private boolean touchStart = false;
    private float mX, mY;
    private Bitmap bitmapOfPathMask = null;
    public class DrawImage extends View{

        Context context;
        private Bitmap  mBitmap;
        private Canvas  mCanvas;


        private Paint imagePaint;

        public float mViewWidth;
        public float mViewHeight;

        public DrawImage(Context c) {
            super(c);
            setDrawingCacheEnabled(true);
            context = c;
            mPath = new Path();

            mMaskPaint = new Paint();
            mMaskPaint.setAntiAlias(true);
            mMaskPaint.setDither(true);
            mMaskPaint.setColor(0xFFFF0000);
            mMaskPaint.setStyle(Paint.Style.STROKE);
            mMaskPaint.setStrokeJoin(Paint.Join.ROUND);
            mMaskPaint.setStrokeCap(Paint.Cap.ROUND);
            mMaskPaint.setStrokeWidth(10);

            mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

            imagePaint = new Paint();
            imagePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
            /// for erasing part
            mPathOfMask = new Path();
        }
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            preSaveImage = mBitmap;
            command_save = new CropPathCommand();

        }
        Bitmap bitmap;
        CropPathCommand command_save;

        int count_flag = 0;
        @Override
        protected  void onDraw(Canvas canvas){
            super.onDraw(canvas);
            if (drawMode.equals("draw")) {
                imageView.setImageBitmap(progressImage);
                imageView.invalidate();
                canvas.drawPath(mPath, mMaskPaint);
                canvas.drawBitmap(bgImage, 0, 0, imagePaint);

                if (touchStart) {
                    if (touchUP) {
                        progressImage = command.process(mPath, progressImage, bgImage, mMaskPaint, imagePaint, 0, 0);
                    }
                }
            }else {
                imageView.setImageBitmap(progressImage);
                imageView.invalidate();

                canvas.drawPath(mPathOfMask, mMaskPaint);
                canvas.drawBitmap(bp, 0, 0, imagePaint);

                if (touchStart) {
                    if (touchUP) {
                        progressImage = command.process(mPathOfMask, progressImage, bp, mMaskPaint, imagePaint, 0, 0);
                    }
                }
            }
        }

        private static final float TOUCH_TOLERANCE = 4;
        private void touch_start(float x, float y){
            if (drawMode.equals("draw")){
                mPath.moveTo(x, y);
            }else {
                mPathOfMask.moveTo(x, y);
            }


            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y){
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                if (drawMode.equals("draw")){
                    mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                }else {
                    mPathOfMask.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                }
                mX = x;
                mY = y;

            }
        }
        private void touch_up(float x, float y)
        {
            touchUP = true;
        }
        @Override
        public boolean onTouchEvent(MotionEvent event){
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    touchStart = true;
                    touchUP = false;
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

































    private SeekBar.OnSeekBarChangeListener sliderChangeListener_Stroke = new SeekBar.OnSeekBarChangeListener() {
        public void onStopTrackingTouch(SeekBar seekBar) {
            // not used here
        }
        public void onStartTrackingTouch(SeekBar seekBar) {
            // not used here
        }
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (sliderMovedByUser(seekBar, fromUser)) {
                drawSetting();
            }
        }
        private boolean sliderMovedByUser(SeekBar seekBar, boolean fromUser) {
            return fromUser && seekBar.getId() == slider_adjustment_stroke.getId();
        }
    };

    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
//            imageView.setDrawingCacheEnabled(true);
//            imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            imageView.buildDrawingCache(true);
//            Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());

            dataStorage.setLastResultBitmap(progressImage);
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
        getMenuInflater().inflate(R.menu.menu_splash, menu);
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
