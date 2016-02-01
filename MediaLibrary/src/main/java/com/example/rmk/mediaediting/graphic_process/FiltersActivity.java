package com.example.rmk.mediaediting.graphic_process;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rmk.mediaediting.GallerySet.FilterGalleryTool;
import com.example.rmk.mediaediting.R;
import com.example.rmk.mediaediting.graphics.commands.BrightnessCommand;
import com.example.rmk.mediaediting.graphics.commands.ColorFilterCommand;
import com.example.rmk.mediaediting.graphics.commands.EmptyCommand;
import com.example.rmk.mediaediting.graphics.commands.GrayscaleCommand;
import com.example.rmk.mediaediting.graphics.commands.InvertColorCommand;
import com.example.rmk.mediaediting.graphics.commands.SepiaCommand;


public class FiltersActivity extends Activity {

    private ImageView imageView;
    private TextView okButton;
    private TextView cancelButton;
    private Gallery gallery;
    private Bitmap[] proceededImage = new Bitmap[14];

    private Bitmap bgImage = null;
    private Bitmap bgImage_progress = null;
    private DataStorage dataStorage;
    private int editImageStatus_old = 100;
    private int editImageStatus = 0;



    private int effectSelectFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        dataStorage = DataStorage.getInstance();
        initializeComponents();
    }
    private void initializeComponents() {
        okButton = (TextView) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);

        imageView = (ImageView) findViewById(R.id.gpuimage);

        getImageFromStorage();
        imageView.setImageBitmap(bgImage);
//        handleImage();

        getGalleryImage();
        gallery = (Gallery) findViewById(R.id.filter_gallery);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setSpacing(20);
        gallery.setSelection(3);
        gallery.setOnItemClickListener(listener);
    }

    private void getGalleryImage(){
        proceededImage[0] = bgImage;

        BrightnessCommand command = new BrightnessCommand();
        command.setBrightness(-100);
        proceededImage[1] = command.process(bgImage);

        BrightnessCommand command1 = new BrightnessCommand();
        command1.setBrightness(-50);
        proceededImage[2] = command1.process(bgImage);

        BrightnessCommand command2 = new BrightnessCommand();
        command2.setBrightness(50);
        proceededImage[3] = command2.process(bgImage);

        ColorFilterCommand command3 = new ColorFilterCommand(1, 3, 1);
        proceededImage[4] = command3.process(bgImage);

        ColorFilterCommand command4 = new ColorFilterCommand(1, 1, 0.5);
        proceededImage[5] = command4.process(bgImage);

        SepiaCommand command5 = new SepiaCommand();
        proceededImage[6] = command5.process(bgImage);

        proceededImage[7] = bgImage;

        BrightnessCommand command6 = new BrightnessCommand(-25);
        proceededImage[8] = command6.process(bgImage);

        BrightnessCommand command7 = new BrightnessCommand(100);
        proceededImage[9] = command7.process(bgImage);

        GrayscaleCommand command8 = new GrayscaleCommand();
        proceededImage[10] = command8.process(bgImage);

        Bitmap tempBp = bgImage;
        tempBp = command8.process(bgImage);
        BrightnessCommand command9 = new BrightnessCommand(-20);
        proceededImage[11] = command9.process(tempBp);


        GrayscaleCommand command10 = new GrayscaleCommand();
        proceededImage[12] = command10.process(bgImage);

        InvertColorCommand command13 = new InvertColorCommand();
        proceededImage[13] = command13.process(bgImage);
    }

    private void getImageFromStorage(){
        bgImage = dataStorage.getBitmap();
    }

    Bitmap bp = null;
    private void imageProcess(){
        if (editImageStatus_old != effectSelectFlag){
            bp = dataStorage.getBitmap();
            editImageStatus_old = effectSelectFlag;
        }
        switch (effectSelectFlag){
            case 0:
                EmptyCommand command = new EmptyCommand();
                bp = command.process(bp);
                break;
            case 1://linear
                BrightnessCommand command1 = new BrightnessCommand();
                command1.setBrightness(-100);
                bp = command1.process(bp);
                break;
            case 2://vignette
                BrightnessCommand command2 = new BrightnessCommand();
                command2.setBrightness(-50);
                bp = command2.process(bp);
                break;
            case 3://instant
                BrightnessCommand command3 = new BrightnessCommand();
                command3.setBrightness(50);
                bp = command3.process(bp);
                break;
            case 4://process
                ColorFilterCommand command4 = new ColorFilterCommand(1, 3, 1);
                bp = command4.process(bp);
                break;
            case 5://transfer
                ColorFilterCommand command5 = new ColorFilterCommand(1, 1, 0.5);
                bp = command5.process(bp);
                break;
            case 6://sepia
                SepiaCommand command6 = new SepiaCommand();
                bp = command6.process(bp);
                break;
            case 7://chrome
                EmptyCommand command7 = new EmptyCommand();
                bp = command7.process(bp);
                break;
            case 8://fade
                BrightnessCommand command8 = new BrightnessCommand(-25);
                bp = command8.process(bp);
                break;
            case 9://curve
                BrightnessCommand command9 = new BrightnessCommand(100);
                bp = command9.process(bp);
                break;
            case 10://tonal
                GrayscaleCommand command10 = new GrayscaleCommand();
                bp = command10.process(bp);
                break;
            case 11://noir
                Bitmap tempBp = bp;
                GrayscaleCommand command11 = new GrayscaleCommand();
                tempBp = command11.process(bgImage);
                BrightnessCommand command12 = new BrightnessCommand(-20);
                bp = command12.process(tempBp);
                break;
            case 12:
                GrayscaleCommand command13 = new GrayscaleCommand();
                bp = command13.process(bp);
                break;
            case 13:
                InvertColorCommand command14 = new InvertColorCommand();
                bp = command14.process(bp);
                break;
        }
        imageView.setImageBitmap(bp);
        imageView.invalidate();
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
            }
            else
                holder=(com.example.rmk.mediaediting.GallerySet.ImageAdapter.ViewHolder)vi.getTag();

            holder.image.setImageBitmap(proceededImage[position]);
            holder.text.setText(FilterGalleryTool.Names.get(position));

            return vi;
        }
    }
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {
//            Toast.makeText(getBaseContext(), "filter: " + position, Toast.LENGTH_SHORT).show();

            effectSelectFlag = position;
            imageProcess();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filters, menu);
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
}

/// GPUImagefilter usage method
//GPUImageToneCurveFilter filter = new GPUImageToneCurveFilter();
//filter.setRgbCompositeControlPoints(pointF);
//        mGPUImageView.setFilter(filter);