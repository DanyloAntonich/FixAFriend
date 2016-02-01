package com.example.rmk.mediaediting.videoEditing;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.rmk.mediaediting.GallerySet.TextGalleryTool;
import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphic_process.DataStorage;
import com.example.rmk.mediaediting.graphics.commands.InsertTextCommand;
import com.example.rmk.mediaediting.utils.AngleCalculation;
import com.example.rmk.mediaediting.utils.Vector2D;

public class VideoTextActivity extends Activity {

    private TextView okButton;
    private TextView cancelButton;

    private RelativeLayout drawWindow;

    Paint mPaint;

    private Gallery gallery;

    private Bitmap bgImage = null;
    private DataStorage dataStorage;

    private Boolean isGallerySelect = false;

    /// this is a part for emoticon
    private DrawingView view1;
    private Bitmap progressImage;
    private boolean flagForSaveImage = false;
    private boolean preSaveFlag = false;

    private boolean boundReset_GallerySelect = false;
    private Bitmap emoticonBackup = null;

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
        setContentView(R.layout.activity_video_text);
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

        /// have to fix
        gallery = (Gallery) findViewById(R.id.text_gallery);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemClickListener(listener);

        okButton = (TextView) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);

        getImageFromStorage();
        getVideoPath();
        DisplayVideoInVideoView();
        coverImagUpdate();

        ///
        drawWindow = (RelativeLayout)findViewById(R.id.drawWindow);
        view1 =new DrawingView(this);
        drawWindow.addView(view1);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(getResources().getColor(R.color.red_color));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(1);

        cancelImage = BitmapFactory.decodeResource(getResources(), R.drawable.btn_delete);
        rotateImage = BitmapFactory.decodeResource(getResources(), R.drawable.btn_reset);
//        emoticon = BitmapFactory.decodeResource(getResources(), R.drawable.sticker001);

        cancelImage = Bitmap.createScaledBitmap(cancelImage, cancelImage.getWidth() / 4, cancelImage.getHeight() / 4, false);
        rotateImage = Bitmap.createScaledBitmap(rotateImage, rotateImage.getWidth() / 12 , rotateImage.getHeight() / 12, false);
//        emoticon = Bitmap.createScaledBitmap(emoticon, emoticon.getWidth() , emoticon.getHeight(), false);

        progressImage = bgImage;
    }
    private void coverImagUpdate(){
        coverImage.setImageBitmap(progressImage);
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
        bgImage = Bitmap.createScaledBitmap(bgImage, dataStorage.getVideoWidth(), dataStorage.getVideoHeight(), false);

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

    public Bitmap captureView(){
        Bitmap resultImage = null;

        if (isGallerySelect) preSave();

        preSaveFlag = false;

        isGallerySelect = true;

        boundReset_GallerySelect = true;

        return resultImage;
    }
    public void preSave(){

        InsertTextCommand command = new InsertTextCommand(emoticon.getText());
        progressImage = command.process(progressImage, (int)boundCircle.left, (int)boundCircle.top, (int)emoticon.getTextSize());

        coverImagUpdate();

    }

    Bitmap rotateImage;
    Bitmap cancelImage;
    Emoticon emoticon;
    Boolean selectGalleryItem = false;
    CirclePathInformation boundCircle;
    LinePathInformation boundLine;



    public class CirclePathInformation{
        private float radius;
        private float[] x = new float[4];
        private float[] y = new float[4];
        private int selectCorner = 100;
        private boolean isCorner = false;
        private float centerX, centerY;
        public float top = 0, left = 0, bottom = 0, right = 0;

        public  CirclePathInformation(){
            centerPoint();
            corner4();
        }

        public Path process(){
            Path result = new Path();
            for (int i = 0; i < 4; i++)
            {
                result.addCircle(x[i], y[i], radius, Path.Direction.CW);
            }
            centerPoint();
            corner4();
            return result;
        }

        public Path reset(){
            Path result = new Path();
            return result;
        }

        public void centerPoint(){
            this.centerX = Math.abs(x[0] - x[1]) / 2 + left;
            this.centerY = Math.abs(y[0] - y[3]) / 2 + top;
        }

        public void corner4(){
            float top = 0, left = 0, bottom = 0, right = 0;

            top = y[0]; left = x[0];
            for (int i = 0; i < 4; i++){
                if (left > x[i]) left = x[i];
                if (top > y[i]) top = y[i];
                if (right < x[i]) right = x[i];
                if (bottom < y[i]) bottom = y[i];
            }
            this.top = top; this.left = left; this.right = right; this.bottom = bottom;
        }

        public void setRadius(float radius){ this.radius = radius;}

        public float getRadius(){ return radius; }

        public void setXY(float[] x, float[] y){
            this.x = x; this.y = y;
            centerPoint();
            corner4();
        }

        public boolean isCornerDecide(float orderX, float orderY){
            boolean result = false;
            if (orderX > left && orderX < right && orderY < bottom && orderY > top){
                selectCorner = 10; result = true;
            }
            for (int i = 0; i < 4; i++){
                float dx = Math.abs(orderX - x[i]);
                float dy = Math.abs(orderY - y[i]);
                if (dx < radius && dy < radius)
                {
                    selectCorner = i;
                    result = true;
                }
            }

            return result;
        }

        public int getSelectCorner(){
            return selectCorner;
        }
    }

    public class LinePathInformation{
        private float[] x = new float[4];
        private float[] y = new float[4];

        public float left, top, right, bottom;
        public  LinePathInformation(){
        }
        public Path process()
        {
            Path result = new Path();
            result.moveTo(x[0], y[0]);
            for (int i = 1; i < 5; i++)
            {
                result.lineTo(x[i % 4], y[i % 4]);
            }
            return result;
        }
        public void setXY(float[] x, float[] y){ this.x = x; this.y = y;}
        public float[] getX(){ return x; }
        public float[] getY() { return y; }

    }

    public class ImageBoxInformation{
        Canvas canvas;
        Paint paint;
        float ratio = 1.0F;
        CirclePathInformation command;
        float top, left, right, bottom;

        float centerX, centerY;

        public ImageBoxInformation(Canvas canvas, Paint paint, float centerX, float centerY, CirclePathInformation command){
            this.canvas = canvas;
            this.paint = paint;
            this.centerX = centerX;
            this.centerY = centerY;
            this.command = command;
        }

        public void process(){
            top = command.top; left = command.left; right = command.right; bottom = command.bottom;
            Bitmap bp1, bp2, bp3;

            bp2 = imageResize(cancelImage, 4);

            String aa = editText.getText().toString();
            emoticon.setString(aa);

            drawWindow.removeView(editText);
            if (!preSaveFlag) emoticon.process((int) ((boundCircle.bottom - boundCircle.top) / 2.4F));
            editText.invalidate();

            canvas.drawBitmap(bp2, boundCircle.left - bp2.getHeight() / 2, boundCircle.top - bp2.getWidth() / 2, paint);
        }

        public Bitmap imageResize(Bitmap bp, int ratio){
            Bitmap bitmap = null;
            float width, height;
            if (ratio == 1){
                width = ((right - left) != 0) ? Math.abs(right - left)/ratio : 1.01F;
                height = ((bottom - top) != 0) ? Math.abs(bottom - top)/ratio : 1.01F;
                bitmap = Bitmap.createScaledBitmap(bp, (int)width, (int)height, false);
            }else {
                bitmap = bp;
            }
            return bitmap;
        }

        public Bitmap imageRotate(Bitmap bp){
            Bitmap bitmap = null;

            return bitmap;
        }


        public void reset(){
            canvas = new Canvas();
        }
    }

    public class DrawingView extends View {
        public int width;
        public  int height;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;
        private Paint mRegionPaint;


        public int widthOfView, heightOfView;
        //////bound process

        LinePathInformation boundLine;
        float cornerRadius = 10f;
        float[] cornerX = new float[4]; float[] cornerY = new float[4];
        Boolean touchIsTrue = false;
        ImageBoxInformation command;


        public DrawingView(Context c) {
            super(c);
            context=c;

            mPath = new Path();

            mBitmapPaint = new Paint(Paint.DEV_KERN_TEXT_FLAG);

            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(getResources().getColor(R.color.thin_blue_color));
            circlePaint.setStyle(Paint.Style.FILL);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(cornerRadius);

            boundCircle = new CirclePathInformation();
            boundLine = new LinePathInformation();

            emoticon = new Emoticon();
            touchIsTrue = true;
        }
        public void reset(){

            mPath = new Path();

            mBitmapPaint = new Paint(Paint.DEV_KERN_TEXT_FLAG);

            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(getResources().getColor(R.color.thin_blue_color));
            circlePaint.setStyle(Paint.Style.FILL);
            circlePaint.setStrokeJoin(Paint.Join.MITER);

            boundCircle = new CirclePathInformation();
            boundLine = new LinePathInformation();
            circlePaint.setStrokeWidth(cornerRadius);

            touchIsTrue = true;
        }
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            this.widthOfView = w; this.heightOfView = h;

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

            width = 250; height = 120;
            boundCircle.setRadius(cornerRadius);
            cornerX[0] = w / 2 - width / 2; cornerY[0] = h / 2 - height / 2;
            cornerX[1] = w / 2 + width / 2; cornerY[1] = h / 2 - height / 2 ;
            cornerX[2] = w / 2 + width / 2; cornerY[2] = h / 2 + height / 2 ;
            cornerX[3] = w / 2 - width / 2; cornerY[3] = h / 2 + height / 2;
            boundCircle.setXY(cornerX, cornerY);
            boundLine.setXY(cornerX, cornerY);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if(boundReset_GallerySelect)
            {
//                mBitmap = Bitmap.createBitmap(widthOfView, heightOfView, Bitmap.Config.ARGB_8888);
//                width = (int)emoticon.getWidth(); height = (int)emoticon.getHeight();
                boundCircle.setRadius(cornerRadius);
                cornerX[0] = widthOfView / 2 - width / 2; cornerY[0] = heightOfView / 2 - height / 2;
                cornerX[1] = widthOfView / 2 + width / 2; cornerY[1] = heightOfView / 2 - height / 2 ;
                cornerX[2] = widthOfView / 2 + width / 2; cornerY[2] = heightOfView / 2 + height / 2 ;
                cornerX[3] = widthOfView / 2 - width / 2; cornerY[3] = heightOfView / 2 + height / 2;
                boundCircle.setXY(cornerX, cornerY);
                boundLine.setXY(cornerX, cornerY);
                boundReset_GallerySelect = false;
            }


            if (touchIsTrue && isGallerySelect) {
                canvas.drawPath(boundLine.process(), mPaint);
                canvas.drawPath(boundCircle.process(), circlePaint);

                //                emoticon.process();
                command = new ImageBoxInformation(canvas, mBitmapPaint, boundCircle.centerX, boundCircle.centerY, boundCircle);
                command.process();
            }

        }

        private void setBoundAll(int cornerOrder, float newX, float newY){
            switch (cornerOrder)
            {
                case 0:
                    cornerX[cornerOrder] = newX;
                    cornerY[cornerOrder] = newY;
                    cornerY[(cornerOrder + 1) % 4] = newY;
                    cornerX[(cornerOrder + 3) % 4] = newX;
                    break;
                case 1:
                    cornerX[cornerOrder] = newX;
                    cornerY[cornerOrder] = newY;
                    cornerX[(cornerOrder + 1) % 4] = newX;
                    cornerY[(cornerOrder + 3) % 4] = newY;
                    break;
                case 2:
                    cornerX[cornerOrder] = newX;
                    cornerY[cornerOrder] = newY;
                    cornerY[(cornerOrder + 1) % 4] = newY;
                    cornerX[(cornerOrder + 3) % 4] = newX;
                    break;
                case 3:
                    cornerX[cornerOrder] = newX;
                    cornerY[cornerOrder] = newY;
                    cornerX[(cornerOrder + 1) % 4] = newX;
                    cornerY[(cornerOrder + 3) % 4] = newY;
                    break;
            }
            boundCircle.setXY(cornerX, cornerY);
            boundLine.setXY(cornerX, cornerY);
        }

        public void rotateAndResizeBound(float newX, float newY){

            Vector2D v3 = new Vector2D(cornerX[3], cornerY[3]);
            Vector2D v2 = new Vector2D(cornerX[2], cornerY[2]);
            Vector2D v1 = new Vector2D(cornerX[1], cornerY[1]);
            Vector2D v0 = new Vector2D(cornerX[0], cornerY[0]);

            AngleCalculation command = new AngleCalculation(v0, v2);
            float angle0 = command.angleTwo;

            cornerX[2] = newX;
            cornerY[2] = newY;
            Vector2D v2again = new Vector2D(cornerX[2], cornerY[2]);
            command = new AngleCalculation(v0, v2again);
            float angle1 = command.angleTwo;

            float deltaAngle = 0;
            deltaAngle = angle0 - angle1;
            float direction = 0;
//            if (angle1 > angle0) {
//                direction = 0; // CCW
//                deltaAngle = angle1 - angle0;
//            }
//
//            if (angle1 < angle0)
//            {
//                direction = 1; // CW
//                deltaAngle = angle0 - angle1;
//            }

            v1 = command.rotateVector(deltaAngle, v0, v1);
            v2 = command.rotateVector(deltaAngle, v0, v2);

            v3 = command.rotateVector(deltaAngle, v0, v3);

            cornerX[1] = v1.x; cornerY[1] = v1.y;
            cornerX[2] = v2.x; cornerY[2] = v2.y;
            cornerX[3] = v3.x; cornerY[3] = v3.y;

            boundCircle.setXY(cornerX, cornerY);
            boundLine.setXY(cornerX, cornerY);
        }

        public void moveImageAndBound(float deltaX, float deltaY){

            for(int i = 0; i < 4; i++)
            {
                cornerX[i] += deltaX; cornerY[i] += deltaY;
            }

            boundCircle.setXY(cornerX, cornerY);
            boundLine.setXY(cornerX, cornerY);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 2;

        private void touch_start(float x, float y) {
            mX = x;
            mY = y;
            if (boundCircle.isCornerDecide(x, y))
            {
                switch (boundCircle.getSelectCorner())
                {
                    case 0:
                        command.reset();
                        drawWindow.removeView(editText);
                        touchIsTrue = false;
                        break;

//                    case 2:
//                        rotateAndResizeBound(mX, mY);
//                        break;
                    case 10:
                        moveImageAndBound(0, 0);
                        break;
                    default:
                        setBoundAll(boundCircle.getSelectCorner(), x, y);
                        break;
                }
            }
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);

            float realDx = x - mX ; float realDy = y - mY;

            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, boundCircle.radius * 2, Path.Direction.CW);

                if (boundCircle.isCornerDecide(x, y)){
                    switch (boundCircle.getSelectCorner())
                    {
                        case 0:
                            command.reset();
                            drawWindow.removeView(editText);
                            touchIsTrue = false;
                            break;
//                        case 2:
//                            rotateAndResizeBound(mX, mY);
//                            break;
                        case 10:
                            moveImageAndBound(realDx, realDy);
                            break;
                        default:
                            setBoundAll(boundCircle.getSelectCorner(), x, y);
                            break;
                    }
                }
            }
        }
        private void touch_up() {
            circlePath.reset();
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


    public EditText editText;
    public float textScaleX = 1;
    public float textScaleY = 1;
    public class Emoticon{
        private float width;
        private float height;
        private float top;
        private float left;
        private float textSize;
        private String text = "Text";
        public Emoticon(){}


        public float topOffSet = 100;

        public void setString(String text){
            this.text = text;
        }

        public void process(int fontSize){

            textScaleX = width / (boundCircle.right - boundCircle.left);
            textScaleY = height / (boundCircle.bottom - boundCircle.top);

            editText = new EditText(getBaseContext());
            editText.setFocusable(true);

            editText.setText(this.text);
            editText.setTextSize(fontSize);

            editText.setX(boundCircle.left);
            editText.setY(boundCircle.top - 50);
//            editText.setEms(10);
            editText.setWidth((int) boundCircle.right - (int) boundCircle.left);

            editText.setScaleX(textScaleX);
            editText.setScaleY(textScaleY);

            drawWindow.removeView(editText);
            drawWindow.addView(editText);

            textSize = fontSize;
        }

        public float getWidth(){
            return this.width;
        }
        public float getHeight(){
            return  this.height;
        }
        public float getTop(){
            return this.top;
        }
        public float getLeft(){
            return this.left;
        }
        public float getTextSize(){
            return textSize;
        }
        public String getText(){
            return editText.getText().toString();
        }

        public void setWidthAndHeight(float w, float h){
            this.width = w; this.height = h;
        }
        public void setLeftAndTop(float mLeft, float mTop){
            this.top = mTop; this.left = mLeft;
        }
        public void invalidate(){
            editText.invalidate();
        }

    }

    //// Gallery Adapter
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {
            if (position == 0) {
                preSaveFlag = true;
                captureView();

                emoticon = new Emoticon();
                emoticon.setWidthAndHeight(view1.width, view1.height);
                emoticon.setString("Text");
                emoticon.process(getResources().getDimensionPixelSize(R.dimen.text_size_medium));
                emoticon.setLeftAndTop(boundCircle.left, boundCircle.top);
                emoticon.invalidate();

                view1.invalidate();
            }
        }
    };



    public class ImageAdapter extends BaseAdapter {
        int galleryItemBackground;
        private Context context;
        private Integer[] mImageIds = TextGalleryTool.ImageIds_TextGallery;

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
            imageView.setLayoutParams(new Gallery.LayoutParams(150, 150));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setBackgroundResource(galleryItemBackground);
            return imageView;
        }
    }

    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            InsertTextCommand command = new InsertTextCommand(emoticon.getText());

            progressImage = command.process(progressImage,  (int) boundCircle.left,(int) boundCircle.top, (int)emoticon.getTextSize());
            progressImage = Bitmap.createScaledBitmap(progressImage, dataStorage.getVideoWidth(), dataStorage.getVideoHeight(), false);
            dataStorage.setCoverImageBitmap(progressImage);
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
        getMenuInflater().inflate(R.menu.menu_video_text, menu);
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
