package com.example.rmk.mediaediting.graphic_process;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.rmk.mediaediting.GallerySet.RotateGalleryTool;
import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphics.ImageProcessor;
import com.example.rmk.mediaediting.graphics.commands.MirrorCommand;
import com.example.rmk.mediaediting.graphics.commands.RotateCommand;


public class RotateActivity extends Activity {
    private ImageView imageView;
    private TextView okButton;
    private TextView cancelButton;
    private ProgressBar progressBar;
    private ImageProcessor imageProcessor;

    private Gallery gallery;
    private SeekBar seekBar_effect_intensity;

    private Bitmap bgImage = null;
    private Bitmap bgImage_progress = null;
    private DataStorage dataStorage;
    private int editImageStatus_old = 10;
    private int editImageStatus = 0;
///
    private int effectSelectFlag = 0;


    /// Parent view size
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate);
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
        gallery = (Gallery) findViewById(R.id.effect_gallery);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemClickListener(listener);
        gallery.setSelection(2);
        gallery.setSpacing(20);
        seekBar_effect_intensity = (SeekBar) findViewById(R.id.seeker_effect_intensity);
        seekBar_effect_intensity.setOnSeekBarChangeListener(sliderChangeListener_intensity);

        okButton = (TextView) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);

        getImageFromStorage();
        imageView.setImageBitmap(bgImage);
    }


    private void getImageFromStorage(){
        bgImage = dataStorage.getBitmap();
    }

    Bitmap bp = null;
    private void imageProcess(){
        float value = (float) seekBar_effect_intensity.getProgress();
        value = value * 1.8F;


        if (effectSelectFlag == 0 || effectSelectFlag == 100){
            bp = dataStorage.getBitmap();
        }else{
            bp = bp;
        }

        switch (effectSelectFlag) {
            case 0:
                int k =(int)(value/90);
                RotateCommand command = new RotateCommand((int)((k + 1) * 90));
                bp = command.process(bp);
                k = (int)(((k + 1) * 90)/1.8F);
                seekBar_effect_intensity.setProgress(k % 200);
                break;
            case 1:
                MirrorCommand command1 = new MirrorCommand(2);
                bp = command1.process(bp);
                break;
            case 2:
                MirrorCommand command2 = new MirrorCommand(1);
                bp = command2.process(bp);
                break;
            case 100:
                RotateCommand command3 = new RotateCommand((int)value);
                bp = command3.process(bp);
                break;
        }

        imageView.setImageBitmap(bp);
        imageView.invalidate();
    }

    Boolean rotate90 = false;
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            effectSelectFlag = position;
            switch (position){
                case 0:
                    rotate90 = true;
                    break;
                case 1:
                    break;
                case 2:
                    break;
                }
            imageProcess();
        }
    };

    public class ImageAdapter extends BaseAdapter {
        int galleryItemBackground;
        private Context context;
        private Integer[] mImageIds = RotateGalleryTool.ImageIds_RotateGallery;

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
            imageView.setBackgroundColor(Color.BLACK);
            return imageView;
        }
    }


















    private SeekBar.OnSeekBarChangeListener sliderChangeListener_intensity = new SeekBar.OnSeekBarChangeListener() {
        public void onStopTrackingTouch(SeekBar seekBar) {
            // not used here
        }
        public void onStartTrackingTouch(SeekBar seekBar) {
            // not used here
        }
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (sliderMovedByUser(seekBar, fromUser)) {
                effectSelectFlag = 100;
                imageProcess();
            }
        }
        private boolean sliderMovedByUser(SeekBar seekBar, boolean fromUser) {
            return fromUser && seekBar.getId() == seekBar_effect_intensity.getId();
        }
    };

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rotate, menu);
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
