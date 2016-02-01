package com.example.rmk.mediaediting.graphic_process;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.rmk.mediaediting.GallerySet.TextGalleryTool;
import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphics.commands.AlphaCommand;
import com.example.rmk.mediaediting.graphics.commands.InsertImageCommand;
import com.example.rmk.mediaediting.graphics.commands.InvertColorCommand;

public class TextActivity extends Activity {
    private ImageView imageView;
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

    private int drawW, drawH;
    private RelativeLayout option_window;
    private Bitmap[] proceededImage;



    /// textsetting window
    DrawTextInformation textInformation;
    private LinearLayout parent_textSettingWindow;
    private EditText rEditTextReady;

    private LinearLayout parent_updrawwindow;

    ImageAdapter galleryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        dataStorage = DataStorage.getInstance();
        textInformation = DrawTextInformation.getInstance();
        initializeTextInformation();
        initializeComponents();
    }

    private void initializeTextInformation() {
        textInformation.setrTextViewWidthHeight(100, initialTextFont + 10);
        textInformation.setrText("Text");
        textInformation.setrTextAlpha(100);
        textInformation.setrTextSaturation(100);
        textInformation.setrTextColor(Color.BLUE);
        textInformation.setrTextThickness(10);
        textInformation.setrTextFontType("control1");

        textInformation.setrStrokeAlpha(100);
        textInformation.setrStrokeSaturation(200);
        textInformation.setrStrokeColor(Color.WHITE);
        textInformation.setrStrokeThickness(0);
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

        galleryImageProduce();
        /// have to fix
        gallery = (Gallery) findViewById(R.id.text_gallery);
        galleryAdapter = new ImageAdapter(this);
        gallery.setAdapter(galleryAdapter);
        gallery.setOnItemClickListener(listener);
        gallery.setSpacing(20);
        gallery.setSelection(3);


        okButton = (TextView) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);

        getImageFromStorage();
        imageView.setImageBitmap(bgImage);

        parent_updrawwindow = (LinearLayout)findViewById(R.id.parent_updrawwindow);
        parent_updrawwindow.setPadding(200, 0, 0, 0);

        /// display textsettingView part
        parent_textSettingWindow = (LinearLayout)findViewById(R.id.parent_textSettingWindow);

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


        cancelImage = Bitmap.createScaledBitmap(cancelImage, cancelImage.getWidth() / 4, cancelImage.getHeight() / 4, false);
        rotateImage = Bitmap.createScaledBitmap(rotateImage, rotateImage.getWidth() / 12 , rotateImage.getHeight() / 12, false);

        progressImage = bgImage;
    }

    private void galleryImageProduce() {
        proceededImage = new Bitmap[7];
        proceededImage[0] = BitmapFactory.decodeResource(getResources(), R.drawable.btn_add);
        proceededImage[1] = BitmapFactory.decodeResource(getResources(), R.drawable.clvideotexttoolwhiteicon);
        proceededImage[2] = BitmapFactory.decodeResource(getResources(), R.drawable.normal_frame_tool);
        proceededImage[2] = Bitmap.createScaledBitmap(proceededImage[2], proceededImage[2].getHeight(), proceededImage[2].getHeight(), false);
        proceededImage[3] = BitmapFactory.decodeResource(getResources(), R.drawable.btn_font);
        proceededImage[4] = BitmapFactory.decodeResource(getResources(), R.drawable.btn_align_left);
        proceededImage[5] = BitmapFactory.decodeResource(getResources(), R.drawable.btn_align_center);
        proceededImage[6] = BitmapFactory.decodeResource(getResources(), R.drawable.btn_align_right);
    }

    private void changeGalleryImage() {
        InvertColorCommand command = new InvertColorCommand();
        proceededImage[2] = command.process(proceededImage[2]);
        galleryAdapter.notifyDataSetChanged();
        gallery.setSelection(3);
    }



//    public Bitmap captureView(){
//        Bitmap resultImage = null;
//
//        if (isGallerySelect) preSave();
//
//        preSaveFlag = false;
//
//        isGallerySelect = true;
//
//        boundReset_GallerySelect = true;
//
//        return resultImage;
//    }
    public void preSave(){

//        InsertTextCommand command = new InsertTextCommand(emoticon.getText());
//        progressImage = command.process(progressImage, (int)boundCircle.left, (int)boundCircle.top, (int)emoticon.getTextSize());

        imageView.setImageBitmap(bgImage);
        imageView.invalidate();

    }







    Paint mTextPaint;
    public void setmTextPaint(){
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStyle(Paint.Style.FILL);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Calibri.ttf");
        mTextPaint.setTypeface(font);
        mTextPaint.setColor(textInformation.getrTextColor());
        mTextPaint.setTextSize(initialTextFont);
    }



    private int initialTextFont = 40;
    private boolean typingTextFlag = false;

    Bitmap rotateImage;
    Bitmap cancelImage;
    Emoticon emoticon;
    CirclePathInformation boundCircle;
    Bitmap textBitmap = null;

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

//            String aa = editText.getText().toString();
//            emoticon.setString(aa);
//
//            drawWindow.removeView(editText);
//            if (!preSaveFlag) emoticon.process((int) ((boundCircle.bottom - boundCircle.top) / 2.4F));
//            editText.invalidate();

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

//            mBitmapPaint = new Paint(Paint.DEV_KERN_TEXT_FLAG);

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



            setmTextPaint();
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

            width = textInformation.getrTextViewWidth(); height = textInformation.getrTextViewHeight();

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


            if (typingTextFlag){
                int deltaX = textInformation.getrTextViewWidth() - width;
                int deltaY = textInformation.getrTextViewHeight() - height;

                cornerX[0] -= deltaX / 2; cornerY[0] -= deltaY/2;
                cornerX[1] += deltaX / 2; cornerY[1] -= deltaY/2;
                cornerX[2] += deltaX / 2; cornerY[2] += deltaY/2;
                cornerX[3] -= deltaX / 2; cornerY[2] += deltaY/2;
                boundCircle.setXY(cornerX, cornerY);
                boundLine.setXY(cornerX, cornerY);
                typingTextFlag = false;
            }

            width = textInformation.getrTextViewWidth(); height = textInformation.getrTextViewHeight();

            if(boundReset_GallerySelect)
            {
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

//                setmTextPaint();
//                canvas.drawText(textInformation.getrText(), cornerX[3], cornerY[3], mTextPaint);

//                canvas.drawBitmap(bgImage, 200, 100, mPaint);
                if (calculationView.getWidth() > 0 && calculationView.getHeight() > 0){
                    textBitmap = loadBitmapFromView(calculationView);
                    canvas.drawBitmap(textBitmap, cornerX[0], cornerY[0], mPaint);


                    ///for saving
                    text_left = (int)cornerX[0];
                    text_top = (int)cornerY[0];
                }



//                canvas.drawBitmap(cDrawText(cornerX[3], cornerY[3]), 0, 0, mTextPaint);
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

        public void moveImageAndBound(float deltaX, float deltaY){

            for(int i = 0; i < 4; i++)
            {
                cornerX[i] += deltaX; cornerY[i] += deltaY;
            }

            boundCircle.setXY(cornerX, cornerY);
            boundLine.setXY(cornerX, cornerY);
        }

        public Bitmap cDrawText(float left, float top){
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            canvas.drawText(textInformation.getrText(), left, top, mTextPaint);

//            Typeface plain = Typeface.createFromAsset(assetManager, pathToFont);
//            Typeface bold = Typeface.create(plain, Typeface.DEFAULT_BOLD)
//            Paint paint = new Paint();
//            paint.setTypeface(bold);
//            canvas.drawText("Sample text in bold Helvetica",0,0,paint);
            return bitmap;
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
//                        drawWindow.removeView(editText);
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
//                            drawWindow.removeView(editText);
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

    public int text_left, text_top;

    public class Emoticon{
        public void process(){

        }
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }
















    //// Gallery Adapter
    private int rPreviousSelectedGalleryItemNumber = 100;
    private int rCurrentSelectedGalleryItemNumber;

    private ImageView rCancelImageButton;
    private View.OnClickListener removeWindowImageButtonListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            InsertImageCommand savecommand = new InsertImageCommand(bgImage);
            bgImage = savecommand.process(bgImage, textBitmap, text_left, text_top, mPaint);
            preSave();

            insertWindow.removeAllViews();
            parent_textSettingWindow.setBackgroundColor(Color.alpha(0));
            insertWindow.invalidate();
            parent_textSettingWindow.invalidate();
        }
    };
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {
            rCurrentSelectedGalleryItemNumber = position;
            AddTextSettingView(position);
        }
    };



    ViewGroup insertWindow;
    View v = null;

    ViewGroup calculationSizeWindow;
    View calculationView = null;
    TextView rTextView_forCalculationSize;

    LinearLayout rLayoutdisplayTextColor, rLayoutDisplayStrokeColor, rLayoutParentDisplaytextcolor, rLayoutParentDisplayStrokecolor;

    CircularColorPicker colorPicker;
    private SeekBar rSeekerColorBrightness;
    private SeekBar rSeekerColorAlpha;
    private SeekBar rSeekerStrokeSize;

    private void AddTextSettingView(int i){
        insertWindow = (ViewGroup) findViewById(R.id.textSettingLayout);
        LayoutInflater v1 = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        calculationSizeWindow = (ViewGroup)findViewById(R.id.up_drawWindow);
        LayoutInflater inflaterOfView = (LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        if (rCurrentSelectedGalleryItemNumber != rPreviousSelectedGalleryItemNumber) {
            if (i == 0){
                isGallerySelect = true;
                boundReset_GallerySelect = true;

                drawWindow = (RelativeLayout)findViewById(R.id.drawWindow);
                view1 =new DrawingView(this);
                drawWindow.addView(view1);
            }


            if (i == 0){
                if (rPreviousSelectedGalleryItemNumber == 100){
                    calculationView = inflaterOfView.inflate(R.layout.onlylabel_forcalculationtextsize, null);
                    calculationSizeWindow.addView(calculationView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    rTextView_forCalculationSize = (TextView)findViewById(R.id.rTextView_forCalculationSize);
                    rTextView_forCalculationSize.setTextSize(initialTextFont);
                    calculationView.setVisibility(View.INVISIBLE);
                }else {

                    insertWindow.removeView(v);
                }
            }else {
                insertWindow.removeView(v);
            }


            if (i == 0) {
                v = v1.inflate(R.layout.textsetting_view, null);
            } else if (i == 1) {
                v = v1.inflate(R.layout.textsetting_view, null);
            } else if (i == 2) {
                v = v1.inflate(R.layout.textedit_color, null);
            } else if (i == 3) {
                v = v1.inflate(R.layout.textedit_font, null);
            }


            if (i == 0) {
//            v.animate().translationX()
                parent_textSettingWindow.setBackgroundColor(Color.BLACK);
                parent_textSettingWindow.invalidate();
                insertWindow.addView(v);

                rEditTextReady = (EditText)findViewById(R.id.textView_ready);
                rEditTextReady.requestFocus();
                rEditTextReady.addTextChangedListener(new GenericTextWatcher(rEditTextReady));

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(rEditTextReady, InputMethodManager.SHOW_IMPLICIT);

                /// calculation textsize

            }
            if (i == 1) {
                insertWindow.addView(v);
            }
            if (i == 2) {
                insertWindow.addView(v);

                RelativeLayout drawWindow_colorSetting = (RelativeLayout)findViewById(R.id.drawWindow_colorSetting);
                colorPicker = new CircularColorPicker(getBaseContext());
                drawWindow_colorSetting.addView(colorPicker);

                rLayoutdisplayTextColor = (LinearLayout)findViewById(R.id.display_textColor);
                rLayoutDisplayStrokeColor = (LinearLayout)findViewById(R.id.display_strokeColor);
                rLayoutParentDisplaytextcolor = (LinearLayout)findViewById(R.id.parent_displaytextcolor);
                rLayoutParentDisplayStrokecolor = (LinearLayout)findViewById(R.id.parenet_displaystrokecolor);
                rSeekerColorBrightness = (SeekBar)findViewById(R.id.seekBar_text_brightness);
                rSeekerColorAlpha = (SeekBar)findViewById(R.id.seekBar_text_opactity);
                rSeekerStrokeSize = (SeekBar)findViewById(R.id.seekBar_stroke_size);

                rSeekerStrokeSize.setOnSeekBarChangeListener(rSeekerColorStrokeSizeListener);

                rLayoutdisplayTextColor.setOnClickListener(rLayoutDisplayTextColorListener);
                rLayoutDisplayStrokeColor.setOnClickListener(rLayoutDisplayStrokeColorListener);


            }
            if (i == 3) {
                insertWindow.addView(v);
            }
            rCancelImageButton =  (ImageView)findViewById(R.id.cancel_imageView);
            rCancelImageButton.setOnClickListener(removeWindowImageButtonListner);
        }
    }

    private void changeCanvasText(String string) {

        rTextView_forCalculationSize.setText(string);


        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Calibri.ttf");
        rTextView_forCalculationSize.setTypeface(font);

        int widthOfLabel = calculationView.getWidth();
        int heightOfLabel = calculationView.getHeight();

        textInformation.setrTextViewWidthHeight(widthOfLabel, heightOfLabel);
        textInformation.setrText(string);

        typingTextFlag = true;
        view1.invalidate();
//        Toast.makeText(getBaseContext(), "view size" + "   " + widthOfLabel + "     " + heightOfLabel, Toast.LENGTH_SHORT).show();
        Log.d("view size", widthOfLabel + "     " + heightOfLabel);
        Log.d("canvastext", string);
    }

    private boolean strokeOrNot = false;
    public void cTextColorChange(){
        int color = colorPicker.getColor();
        if (!strokeOrNot) {
            textInformation.setrTextColor(colorPicker.getColor());

            MakeDrawable drawable = new MakeDrawable(color, color, color, 1, color, 0);
            rLayoutdisplayTextColor.setBackground(drawable);
        }
        else {
            textInformation.setrStrokeColor(colorPicker.getColor());

            MakeDrawable drawable = new MakeDrawable(color, color, color, 1, color, 0);
            rLayoutDisplayStrokeColor.setBackground(drawable);
        }

        rTextView_forCalculationSize.setTextColor(color);
        rTextView_forCalculationSize.invalidate();
        view1.invalidate();
    }




    private View.OnClickListener rLayoutDisplayStrokeColorListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            strokeOrNot = true;
            rLayoutParentDisplaytextcolor.setBackgroundResource(R.color.black_color);
            rLayoutParentDisplayStrokecolor.setBackgroundResource(R.color.gray_color);
        }
    };
    private View.OnClickListener rLayoutDisplayTextColorListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            strokeOrNot = false;
            rLayoutParentDisplaytextcolor.setBackgroundResource(R.color.gray_color);
            rLayoutParentDisplayStrokecolor.setBackgroundResource(R.color.black_color);
        }
    };


//    private SeekBar.OnSeekBarChangeListener rSeekerColorAlphaListener = new SeekBar.OnSeekBarChangeListener() {
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            if (sliderMovedByUser(seekBar, fromUser)) {
//                if (!strokeOrNot)
//                    textInformation.setrTextAlpha(0);
//                else
//                    textInformation.setrStrokeAlpha(progress);
//                view1.invalidate();
//                colorPicker.invalidate();
//            }
//        }
//        private boolean sliderMovedByUser(SeekBar seekBar, boolean fromUser) {
//            return fromUser && seekBar.getId() == rSeekerColorAlpha.getId();
//        }
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//
//        }
//    };
    private SeekBar.OnSeekBarChangeListener rSeekerColorStrokeSizeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (sliderMovedByUser(seekBar, fromUser)) {
                if (!strokeOrNot)
                    textInformation.setrStrokeThickness(progress);
                else
                    textInformation.setrStrokeThickness(progress);
//                view1.invalidate();
            }
        }
        private boolean sliderMovedByUser(SeekBar seekBar, boolean fromUser) {
            return fromUser && seekBar.getId() == rSeekerStrokeSize.getId();
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
//    private SeekBar.OnSeekBarChangeListener rSeekerColorBrightnessListener = new SeekBar.OnSeekBarChangeListener() {
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            if (sliderMovedByUser(seekBar, fromUser)) {
//                if (!strokeOrNot)
//                    textInformation.setrTextSaturation(progress);
//                else
//                    textInformation.setrTextSaturation(progress);
//                view1.invalidate();
//                colorPicker.invalidate();
//            }
//        }
//        private boolean sliderMovedByUser(SeekBar seekBar, boolean fromUser) {
//            return fromUser && seekBar.getId() == rSeekerColorBrightness.getId();
//        }
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//
//        }
//    };



    public class CircularColorPicker extends View {

        /**
         * Customizable display parameters (in percents)
         */
        private final int paramOuterPadding = 2; // outer padding of the whole color picker view
        private final int paramInnerPadding = 5; // distance between value slider wheel and inner color wheel
        private final int paramValueSliderWidth = 10; // width of the value slider
        private final int paramArrowPointerSize = 4; // size of the arrow pointer; set to 0 to hide the pointer

        private final int paramColorCount = 5;
        private final float paramHueSpreadAngle = 30f; // in degrees

        private Paint colorWheelPaint;
        private Paint valueSliderPaint;

        private Paint colorViewPaint;

        private Paint colorPointerPaint;
        private RectF colorPointerCoords;

        private Paint valuePointerPaint;
        private Paint valuePointerArrowPaint;

        private RectF outerWheelRect;
        private RectF innerWheelRect;

        private Path colorViewPath;
        private Path valueSliderPath;
        private Path arrowPointerPath;

        private Bitmap colorWheelBitmap;

        private int valueSliderWidth;
        private int innerPadding;
        private int outerPadding;

        private int arrowPointerSize;
        private int outerWheelRadius;
        private int innerWheelRadius;
        private int colorWheelRadius;

        private Matrix gradientRotationMatrix;

        /** Currently selected color */
        private float[] colorHSV = new float[] { 0f, 0f, 1f };
        private float[] adjacentHue = new float[paramColorCount];

        public CircularColorPicker(Context context) {
            super(context);
            init();
        }

        public void chageColorPointerPaint(int innerColor){
            colorPointerPaint.setColor(innerColor);
        }

        private void init() {

            colorPointerPaint = new Paint();
            colorPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            colorPointerPaint.setStrokeWidth(2f);
            colorPointerPaint.setARGB(128, 255, 255, 255);

            valuePointerPaint = new Paint();
            valuePointerPaint.setStyle(Paint.Style.STROKE);
            valuePointerPaint.setStrokeWidth(2f);

            valuePointerArrowPaint = new Paint();

            colorWheelPaint = new Paint();
            colorWheelPaint.setAntiAlias(true);
            colorWheelPaint.setDither(true);

            valueSliderPaint = new Paint();
            valueSliderPaint.setAntiAlias(true);
            valueSliderPaint.setDither(true);

            colorViewPaint = new Paint();
            colorViewPaint.setAntiAlias(true);

            colorViewPath = new Path();
            valueSliderPath = new Path();
            arrowPointerPath = new Path();

            outerWheelRect = new RectF();
            innerWheelRect = new RectF();

            colorPointerCoords = new RectF();

        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            int size = Math.min(widthSize, heightSize);
            setMeasuredDimension(size, size);
        }

        @SuppressLint("DrawAllocation")
        @Override
        protected void onDraw(Canvas canvas) {

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            // drawing color wheel
//            changeBitmap(textInformation.getrTextAlpha(), textInformation.getrTextSaturation());
            canvas.drawBitmap(colorWheelBitmap, centerX - colorWheelRadius, centerY - colorWheelRadius, null);

            // drawing color wheel pointer

//            for (int i = 0; i < paramColorCount; i++) {
            int i = 2;
            drawColorWheelPointer(canvas, (float) Math.toRadians(adjacentHue[i]));
//            }
        }
        private void drawColorWheelPointer(Canvas canvas, float hueAngle) {

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            int colorPointX = (int) (-Math.cos(hueAngle) * colorHSV[1] * colorWheelRadius) + centerX;
            int colorPointY = (int) (-Math.sin(hueAngle) * colorHSV[1] * colorWheelRadius) + centerY;

            float pointerRadius = 0.3f * colorWheelRadius;
            int pointerX = (int) (colorPointX - pointerRadius / 2);
            int pointerY = (int) (colorPointY - pointerRadius / 2);

            colorPointerCoords.set(pointerX, pointerY, pointerX + pointerRadius, pointerY + pointerRadius);

            colorPointerPaint.setColor(this.getColor());
            canvas.drawOval(colorPointerCoords, colorPointerPaint);


        }

        private void drawPointerArrow(Canvas canvas) {

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            double tipAngle = (colorHSV[2] - 0.5f) * Math.PI;
            double leftAngle = tipAngle + Math.PI / 96;
            double rightAngle = tipAngle - Math.PI / 96;

            double tipAngleX = Math.cos(tipAngle) * outerWheelRadius;
            double tipAngleY = Math.sin(tipAngle) * outerWheelRadius;
            double leftAngleX = Math.cos(leftAngle) * (outerWheelRadius + arrowPointerSize);
            double leftAngleY = Math.sin(leftAngle) * (outerWheelRadius + arrowPointerSize);
            double rightAngleX = Math.cos(rightAngle) * (outerWheelRadius + arrowPointerSize);
            double rightAngleY = Math.sin(rightAngle) * (outerWheelRadius + arrowPointerSize);

            arrowPointerPath.reset();
            arrowPointerPath.moveTo((float) tipAngleX + centerX, (float) tipAngleY + centerY);
            arrowPointerPath.lineTo((float) leftAngleX + centerX, (float) leftAngleY + centerY);
            arrowPointerPath.lineTo((float) rightAngleX + centerX, (float) rightAngleY + centerY);
            arrowPointerPath.lineTo((float) tipAngleX + centerX, (float) tipAngleY + centerY);

            valuePointerArrowPaint.setColor(Color.HSVToColor(colorHSV));
            valuePointerArrowPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(arrowPointerPath, valuePointerArrowPaint);

            valuePointerArrowPaint.setStyle(Paint.Style.STROKE);
            valuePointerArrowPaint.setStrokeJoin(Paint.Join.ROUND);
            valuePointerArrowPaint.setColor(Color.BLACK);
            canvas.drawPath(arrowPointerPath, valuePointerArrowPaint);

        }

        @Override
        protected void onSizeChanged(int width, int height, int oldw, int oldh) {

            int centerX = width / 2;
            int centerY = height / 2;

//            innerPadding = (int) (paramInnerPadding * width / 100);
            innerPadding = 0;
            outerPadding = 0;
//            outerPadding = (int) (paramOuterPadding * width / 100);
            arrowPointerSize = (int) (paramArrowPointerSize * width / 100);
            valueSliderWidth = (int) (paramValueSliderWidth * width / 100);

            outerWheelRadius = width / 2 - outerPadding - arrowPointerSize;
            innerWheelRadius = outerWheelRadius - valueSliderWidth;
            colorWheelRadius = innerWheelRadius - innerPadding;

            outerWheelRect.set(centerX - outerWheelRadius, centerY - outerWheelRadius, centerX + outerWheelRadius, centerY + outerWheelRadius);
            innerWheelRect.set(centerX - innerWheelRadius, centerY - innerWheelRadius, centerX + innerWheelRadius, centerY + innerWheelRadius);

            colorWheelBitmap = createColorWheelBitmap(colorWheelRadius * 2, colorWheelRadius * 2);

            gradientRotationMatrix = new Matrix();
            gradientRotationMatrix.preRotate(270, width / 2, height / 2);

            valueSliderPath.arcTo(outerWheelRect, 270, 180);
            valueSliderPath.arcTo(innerWheelRect, 90, -180);

        }

        private Bitmap createColorWheelBitmap(int width, int height) {

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            int colorCount = 12;
            int colorAngleStep = 360 / 12;
            int colors[] = new int[colorCount + 1];
            float hsv[] = new float[] { 0f, 1f, 1f };
            for (int i = 0; i < colors.length; i++) {
                hsv[0] = (i * colorAngleStep + 180) % 360;
                colors[i] = Color.HSVToColor(hsv);
            }
            colors[colorCount] = colors[0];

//            Paint colorWheelInnerPaint = new Paint();
//            colorWheelInnerPaint.setColor(Color.BLACK);
//            colorWheelInnerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));



            SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2, colors, null);
            RadialGradient radialGradient = new RadialGradient(width / 2, height / 2, colorWheelRadius, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
//            ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.SRC_OVER);
            ComposeShader composeShader = new ComposeShader(sweepGradient, radialGradient, PorterDuff.Mode.DST_OVER);

            colorWheelPaint.setShader(composeShader);

            Canvas canvas = new Canvas(bitmap);
//            canvas.drawCircle(width / 2, height / 2, colorWheelRadius - 10, colorWheelInnerPaint);
            canvas.drawCircle(width / 2, height / 2, colorWheelRadius, colorWheelPaint);
            return bitmap;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:

                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    int cx = x - getWidth() / 2;
                    int cy = y - getHeight() / 2;
                    double d = Math.sqrt(cx * cx + cy * cy);

                    if (d <= colorWheelRadius) {

                        colorHSV[0] = (float) (Math.toDegrees(Math.atan2(cy, cx)) + 180f);
                        colorHSV[1] = Math.max(0f, Math.min(1f, (float) (d / colorWheelRadius)));

                        updateAdjacentHue();
                        invalidate();

                        cTextColorChange();

                    } else if (x >= getWidth() / 2 && d >= innerWheelRadius) {

                        colorHSV[2] = (float) Math.max(0, Math.min(1, Math.atan2(cy, cx) / Math.PI + 0.5f));

                        updateAdjacentHue();
                        invalidate();
                        cTextColorChange();

                    }

                    return true;
            }
            return super.onTouchEvent(event);
        }

        private void updateAdjacentHue() {

            for (int i = 0; i < paramColorCount; i++) {
                adjacentHue[i] = (colorHSV[0] - paramHueSpreadAngle * (paramColorCount / 2 - i)) % 360.0f;
                adjacentHue[i] = (adjacentHue[i] < 0) ? adjacentHue[i] + 360f : adjacentHue[i];
            }
            adjacentHue[paramColorCount / 2] = colorHSV[0];

        }

        public void setColor(int color) {
            Color.colorToHSV(color, colorHSV);
            updateAdjacentHue();
        }
        public void changeBitmap(int alpha, int brightness){
            AlphaCommand command = new AlphaCommand();
            colorWheelBitmap =  command.AlphaCommand(colorWheelBitmap, alpha);
//            BrightnessCommand command1 = new BrightnessCommand(brightness);
//            colorWheelBitmap = command1.process(colorWheelBitmap);
        }
        public int getColor() {
            return Color.HSVToColor(colorHSV);
        }

        public int[] getColors() {
            int[] colors = new int[paramColorCount];
            float[] hsv = new float[3];
            for (int i = 0; i < paramColorCount; i++) {
                hsv[0] = adjacentHue[i];
                hsv[1] = colorHSV[1];
                hsv[2] = colorHSV[2];
                colors[i] = Color.HSVToColor(hsv);
            }
            return colors;
        }

        @Override
        protected Parcelable onSaveInstanceState() {
            Bundle state = new Bundle();
            state.putFloatArray("color", colorHSV);
            state.putParcelable("super", super.onSaveInstanceState());
            return state;
        }

        @Override
        protected void onRestoreInstanceState(Parcelable state) {
            if (state instanceof Bundle) {
                Bundle bundle = (Bundle) state;
                colorHSV = bundle.getFloatArray("color");
                updateAdjacentHue();
                super.onRestoreInstanceState(bundle.getParcelable("super"));
            } else {
                super.onRestoreInstanceState(state);
            }
        }

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
        parentWindow.setPadding((drawW - bgImage.getWidth()) / 2, (drawH - bgImage.getHeight()) / 2, (drawW - bgImage.getWidth()) / 2, (drawH - bgImage.getHeight()) / 2);
        parentWindow.invalidate();
    }
    private void getImageFromStorage(){
        bgImage = dataStorage.getBitmap();
    }
    private class GenericTextWatcher implements TextWatcher {

        private View view;
        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            int i = view.getId();
            if (i == R.id.textView_ready) {
                changeCanvasText(rEditTextReady.getText().toString());
            }
        }
    }

    public class ImageAdapter extends BaseAdapter {
        private Activity activity;
        private LayoutInflater inflater = null;

        public ImageAdapter(Activity a) {
            activity = a;
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return proceededImage.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View vi=convertView;
            com.example.rmk.mediaediting.GallerySet.ImageAdapter.ViewHolder holder;
            if(convertView==null){
                vi = inflater.inflate(R.layout.gallerycell, null);
                holder=new com.example.rmk.mediaediting.GallerySet.ImageAdapter.ViewHolder();
                holder.text=(TextView)vi.findViewById(R.id.gallery_text);
                holder.image=(ImageView)vi.findViewById(R.id.gallery_image);
                vi.setTag(holder);
            } else
                holder=(com.example.rmk.mediaediting.GallerySet.ImageAdapter.ViewHolder)vi.getTag();

            holder.image.setImageBitmap(proceededImage[position]);
            holder.text.setText(TextGalleryTool.Names[position]);

            return vi;
        }
    }

    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
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
        getMenuInflater().inflate(R.menu.menu_text, menu);
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
