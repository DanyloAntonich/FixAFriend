package com.han.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.han.login.R;

import java.util.Timer;
import java.util.TimerTask;

public class ImageViewActivity extends AppCompatActivity {

    private  RelativeLayout relativeLayout, rlSuper;
    private TextView btnBack;
    private ImageView imageView;

    private ScaleGestureDetector scaleGestureDetector;
    private Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF startPoint = new PointF();
    PointF midPoint = new PointF();
    float oldDist = 1f;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;

    int mode = NONE;

    boolean topbarShow;
    Timer timer;
    TimerTask timerTask;

    ///device width
    DisplayMetrics displayMetrics;
    int width ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        initVariables();
        initUI();
        viewImage();
    }
    @Override
    protected void onPause() {
        super.onPause();
        ////
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
    private void initVariables(){
        topbarShow = false;
        displayMetrics = this.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
    }
    private void initUI(){

        relativeLayout = (RelativeLayout)findViewById(R.id.rl_imageview_banner);
        relativeLayout.setVisibility(View.INVISIBLE);

        rlSuper = (RelativeLayout)findViewById(R.id.rl_super_relativelayout);


        btnBack = (TextView)findViewById(R.id.btn_imageview_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageView = (ImageView)findViewById(R.id.iv_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topbarShow){
                    relativeLayout.setVisibility(View.INVISIBLE);
                    topbarShow = false;
                }else {
                    relativeLayout.setVisibility(View.VISIBLE);
                    topbarShow = true;
                }


            }
        });
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                System.out.println("matrix=" + savedMatrix.toString());
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        startPoint.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(midPoint, event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(topbarShow){
                            relativeLayout.setVisibility(View.INVISIBLE);
                            topbarShow = false;
                        }else {
                            relativeLayout.setVisibility(View.VISIBLE);
                            topbarShow = true;
                        }
//                        timer = new Timer();
//
//                        final Handler handler = new Handler();
//                        timer.schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                handler.post(new Runnable() {
//                                    public void run() {
//                                        relativeLayout.setVisibility(View.INVISIBLE);
//                                    }
//                                });
//                            }
//
//                        }, 5000);
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float scale = newDist / oldDist;
                                matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                            }
                        }
                        break;
                }
                view.setImageMatrix(matrix);
                return true;
            }

            @SuppressLint("FloatMath")
            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return FloatMath.sqrt(x * x + y * y);
            }

            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }
        });


    }

    private void viewImage(){

        Intent intent = getIntent();
        String strURL = intent.getStringExtra("url");
        Bitmap bitmap  = BitmapFactory.decodeFile(strURL);
        bitmap = Bitmap.createScaledBitmap(bitmap, width, width, false);
        imageView.setImageBitmap(bitmap);
//        if(!strURL.equals("")){
//            UrlRectangleImageViewHelper.setUrlDrawable(imageView, strURL, R.drawable.loading, new UrlImageViewCallback() {
//                @Override
//                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
//                    if (!loadedFromCache) {
//                        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
//                        scale.setDuration(300);
//                        scale.setInterpolator(new OvershootInterpolator());
//                        imageView.startAnimation(scale);
//                    }
//                }
//            });
//        }

    }

}
