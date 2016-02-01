package com.example.rmk.mediaediting.videoEditing;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
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

import com.example.rmk.mediaediting.GallerySet.EmoticonGalleryTool;
import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphic_process.DataStorage;
import com.example.rmk.mediaediting.graphics.commands.InsertImageCommand;
import com.example.rmk.mediaediting.graphics.commands.RotateCommand;

public class VideoEmoticonActivity extends Activity {

    //    private ImageView imageView;
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

    private int effectSelectFlag = 0;
    private int new_imageSizeX = 0;
    private int new_imageSizeY = 0;
    private int old_imageSizeX = 0;
    private int old_imageSizeY = 0;
    private boolean boundReset_GallerySelect = false;
    private Bitmap emoticonBackup = null;

    private int drawW, drawH;

    private VideoView videoView;
    private String firstVideoPath;
    private String lastVideoPath;
    private int videoWidth;
    private int videoHeight;
    private VideoPathSetting videoPathSetting;
    private ImageView coverImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataStorage = DataStorage.getInstance();
        videoPathSetting = VideoPathSetting.getInstance();
        dataStorage.setVideoEditStart(1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_emoticon);
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
//        activityStartFlag  = true;
        videoView = (VideoView)findViewById(R.id.image_view);
        coverImage = (ImageView)findViewById(R.id.coverImage);

        /// have to fix
        gallery = (Gallery) findViewById(R.id.emoticons_gallery);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemClickListener(listener);
        gallery.setSpacing(20);
        gallery.setSelection(2);

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
        emoticon = BitmapFactory.decodeResource(getResources(), R.drawable.rr20);

        cancelImage = Bitmap.createScaledBitmap(cancelImage, cancelImage.getWidth() / 4, cancelImage.getHeight() / 4, false);
        rotateImage = Bitmap.createScaledBitmap(rotateImage, rotateImage.getWidth() / 12 , rotateImage.getHeight() / 12, false);
        emoticon = Bitmap.createScaledBitmap(emoticon, emoticon.getWidth() , emoticon.getHeight(), false);

        progressImage = bgImage;
        coverImagUpdate();
    }

    private void coverImagUpdate(){
        coverImage.setImageBitmap(progressImage);
        coverImage.invalidate();
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
        bgImage = Bitmap.createScaledBitmap(bgImage, dataStorage.getVideoWidth(), dataStorage.getVideoHeight(), false);
    }

    public Bitmap captureView(){
        Bitmap resultImage = null;

        if (isGallerySelect && touchIsTrue)
        {
            preSave();
        }

        isGallerySelect = true;

        boundReset_GallerySelect = true;

        return resultImage;
    }

    public void preSave(){

        InsertImageCommand command = new InsertImageCommand(emoticonBackup);

        progressImage = command.process(progressImage,  (int)boundCircle.left,(int) boundCircle.top);

        coverImagUpdate();

//        imageView.setImageBitmap(progressImage);
//        imageView.invalidate();
    }








    Bitmap rotateImage;
    Bitmap cancelImage;
    Bitmap emoticon;


    Boolean selectGalleryItem = false;
    CirclePathInformation boundCircle;
    LinePathInformation boundLine;
    Boolean touchIsTrue = false;

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
            corner4();
            centerPoint();
            return result;
        }

        public Path reset(){
            Path result = new Path();
            return result;
        }

        public void centerPoint(){
            this.centerX = (right - left) / 2 + left;
            this.centerY = (bottom - top) / 2 + top;
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
            corner4();
            centerPoint();

        }

        public boolean isCornerDecide(float orderX, float orderY){
            boolean result = false;
            if (orderX > left && orderX < right && orderY < bottom && orderY > top){
                selectCorner = 10; result = true;
            }
            for (int i = 0; i < 4; i++){
                float dx = Math.abs(orderX - x[i]);
                float dy = Math.abs(orderY - y[i]);
                if (dx < radius * 3 && dy < radius * 3)
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

        public void process(float corner0x, float corner0y, float rotateCornerX, float rotateCornerY){
            top = command.top; left = command.left; right = command.right; bottom = command.bottom;

            Bitmap bp1, bp2, bp3;
            bp1 = imageResize(emoticon, 1);
            bp2 = imageResize(cancelImage, 4);
            bp3 = imageResize(rotateImage, 6);

            if (command.selectCorner == 2){
                bp1 = imageRotate(bp1);
            }
            emoticonBackup = bp1;

            canvas.drawBitmap(bp1, centerX - bp1.getWidth()/2, centerY - bp1.getHeight()/2, paint);
            canvas.drawBitmap(bp2, corner0x - bp2.getWidth()/2, corner0y - bp2.getHeight()/2, paint);
            canvas.drawBitmap(bp3, rotateCornerX - bp3.getWidth() /2 , rotateCornerY - bp3.getHeight() /2, paint);
        }

        public Bitmap imageResize(Bitmap bp, int ratio){
            Bitmap bitmap = null;
            float width, height;
            if (ratio == 1){
                bitmap = Bitmap.createScaledBitmap(bp, (int)(view1.emoticonBoundWidth/ratio), (int)(view1.emoticonBoundWidth/ratio), false);
            }else {
                bitmap = bp;
            }
            return bitmap;
        }

        public Bitmap imageRotate(Bitmap bp){
            Bitmap bitmap = null;
            if (view1.rotateAngle > 0.5) Log.d("dddd", "rotation test" + view1.rotateAngle);
            RotateCommand command = new RotateCommand((int)(-view1.rotateAngle * 180 / Math.PI));
            bitmap = command.process(bp);
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

        private float rotateAngle = 0;
        private float deltaAngle = 0;
        private double emoticonBoundWidth = 0;
        public int widthOfView, heightOfView;
        //////bound process

        LinePathInformation boundLine;
        float cornerRadius = 10f;
        float[] cornerX = new float[4]; float[] cornerY = new float[4];
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

            boundCircle = new CirclePathInformation();
            boundLine = new LinePathInformation();
            circlePaint.setStrokeWidth(cornerRadius);

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

            width = emoticon.getWidth(); height = emoticon.getHeight();
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

            PointF[] pointTwo = new PointF[2];
            pointTwo[0] = new PointF(cornerX[0], cornerY[0]);
            pointTwo[1] = new PointF(cornerX[1], cornerY[1]);
            setEmoticonBoundWidth(pointTwo);

            if(boundReset_GallerySelect)
            {
                touchIsTrue = true;
                width = emoticon.getWidth(); height = emoticon.getHeight();
                boundCircle.setRadius(cornerRadius);
                cornerX[0] = widthOfView / 2 - width / 2; cornerY[0] = heightOfView / 2 - height / 2;
                cornerX[1] = widthOfView / 2 + width / 2; cornerY[1] = heightOfView / 2 - height / 2 ;
                cornerX[2] = widthOfView / 2 + width / 2; cornerY[2] = heightOfView / 2 + height / 2 ;
                cornerX[3] = widthOfView / 2 - width / 2; cornerY[3] = heightOfView / 2 + height / 2;
                boundCircle.setXY(cornerX, cornerY);
                boundLine.setXY(cornerX, cornerY);
                boundReset_GallerySelect = false;
                setEmoticonBoundWidth(new PointF[]{new PointF(cornerX[0], cornerY[0]), new PointF(cornerX[1], cornerY[1])});
            }
            if (touchIsTrue && isGallerySelect) {
                canvas.drawPath(boundLine.process(), mPaint);
                canvas.drawPath(boundCircle.process(), circlePaint);
                command = new ImageBoxInformation(canvas, mBitmapPaint, boundCircle.centerX, boundCircle.centerY, boundCircle);
                command.process(cornerX[0], cornerY[0], cornerX[2], cornerY[2]);
            }
        }
        public void resetALLBound(){
//
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

            PointF[] pointF = new PointF[4];
            for (int j = 0; j < 4; j++){
                pointF[j] = new PointF(0,0);
            }
            for (int k = 0; k < 4; k++){
                int p = (k + 2) % 4;
                pointF[p].x = cornerX[k];
                pointF[p].y = cornerY[k];
            }
            PointF newPointF = new PointF(newX, newY);

            pointF = getPointsOfRect(pointF, newPointF);
            for (int i = 0; i < 4; i++)
            {
                cornerX[i] = pointF[(i + 2) % 4].x;
                cornerY[i] = pointF[(i + 2) % 4].y;
            }
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

        public void setEmoticonBoundWidth(PointF[] p){
            emoticonBoundWidth = (Math.sqrt((p[0].x - p[1].x) * (p[0].x - p[1].x) + (p[0].y - p[1].y) * (p[0].y - p[1].y)));
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 2;



        ///Rotate module

        private PointF convertCoordinate(PointF point,PointF center)
        {
            PointF point1 = new PointF();
            point1.x = point.x - center.x;
            point1.y = center.y -point.y;
            return point1;
        }

        private double getAngle(PointF point, double r)
        {
            if (point.x >= 0)
            {
                return Math.asin((double)(point.y/r));
            }else {
                return Math.PI - Math.asin((double)(point.y/r));
            }
        }

        private PointF[] getPointsOfRect(PointF point[], PointF newPoint1)
        {
            PointF center = new PointF();
            center.x = (point[0].x + point[2].x) / 2;
            center.y = (point[0].y + point[2].y) / 2;
            PointF p[] = new PointF[4];

            for (int i=0; i<4; i++)
            {
                p[i] = new PointF();
                p[i] = convertCoordinate(point[i],center);
            }

            PointF newP = new PointF();
            newP = convertCoordinate(newPoint1,center);
            double r0 = Math.sqrt(p[0].x *p[0].x + p[0].y * p[0].y);
            double r = Math.sqrt(newP.x *newP.x + newP.y * newP.y);
            double width = Math.sqrt((p[0].x - p[1].x) * (p[0].x - p[1].x) + (p[0].y - p[1].y) * (p[0].y - p[1].y));
            emoticonBoundWidth = width;
            double alpha = 2 * Math.asin(width / (2 * r0));

            double oldAlpha = getAngle(p[0],r0);
            double newAlpha = getAngle(newP,r);
            /// for emoticon
            deltaAngle = (float)(newAlpha - oldAlpha);
            rotateAngle += deltaAngle;
            ////
            double alpha1 = newAlpha - alpha;
            double alpha2 = newAlpha - Math.PI;
            double alpha3 = alpha + newAlpha;
            PointF returnPoint[] = new PointF[4];

            for (int i = 0; i < 4; i++)
            {
                returnPoint[i] = new PointF();
            }
            returnPoint[0] = newPoint1;
            returnPoint[1].x = (float)(r * Math.cos(alpha1)) + center.x;
            returnPoint[1].y = center.y - (float)(r * Math.sin(alpha1));

            returnPoint[2].x = (float)(r * Math.cos(alpha2)) + center.x;
            returnPoint[2].y = center.y - (float)(r * Math.sin(alpha2));

            returnPoint[3].x = (float)(r * Math.cos(alpha3)) + center.x;
            returnPoint[3].y = center.y - (float)(r * Math.sin(alpha3));

            return returnPoint;
        }

        ///////////////// end rotate module


        private void touch_start(float x, float y) {
            mX = x;
            mY = y;
            if (boundCircle.isCornerDecide(x, y))
            {
                switch (boundCircle.getSelectCorner())
                {
                    case 0:
                        command.reset();
                        touchIsTrue = false;
                        break;
                    case 2:
                        rotateAndResizeBound(mX, mY);
                        break;
                    case 10:
                        moveImageAndBound(0, 0);
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
                            touchIsTrue = false;
                            break;
                        case 2:
                            rotateAndResizeBound(mX, mY);
                            break;
                        case 10:
                            moveImageAndBound(realDx, realDy);
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







    //// Gallery Adapter
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            effectSelectFlag = position;
            captureView();
            emoticon = BitmapFactory.decodeResource(getResources(), EmoticonGalleryTool.ImageIds_FrameGallery[position]);

            view1.invalidate();
        }
    };

    public class ImageAdapter extends BaseAdapter {
        int galleryItemBackground;
        private Context context;
        private Integer[] mImageIds = EmoticonGalleryTool.ImageIds_FrameGallery;

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

    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            InsertImageCommand command = new InsertImageCommand(emoticonBackup);

            progressImage = command.process(progressImage,  (int) boundCircle.left,(int) boundCircle.top);
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




























}
