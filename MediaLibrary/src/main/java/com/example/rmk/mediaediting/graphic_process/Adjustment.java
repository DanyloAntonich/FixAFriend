package com.example.rmk.mediaediting.graphic_process;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphics.commands.BrightnessContrastSaturationCommand;

public class Adjustment extends Activity {
    private ImageView imageView;
    private TextView okButton;
    private TextView cancelButton;
    private SeekBar slider_adjustment_brightness;
    private SeekBar slider_adjustment_color;
    private SeekBar slider_adjustment_contrast;

    private Bitmap bgImage;
    private Bitmap bgImage_progress = null;
    private DataStorage dataStorage;

    private int editImageStatus = 0;
    private int editImageStatus_old = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataStorage = DataStorage.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjustment);
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

        slider_adjustment_color = (SeekBar) findViewById(R.id.slider_adjustment_color);
        slider_adjustment_contrast = (SeekBar) findViewById(R.id.slider_adjustment_contrast);
        slider_adjustment_brightness = (SeekBar)findViewById(R.id.slider_adjustment_brightness);

        slider_adjustment_brightness.setOnSeekBarChangeListener(sliderChangeListener_brightness);
        slider_adjustment_color.setOnSeekBarChangeListener(sliderChangeListener_color);
        slider_adjustment_contrast.setOnSeekBarChangeListener(sliderChangeListener_contrast);

        okButton = (TextView) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);

        getImageFromStorage();
        imageView.setImageBitmap(bgImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Onresume", "ON____RESUME");
    }

    private void getImageFromStorage(){
        bgImage = dataStorage.getBitmap();
    }



    private void imageProcess(){
        Bitmap bp = null;
        int contrast = slider_adjustment_contrast.getProgress() - 100;
        int brightness = slider_adjustment_brightness.getProgress() - 100;
        int saturation = slider_adjustment_color.getProgress() - 100;

        if (editImageStatus_old != editImageStatus){
            imageViewCapture();
            editImageStatus_old = editImageStatus;
        }

        BrightnessContrastSaturationCommand command = new BrightnessContrastSaturationCommand(brightness, contrast, saturation);
        bp = command.process(bgImage);
        imageView.setImageBitmap(bp);
        imageView.invalidate();
    }
    private void imageViewCapture()
    {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache(false);
        if(imageView.getDrawingCache() != null) {
            if (bgImage_progress != null) {
                bgImage_progress.recycle();
                bgImage_progress = null;
            }
            bgImage_progress = Bitmap.createBitmap(imageView.getDrawingCache());
            imageView.setDrawingCacheEnabled(false);
        }
    }

    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            imageView.setDrawingCacheEnabled(true);
            imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            imageView.buildDrawingCache(true);
            Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());

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

    private SeekBar.OnSeekBarChangeListener sliderChangeListener_brightness = new SeekBar.OnSeekBarChangeListener() {
        public void onStopTrackingTouch(SeekBar seekBar) {
            // not used here
        }
        public void onStartTrackingTouch(SeekBar seekBar) {
            // not used here
        }
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (sliderMovedByUser(seekBar, fromUser)) {
                editImageStatus = 1;
//                imageProcess(progress - 100);
                imageProcess();
            }
        }
        private boolean sliderMovedByUser(SeekBar seekBar, boolean fromUser) {
            return fromUser && seekBar.getId() == slider_adjustment_brightness.getId();
        }
    };

    private SeekBar.OnSeekBarChangeListener sliderChangeListener_color = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (sliderMovedByUser(seekBar, fromUser)) {
                editImageStatus = 2;
                imageProcess();
            }
        }
        private boolean sliderMovedByUser(SeekBar seekBar, boolean fromUser) {
            return fromUser && seekBar.getId() == slider_adjustment_color.getId();
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private SeekBar.OnSeekBarChangeListener sliderChangeListener_contrast = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (sliderMovedByUser(seekBar, fromUser)){
                editImageStatus = 3;
                imageProcess();
            }
        }
        private boolean sliderMovedByUser(SeekBar seekBar, boolean fromUser) {
            return fromUser && seekBar.getId() == slider_adjustment_contrast.getId();
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_adjustment, menu);
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
//    private void setImage(){
//        byte[] bytes = getIntent().getByteArrayExtra("BMP");
//        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        bgImage = bmp;
//        imageView.setImageBitmap(bmp);
//    }