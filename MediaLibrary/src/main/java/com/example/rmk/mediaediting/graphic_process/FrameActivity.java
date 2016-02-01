package com.example.rmk.mediaediting.graphic_process;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rmk.mediaediting.GallerySet.FrameGalleryTool;
import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphics.commands.InsertImageCommand;

public class FrameActivity extends Activity {
    private ImageView imageView;
    private TextView okButton;
    private TextView cancelButton;

    private LinearLayout drawWindow;

    Paint mPaint;

    private Gallery gallery;

    private Bitmap bgImage = null;
    private DataStorage dataStorage;

    private int effectSelectFlag = 0;

    private Bitmap frameImage;
    private Boolean isframeSelected = true;
    private int editImageStatus_old = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);
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

        frameImage = BitmapFactory.decodeResource(getResources(), R.drawable.r003);

        gallery = (Gallery) findViewById(R.id.frame_gallery);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setOnItemClickListener(listener);
        gallery.setSelection(3);
        gallery.setSpacing(20);
    }

    private void getImageFromStorage(){
        bgImage = dataStorage.getBitmap();
    }
    private void imageProcess() {
        if (editImageStatus_old != effectSelectFlag) {
            bp = dataStorage.getBitmap();
            editImageStatus_old = effectSelectFlag;
        }
        frameImage = BitmapFactory.decodeResource(getResources(), FrameGalleryTool.ImageIds_FrameGallery[effectSelectFlag]);
        Bitmap scaleFrameImage = Bitmap.createScaledBitmap(frameImage, bgImage.getWidth(), bgImage.getHeight(), false);
        InsertImageCommand command = new InsertImageCommand(scaleFrameImage);
        bp = command.process(bp);

        imageView.setImageBitmap(bp);
        imageView.invalidate();
    }

    Bitmap bp = null;
    //// Gallery Adapter
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            effectSelectFlag = position;
            imageProcess();
            isframeSelected = true;
        }
    };

    public class ImageAdapter extends BaseAdapter {
        int galleryItemBackground;
        private Context context;
        private Integer[] mImageIds = FrameGalleryTool.ImageIds_FrameGallery;

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
            imageView.setLayoutParams(new Gallery.LayoutParams(80, 80));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setBackgroundColor(Color.BLACK);
            return imageView;
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




























    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_frame, menu);
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
