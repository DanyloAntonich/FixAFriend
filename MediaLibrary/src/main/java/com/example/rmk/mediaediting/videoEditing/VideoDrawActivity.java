package com.example.rmk.mediaediting.videoEditing;

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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphic_process.DataStorage;
import com.example.rmk.mediaediting.graphics.commands.DrawCircleCommand;
import com.example.rmk.mediaediting.graphics.commands.DrawPathCommand;
import com.example.rmk.mediaediting.graphics.commands.SaveMaskEffect;

public class VideoDrawActivity extends Activity {

    private ImageView imageViewCircle;
    private TextView okButton;
    private TextView cancelButton;

    private Bitmap bgImage = null;
    private Bitmap bgProgressImage = null;

    private Bitmap bmCircle = null;
    private DataStorage dataStorage;

    Paint mPaint;
    private Path mPath;
    float Mx1,My1;
    float x,y;
    private SeekBar slider_adjustment_RGB;
    private SeekBar slider_adjustment_stroke;
    private int drawW, drawH;


    private VideoView videoView;
    private String firstVideoPath;
    private String lastVideoPath;
    private int videoWidth;
    private int videoHeight;
    private VideoPathSetting videoPathSetting;
    private ImageView coverImage;

    DrawingView view1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataStorage = DataStorage.getInstance();
        videoPathSetting = VideoPathSetting.getInstance();
        dataStorage.setVideoEditStart(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_draw);
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

        imageViewCircle = (ImageView)findViewById(R.id.image_view_circle);
        bmCircle = BitmapFactory.decodeResource(getResources(), R.drawable.back);
        imageViewCircle.setImageBitmap(bmCircle);

        okButton = (TextView) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);

        ImageView rToolPanel = (ImageView)findViewById(R.id.image_view_circle);
        rToolPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapDrawingMethod();

            }
        });

        getVideoPath();
        DisplayVideoInVideoView();
        coverImagUpdate();
        bgProgressImage = dataStorage.getCoverImageBitmap();
        bgProgressImage = Bitmap.createScaledBitmap(bgProgressImage, videoWidth, videoHeight, false);
        bgImage = bgProgressImage;

        slider_adjustment_RGB = (SeekBar) findViewById(R.id.slider_adjustment_RGB);
        slider_adjustment_RGB.setProgress(51);
        slider_adjustment_stroke = (SeekBar) findViewById(R.id.slider_adjustment_stroke);
        slider_adjustment_stroke.setProgress(10);

        slider_adjustment_RGB.setOnSeekBarChangeListener(sliderChangeListener_RGB);
        slider_adjustment_stroke.setOnSeekBarChangeListener(sliderChangeListener_stroke);

        view1 =new DrawingView(this);
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
        drawSetting();
    }

    String drawMode = "draw";
    private void swapDrawingMethod() {
        Bitmap rBitmapEraser = BitmapFactory.decodeResource(getResources(), R.drawable.btn_eraser);

        imageViewCircle.setImageBitmap((drawMode.equals("draw")) ? rBitmapEraser : bmCircle);

        imageViewCircle.invalidate();
        drawMode = (drawMode.equals("draw")) ? "delete" : "draw";
        mPathOfMask.reset();
        view1.invalidate();
    }

    private void coverImagUpdate(){
        coverImage.setImageBitmap(dataStorage.getCoverImageBitmap());
        coverImage.invalidate();
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
//        getImageFromStorage();
        LinearLayout parentWindow = (LinearLayout)findViewById(R.id.parentWindow);
        parentWindow.setPadding((drawW - dataStorage.getVideoWidth()) / 2, (drawH - dataStorage.getVideoHeight()) / 2, (drawW - dataStorage.getVideoWidth()) / 2, (drawH - dataStorage.getVideoHeight()) / 2);
        parentWindow.invalidate();
    }
    private void drawSetting(){

        int radius = slider_adjustment_stroke.getProgress();

        int i = slider_adjustment_RGB.getProgress();

        float a = 255;
        float r = 0;
        float g = 0;
        float b = 0;

        float ratio = 255.f / 40.f;

        if (i < 40 ) {
            r = ratio * i;
            g = ratio * i;
            b = ratio * i;
        }
        if (i < 81 && i > 41){
            r = 255;
            g = ratio * (i - 40); b = 0; }
        if (i < 121 && i > 80) {
            r = 255 - ratio * (i - 80); g = 255; b = 0; }
        if (i < 161 && i > 120) {
            r = 0; g = 255; b = ratio * (i - 120);
        }
        if (i < 201 && i > 160){
            r = 0; g = 255 - ratio * (i - 160); b = 255;
        }

        mPaint.setColor(Color.argb((int) a, (int) r, (int) g, (int) b));
        mPaint.setStrokeWidth(radius);
        rMaskPaint.setStrokeWidth(radius);

        DrawCircleCommand command = new DrawCircleCommand(radius / 2 , 0.5F, 0.5F, Color.argb((int) a, (int) r, (int) g, (int) b));
        bmCircle = command.process(bmCircle);
        imageViewCircle.setImageBitmap(bmCircle);
        imageViewCircle.invalidate();

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


    private SeekBar.OnSeekBarChangeListener sliderChangeListener_RGB = new SeekBar.OnSeekBarChangeListener() {
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
            return fromUser && seekBar.getId() == slider_adjustment_RGB.getId();
        }
    };

    private SeekBar.OnSeekBarChangeListener sliderChangeListener_stroke = new SeekBar.OnSeekBarChangeListener() {
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
            dataStorage.setCoverImageBitmap(bgProgressImage);
//            dataStorage.setVideoIsModified(true);
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
        getMenuInflater().inflate(R.menu.menu_draw, menu);
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


    // part for eraser

    Paint rMaskPaint;
    Paint rPaintOfDrawingPaint;
    private Path mPathOfMask;
    private SaveMaskEffect saveMaskEffect= new SaveMaskEffect();
    // end eraser

    public class DrawingView extends View {

        public int width;
        public  int height;
        private Bitmap  mBitmap;
        private Canvas mCanvas;

        private Paint   mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context=c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);

            // for erasing
            mPathOfMask = new Path();
            rMaskPaint = new Paint();
            rMaskPaint.setAntiAlias(true);
            rMaskPaint.setDither(true);
            rMaskPaint.setColor(0xFFFF0000);
            rMaskPaint.setStyle(Paint.Style.STROKE);
            rMaskPaint.setStrokeJoin(Paint.Join.ROUND);
            rMaskPaint.setStrokeCap(Paint.Cap.ROUND);
            rMaskPaint.setStrokeWidth(10);

            rMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

            rPaintOfDrawingPaint = new Paint();
            rPaintOfDrawingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (drawMode.equals("draw"))
            {
                canvas.drawBitmap(bgProgressImage, 0, 0, mBitmapPaint);
                canvas.drawPath(mPath, mPaint);
            }else {

                coverImage.setImageBitmap(bgProgressImage);
                coverImage.invalidate();
                canvas.drawPath(mPathOfMask, rMaskPaint);
                canvas.drawBitmap(bgImage, 0, 0, rPaintOfDrawingPaint);

                bgProgressImage = saveMaskEffect.process(mPathOfMask, bgProgressImage, bgImage, rMaskPaint, rPaintOfDrawingPaint, 0, 0);
            }

//            canvas.drawPath( circlePath,  circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            if (drawMode.equals("draw")) {
                mPath.reset();
                mPath.moveTo(x, y);
            }else {
                mPathOfMask.moveTo(x, y);
            }

            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                if (drawMode.equals("draw"))
                {
                    mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                }else {
                    mPathOfMask.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                }
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }
        private void touch_up() {
            if (drawMode.equals("draw")) {
                mPath.lineTo(mX, mY);
            }else {
                mPathOfMask.lineTo(mX, mY);
            }
            circlePath.reset();

            // commit the path to our offscreen
            mCanvas.drawPath(mPath,  mPaint);

            DrawPathCommand command = new DrawPathCommand();
            bgProgressImage = command.process(bgProgressImage, mPath, mPaint);

            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
}
