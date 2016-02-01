package com.example.rmk.mediaediting.graphic_process;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.rmk.mediaediting.GPUImageFilterTools;
import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.utils.LimitValueBetweenMaxMin;

import jp.co.cyberagent.android.gpuimage.GPUImageToneCurveFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

public class ToneCurveActivity extends Activity {

    private ImageView imageView;
    private TextView okButton;
    private TextView cancelButton;

    private LinearLayout drawWindow;
    private ImageView dropDownButton;
    private ImageView refreshToneButton;
    Paint mPaint;

    private Bitmap bgImage = null;
    private Bitmap bgImage_progress = null;
    private DataStorage dataStorage;
    private int editImageStatus_old = 100;
    private int editImageStatus = 0;


    DrawingView view1;
    RelativeLayout tonecurveRelativeLayout;
    LinearLayout tonecurveLinearLayout;

    int screenWidth, screenHeight;

    private GPUImageView mGPUImageView;
    private int effectSelectFlag = 0;
    private int drawW, drawH;
    private LinearLayout upscrollWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tone_curve);

        dataStorage = DataStorage.getInstance();
        initializeComponents();
        screenHeight = dataStorage.getScreenHeight();
        screenWidth = dataStorage.getScreenWidth();
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
        okButton = (TextView) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);

        tonecurveRelativeLayout = (RelativeLayout)findViewById(R.id.tonecurve_relativelayout);
        tonecurveLinearLayout = (LinearLayout)findViewById(R.id.tonecurve_linearlayout);

        dropDownButton = (ImageView)findViewById(R.id.tonepanel_down);
        refreshToneButton = (ImageView)findViewById(R.id.refresh_tone);

        upscrollWindow = (LinearLayout)findViewById(R.id.upScrollWindow);

        dropDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutAnimation();
//                layoutAnimationTest();
            }
        });
        refreshToneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawWindow.removeView(view1);
                view1 = new DrawingView(getBaseContext());
                drawWindow.addView(view1);
                filterFromJointPoint(toneCurvePoint);

            }
        });

        mGPUImageView = (GPUImageView) findViewById(R.id.gpuimage);

        getImageFromStorage();

        handleImage();

        drawWindow = (LinearLayout)findViewById(R.id.drawWindow);
        view1 =new DrawingView(this);
        drawWindow.addView(view1);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(getResources().getColor(R.color.gray_color));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(1);

        imageProcess();
    }

//    private void layoutAnimationTest() {
//        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = vi.inflate(R.layout.customview_test, null);
//
//// fill in any details dynamically here
//        TextView textView = (TextView) v.findViewById(R.id.textView);
//        textView.setText("your text adsfa sfjalksjd;flja;ljsdf;ja;lksdjf;klasdf");
//
//// insert into main view
//        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.drawWindow);
//        insertPoint.addView(v, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
//    }

    ImageView imageViewScrollButton;
    private void layoutAnimation(){
        final float scale = getBaseContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (screenHeight * scale * 2 + 0.5f);

        tonecurveRelativeLayout.getLayoutParams().height = pixels;
        tonecurveRelativeLayout.setPadding(0, 600, 0, 0);
        dropDownButton.setVisibility(View.GONE);
        tonecurveRelativeLayout.invalidate();

        imageViewScrollButton = new ImageView(this);
        upscrollWindow.addView(imageViewScrollButton);
        LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageViewScrollButton.setLayoutParams(vp);
        Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.btn_arrow_up);
        bp = Bitmap.createScaledBitmap(bp, bp.getWidth()/9, bp.getHeight()/9, false);
        imageViewScrollButton.setImageBitmap(bp);
        imageViewScrollButton.invalidate();

        imageViewScrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upScroll();
            }
        });

    }

    private void upScroll() {
        tonecurveRelativeLayout.setPadding(0, 0, 0, 0);
        dropDownButton.setVisibility(View.VISIBLE);
        tonecurveRelativeLayout.invalidate();

        upscrollWindow.removeView(imageViewScrollButton);
        upscrollWindow.invalidate();
    }

    private void getImageFromStorage(){
        bgImage = dataStorage.getBitmap();
    }

    private void handleImage(){
        mGPUImageView.setImage(bgImage);
        mGPUImageView.requestRender();

    }

    Bitmap bp = null;
    public void imageProcess() {
        if (editImageStatus_old != effectSelectFlag) {
            bp = dataStorage.getBitmap();
            editImageStatus_old = effectSelectFlag;
        }
        filterFromJointPoint();
    }

    public void filterFromJointPoint(PointF pointF[]){
        if(pointF == null){
            Log.d("GPUError", "Curve point not initialize");
            pointF = new PointF[]{new PointF(0, 0), new PointF(0.5F, 0.5F), new PointF(1.0F, 1.0F)};
        }
        mGPUImageView.setImage(bp);
        GPUImageToneCurveFilter filter = new GPUImageToneCurveFilter();
        filter.setRgbCompositeControlPoints(pointF);
        mGPUImageView.setFilter(filter);
        mGPUImageView.requestRender();
    }
    public void filterFromJointPoint(){

        PointF pointF[] = new PointF[]{new PointF(0, 0), new PointF(0.5F, 0.5F), new PointF(1.0F, 1.0F)};

        mGPUImageView.setImage(bp);
        GPUImageToneCurveFilter filter = new GPUImageToneCurveFilter();
        filter.setRgbCompositeControlPoints(pointF);
        mGPUImageView.setFilter(filter);
        mGPUImageView.requestRender();
    }


    PointF toneCurvePoint[] = new PointF[5];

    /// Control panel display
    CirclePathInformation boundCircle;
    LinePathInformation boundLine;
    BackgroundFrame backgroundLine;
    public class CirclePathInformation{
        private float radius;
        private float[] x = new float[5];
        private float[] y = new float[5];
        private int selectCorner = 5;
        private boolean isCorner = false;


        public  CirclePathInformation(){
        }

        public Path process(){
            Path result = new Path();
            for (int i = 0; i < 5; i++){
                result.addCircle(x[i], y[i], radius, Path.Direction.CW);
            }

            return result;
        }
        public Path reset(){
            Path result = new Path();
            return result;
        }

        public void setRadius(float radius){ this.radius = radius;}
        public float getRadius(){ return radius; }

        public void setXY(float[] x, float[] y){ this.x = x; this.y = y;}

        public float[] getX(){ return x; }
        public float[] getY() { return y; }
        public boolean isCornerDecide(float orderX, float orderY){
            boolean result = false;
            for (int i = 0; i < 5; i++){
                float dx = Math.abs(orderX - x[i]);
                float dy = Math.abs(orderY - y[i]);
                if (dx < radius * 5 && dy < radius * 5)
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

    public class BackgroundFrame{
        public BackgroundFrame(){}

        public Path process(float w, float h)
        {
            Path result = new Path();
            for (int i = 0; i < 5; i++){
                result.moveTo(i * w/4, 0);
                result.lineTo(i * w / 4, h);

                result.moveTo(0, i * h / 4);
                result.lineTo(w, i * h/4);
            }
            return result;
        }
    }

    public class LinePathInformation{

        private float[] x = new float[5];
        private float[] y = new float[5];

        public  LinePathInformation(){
        }
        public Path process()
        {
            Path result = new Path();
            result.moveTo(x[0], y[0]);
            for (int i = 1; i < 5; i++)
            {
                result.lineTo(x[i], y[i]);
            }
            return result;
        }

        public Path createSplinePath(){
            Path result = new Path();
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
        private Paint backgroundPaint;
        private Path backgroundPath;


        //////bound process

        float cornerRadius = 8f;
        float[] cornerX = new float[5]; float[] cornerY = new float[5];
        Boolean touchIsTrue = false;

        public DrawingView(Context c) {
            super(c);
            context=c;

            mPath = new Path();

            mBitmapPaint = new Paint(Paint.DEV_KERN_TEXT_FLAG);

            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(getResources().getColor(R.color.gray_color));
            circlePaint.setStyle(Paint.Style.FILL);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(cornerRadius);

            backgroundPaint = new Paint();
            backgroundPath = new Path();
            backgroundPaint.setAntiAlias(true);
            backgroundPaint.setDither(true);
            backgroundPaint.setColor(getResources().getColor(R.color.gray_color));
            backgroundPaint.setStyle(Paint.Style.STROKE);
            backgroundPaint.setStrokeJoin(Paint.Join.ROUND);
            backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
            backgroundPaint.setStrokeWidth(1);

            boundCircle = new CirclePathInformation();
            boundLine = new LinePathInformation();
            backgroundLine = new BackgroundFrame();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            Log.d("widthAndheigt", "width   " + w + "height" + h + "old" + oldw + "    " + oldh);

            width = w; height = h;
            boundCircle.setRadius(cornerRadius);
            for (int i = 0; i < 5; i++){
                cornerX[i] = i * w / 4; cornerY[i] = h - i * h / 4;
                toneCurvePoint[i] = new PointF((width - cornerX[i])/width, cornerY[i]/height);
            }

            boundCircle.setXY(cornerX, cornerY);
            boundLine.setXY(cornerX, cornerY);
            curveProduce(fivePointProduce());
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawPath(circlePath, circlePaint);
            canvas.drawPath(boundCircle.process(), circlePaint);
            canvas.drawPath(backgroundLine.process(width, height), backgroundPaint);
            canvas.drawPath(splineCurvePath, mPaint);
        }


        Path splineCurvePath;
        PointF[] splinePoint;
        private void curveProduce (PointF[] points)
        {
            splineCurvePath = new Path();

            // first line
            splineCurvePath.lineTo(-10.0F, cornerY[0]);
            splineCurvePath.moveTo(cornerX[0], cornerY[0]);
            // end

            splinePoint = new PointF[2000];
            for (int j = 0; j < splinePoint.length; j++){
                splinePoint[j] = new PointF(0, 0);
            }

            float i = points[0].x;
            int cnt = 0;
            float oldPointX = 0, oldPointY = 0;

            while ( i >= points[0].x && i< points[4].x)
            {
                float yi = 0;
                for (int j=0;j<5;j++)
                {
                    float lk = 1;
                    for (int k=0;k<5;k++)
                    {
                        if (k != j) {

                            lk *= (i - points[k].x) / (points[j].x - points[k].x);
                        }
                    }
                    yi += points[j].y * lk;
                }
                //Draw point ( i, yi) then show curve.
                float splineX = i; float splineY = yi;

                //limit spline curve along bounds
                splineX = LimitValueBetweenMaxMin.ensureRange(splineX, 0, width);
                splineY = LimitValueBetweenMaxMin.ensureRange(splineY, 0, height);
                // end limit

                splinePoint[cnt] = new PointF(splineX, splineY);
                cnt++;

                // draw

                splineCurvePath.quadTo(oldPointX, oldPointY, splineX, splineY);
                oldPointX = splineX;
                oldPointY = splineY;
                i += 1;
            }
            // end line
            splineCurvePath.moveTo(cornerX[4], cornerY[4]);
            splineCurvePath.lineTo(width + 10, cornerY[4]);

        }


        /*private Path makeSpline() {

            final Path path = new Path();
            path.moveTo(cornerX[0], cornerY[0]);

            for (int i = 0; i < 5; i++){
                float x2, y2;
                if (i == 0){
                    x2 = (cornerX[i + 1] - 100) / 2;
                    y2 = cornerY[i + 1];
                    path.quadTo(x2, y2, cornerX[i + 1], cornerY[i + 1]);
                }else if (i == 4){
                    x2 = ((cornerX[i] + 100) + cornerX[i - 1]) / 2;
                    y2 = (cornerY[i] + cornerY[i - 1]) / 2;
                    path.quadTo(x2, y2, (cornerX[i] + 100), cornerY[i]);
                }else {
                    x2 = (cornerX[i + 1] + cornerX[i - 1]) / 2;
                    y2 = (cornerY[i + 1] + cornerY[i - 1]) / 2;
                    path.quadTo(x2, y2, cornerX[i + 1], cornerY[i + 1]);
                }
            }
            return path;
        }*/
        private PointF[] fivePointProduce(){
            PointF[] resultPoint = new PointF[5];
            for (int i = 0; i < 5; i++)
            {
                resultPoint[i] = new PointF(cornerX[i], cornerY[i]);
            }
            return resultPoint;
        }



        private void setBoundAll(int cornerOrder, float newX, float newY)
        {
            cornerX[cornerOrder] = newX; cornerY[cornerOrder] = newY;

            cornerX[cornerOrder] = LimitValueBetweenMaxMin.ensureRange(cornerX[cornerOrder], 0, width);
            cornerY[cornerOrder] = LimitValueBetweenMaxMin.ensureRange(cornerY[cornerOrder], 0, height);

            if (cornerOrder == 4){
                cornerX[cornerOrder] = LimitValueBetweenMaxMin.ensureRange(cornerX[cornerOrder], cornerX[cornerOrder - 1] + TOUCH_TOLERANCE * 3, cornerX[cornerOrder]);
                Log.d("corner4", cornerOrder + "");
            }
            if (cornerOrder == 0){
                cornerX[cornerOrder] = LimitValueBetweenMaxMin.ensureRange(cornerX[cornerOrder], cornerX[cornerOrder], cornerX[cornerOrder + 1] - TOUCH_TOLERANCE * 3);
            }
            if (cornerOrder == 1 || cornerOrder == 2 || cornerOrder == 3)
            {
                Log.d("corner4", cornerOrder + "");
                cornerX[cornerOrder] = LimitValueBetweenMaxMin.ensureRange(cornerX[cornerOrder], cornerX[cornerOrder - 1] + TOUCH_TOLERANCE * 3, cornerX[cornerOrder + 1] - TOUCH_TOLERANCE * 3);
            }

            boundCircle.setXY(cornerX, cornerY);
            boundLine.setXY(cornerX, cornerY);
            toneCurvePoint[cornerOrder] = new PointF((width - newX)/width, newY/height);
        }


        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mX = x;
            mY = y;
            if (boundCircle.isCornerDecide(x, y))
            {
                circlePath.addCircle(mX, mY, boundCircle.getRadius() * 2, Path.Direction.CW);
                setBoundAll(boundCircle.getSelectCorner(), x, y);
                curveProduce(fivePointProduce());

            }
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, boundCircle.radius * 2, Path.Direction.CW);

                if (boundCircle.isCornerDecide(x, y)){
                    setBoundAll(boundCircle.getSelectCorner(), x, y);
                    //// Filter apply
                    curveProduce(fivePointProduce());
                    filterFromJointPoint(toneCurvePoint);
                }
            }
        }
        private void touch_up() {
            circlePath.reset();
            filterFromJointPoint(toneCurvePoint);
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



    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            Bitmap bitmap = null;
            try {
                bitmap = mGPUImageView.capture();
            } catch (InterruptedException e) {
                e.printStackTrace();
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
        getMenuInflater().inflate(R.menu.menu_tone_curve, menu);
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
