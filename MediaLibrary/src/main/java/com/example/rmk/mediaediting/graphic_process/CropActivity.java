package com.example.rmk.mediaediting.graphic_process;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rmk.mediaediting.GallerySet.CropGalleryTool;
import com.example.rmk.mediaediting.GallerySet.GeneralGalleryAdapter;
import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphics.commands.CropCommand;

public class CropActivity extends Activity {
    private ImageView imageView;
    private TextView okButton;
    private TextView cancelButton;

    private Gallery gallery;
    private Bitmap[] proceededImage = new Bitmap[5];

    private RelativeLayout drawWindow;
    Paint mPaint;


    private Bitmap bgImage = null;
    private DataStorage dataStorage;

    private int effectSelectFlag = 0;
    private int new_imageSizeX = 0;
    private int new_imageSizeY = 0;
    private int old_imageSizeX = 0;
    private int old_imageSizeY = 0;

    //bug fix
    private int drawW;
    private int drawH;
    DrawingView view1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        dataStorage = DataStorage.getInstance();
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


        okButton = (TextView) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);

        getImageFromStorage();
        imageView.setImageBitmap(bgImage);

        ///
        drawWindow = (RelativeLayout)findViewById(R.id.drawWindow);
        view1 =new DrawingView(this);
        drawWindow.addView(view1);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(getResources().getColor(R.color.blue_color));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(1);

        getGalleryImage();
        gallery = (Gallery) findViewById(R.id.crop_gallery);
        gallery.setAdapter(new GeneralGalleryAdapter(this, CropGalleryTool.Names, proceededImage));
        gallery.setSpacing(20);
        gallery.setSelection(2);
        gallery.setOnItemClickListener(listener);
    }

    private void getGalleryImage() {
        int width  = bgImage.getWidth();
        int height = bgImage.getHeight();

        proceededImage[0] = bgImage;
        proceededImage[1] = bgImage;
        proceededImage[2] = Bitmap.createScaledBitmap(bgImage, (int)(0.75F * width), height, false);
        proceededImage[3] = Bitmap.createScaledBitmap(bgImage, (int)(0.667F * width), height, false);
        proceededImage[4] = Bitmap.createScaledBitmap(bgImage, (int)(0.5625F * width), height, false);
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
        parentWindow.setPadding((drawW - bgImage.getWidth())/2, (drawH - bgImage.getHeight())/2, (drawW - bgImage.getWidth())/2, (drawH - bgImage.getHeight())/2);
        parentWindow.invalidate();
    }
    private void getImageFromStorage(){
        bgImage = dataStorage.getBitmap();
    }



    CirclePathInformation boundCircle;
    LinePathInformation boundLine;
    public class CirclePathInformation{
        private float radius;
        private float[] x = new float[4];
        private float[] y = new float[4];
        private int selectCorner = 5;
        private boolean isCorner = false;
        private float centerX, centerY;
        public float top = 0, left = 0, bottom = 0, right = 0;

        public  CirclePathInformation(){
            centerPoint();
            corner4();
        }

        public Path process()
        {
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

        public void setXY(float[] x, float[] y){ this.x = x; this.y = y;}
        public float[] getX(){ return x; }
        public float[] getY() { return y; }
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

        public void setSelectCorner(boolean isSelectCorner) { this.isCorner = isSelectCorner;}
        public int getSelectCorner(){
            return selectCorner;
        }
    }

    public class LinePathInformation{
        private int boundWidth, boundHeight;
        private int boundTop, boundLeft;

        private float[] x = new float[4];
        private float[] y = new float[4];

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

        public Path remainRegion(float originalTop, float originalLeft , float originalBottom, float originalRight){
            Path result = new Path();
            float top = 0, left = 0, bottom = 0, right = 0;

            top = y[0]; left = x[0];
            for (int i = 0; i < 4; i++){
                if (left > x[i]) left = x[i];
                if (top > y[i]) top = y[i];
                if (right < x[i]) right = x[i];
                if (bottom < y[i]) bottom = y[i];
            }
            result.addRect(originalLeft, originalTop, originalRight, top, Path.Direction.CW);
            result.addRect(originalLeft, top, left, bottom, Path.Direction.CCW);
            result.addRect(right, top, originalRight, bottom, Path.Direction.CCW);
            result.addRect(originalLeft, bottom, originalBottom, originalRight, Path.Direction.CW);

            boundTop = (int)top;
            boundLeft = (int)left;
            boundWidth = (int)(right - left);
            boundHeight = (int)(bottom - top);
            return result;
        }

        public void setXY(float[] x, float[] y){ this.x = x; this.y = y;}
        public float[] getX(){ return x; }
        public float[] getY() { return y; }

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
        private float ratioOfWidthHeight = 1;


        //////bound process

        float cornerRadius = 20f;
        float[] cornerX = new float[4]; float[] cornerY = new float[4];
        Boolean touchIsTrue = false;

        public DrawingView(Context c) {
            super(c);
            context=c;

            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DEV_KERN_TEXT_FLAG);

            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(getResources().getColor(R.color.blue_color));
            circlePaint.setStyle(Paint.Style.FILL);
            circlePaint.setStrokeJoin(Paint.Join.MITER);

            mRegionPaint = new Paint();
            mRegionPaint.setAntiAlias(true);
            mRegionPaint.setDither(true);
            mRegionPaint.setColor(getResources().getColor(R.color.red_color));
            mRegionPaint.setStyle(Paint.Style.FILL);
            mRegionPaint.setStrokeJoin(Paint.Join.ROUND);
//            mRegionPaint.setStrokeCap(Paint.Cap.ROUND);
            mRegionPaint.setStrokeWidth(1);

            boundCircle = new CirclePathInformation();
            boundLine = new LinePathInformation();
            circlePaint.setStrokeWidth(cornerRadius);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            Log.d("widthAndheigt", "width   " + w + "height" + h + "old" + oldw + "    " + oldh);

            width = w; height = h;
            boundCircle.setRadius(cornerRadius);
            setCorner(1);
            boundCircle.setXY(cornerX, cornerY);
            boundLine.setXY(cornerX, cornerY);
        }
        public void setCorner(float ratioOfWH){
            ratioOfWidthHeight = ratioOfWH;
            float customWidth = ratioOfWH * width;
            cornerX[0] = width / 2 - customWidth / 2 ; cornerY[0] = 0;
            cornerX[1] = width / 2 + customWidth / 2; cornerY[1] = 0;
            cornerX[2] = width / 2 + customWidth / 2; cornerY[2] = height;
            cornerX[3] = width / 2 - customWidth / 2; cornerY[3] = height;
        }

        public void moveImageAndBound(float deltaX, float deltaY){

            for(int i = 0; i < 4; i++)
            {
                cornerX[i] += deltaX; cornerY[i] += deltaY;
            }
            boundCircle.setXY(cornerX, cornerY);
            boundLine.setXY(cornerX, cornerY);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawPath(circlePath, circlePaint);

            canvas.drawPath(boundLine.process(), mPaint);
            canvas.drawPath(boundCircle.process(), circlePaint);
            canvas.drawPath(boundLine.remainRegion(0, 0, (float) width, (float) height), mRegionPaint);
        }

        private void setBoundWithY(int corner, float deltaY)
        {
            cornerX[corner] += deltaY * ratioOfWidthHeight;
            cornerY[corner] += deltaY;
        }
        private void setBoundAll(int cornerOrder, float deltaX, float deltaY){
            float newX, newY;
            if (effectSelectFlag == 0){
                cornerX[cornerOrder] += deltaX;
                cornerY[cornerOrder] += deltaY;
                newX = cornerX[cornerOrder]; newY = cornerY[cornerOrder];
            }else {
                cornerX[cornerOrder] += deltaY * ratioOfWidthHeight;
                cornerY[cornerOrder] += deltaY;
                newX = cornerX[cornerOrder];
                newY = cornerY[cornerOrder];
            }
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


        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mX = x;
            mY = y;
            if (boundCircle.isCornerDecide(x, y))
            {
                circlePath.addCircle(mX, mY, boundCircle.getRadius() * 2, Path.Direction.CW);
                switch (boundCircle.getSelectCorner()) {
                    case 10:
                        moveImageAndBound(0, 0);
                        break;
                    default:
                        setBoundAll(boundCircle.getSelectCorner(), 0, 0);
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
                    circlePath.addCircle(mX, mY, boundCircle.getRadius(), Path.Direction.CW);
                    switch (boundCircle.getSelectCorner()) {
                        case 10:
                            moveImageAndBound(realDx, realDy);
                            break;
                        default:
                            setBoundAll(boundCircle.getSelectCorner(), realDx, realDy);
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




















    //// Gallery Adapter
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            effectSelectFlag = position;
            switch (position){
                case 0:
                    view1.setCorner(1);
                    break;
                case 1:
                    view1.setCorner(1);
                    break;
                case 2:
                    view1.setCorner(0.75F);
                    break;
                case 3:
                    view1.setCorner(0.667F);
                    break;
                case 4:
                    view1.setCorner(0.5625F);
                    break;
            }
            view1.invalidate();
        }
    };

    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

            CropCommand command = new CropCommand(boundLine.boundLeft, boundLine.boundTop, boundLine.boundWidth, boundLine.boundHeight);
            bgImage = command.process(bgImage);

            dataStorage.setOriginalImageWidthAndHeight(boundLine.boundWidth, boundLine.boundHeight);

            dataStorage.setLastResultBitmap(bgImage);
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
        getMenuInflater().inflate(R.menu.menu_crop, menu);
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
