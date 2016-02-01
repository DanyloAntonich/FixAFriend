package com.example.rmk.mediaediting.graphic_process;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rmk.mediaediting.GallerySet.CropGalleryAdapter;
import com.example.rmk.mediaediting.GallerySet.ResizeGalleryTool;
import com.example.rmk.mediaediting.R;


public class ResizeActivity extends Activity {
    private ImageView imageView;
    private TextView okButton;
    private TextView cancelButton;

    private LinearLayout settingWindow;
    private TextView imageSizeAll;
    private EditText editText_imageSizeX;
    private EditText editText_imageSizeY;
    private ImageView chainOnOffImageButton;
    private ImageView widthOrHeightImageButton;

    private Gallery gallery;

    private Bitmap bgImage = null;
    private DataStorage dataStorage;

    private int effectSelectFlag = 0;
    private int new_imageSizeX = 0;
    private int new_imageSizeY = 0;
    private int old_imageSizeX = 0;
    private int old_imageSizeY = 0;

    private boolean startflag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resize);
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
        startflag = false;
        imageView = (ImageView) findViewById(R.id.image_view);
        gallery = (Gallery) findViewById(R.id.frame_gallery);
        gallery.setAdapter(new CropGalleryAdapter(this, ResizeGalleryTool.Names_Resize, ResizeGalleryTool.ImageIds_ResizeGallery));
        gallery.setSpacing(20);
        gallery.setSelection(2);
        gallery.setOnItemClickListener(listener);

        settingWindow = (LinearLayout)findViewById(R.id.setting_window);

        imageSizeAll = (TextView)findViewById(R.id.textView_original_imageSize);
        editText_imageSizeX = (EditText)findViewById(R.id.imageSizX);
        editText_imageSizeY = (EditText)findViewById(R.id.imageSizeY);

//        editTextSetting();

        chainOnOffImageButton = (ImageView)findViewById(R.id.sizeChainImage);
        chainOnOffImageButton.setImageResource(R.drawable.btn_chain_on);
        chainOnOffImageButton.setOnClickListener(imageChangeOnOffListener);

        widthOrHeightImageButton = (ImageView)findViewById(R.id.widthOrheight);
        widthOrHeightImageButton.setOnClickListener(widthOrHeightChangeListener);
        widthOrHeightImageButton.setImageResource(R.drawable.btn_width);
        widthOrHeightImageButton.bringToFront();

        okButton = (TextView) findViewById(R.id.ok_button);
        okButton.setOnClickListener(okButtonListener);
        cancelButton = (TextView) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(cancelButtonListener);

        getImageFromStorage();
        imageView.setImageBitmap(bgImage);

        displayOfSettingWindow();
    }

    private void editTextSetting() {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(editText_imageSizeX, InputMethodManager.SHOW_IMPLICIT);
//        imm.showSoftInput(editText_imageSizeY, InputMethodManager.SHOW_IMPLICIT);
        if (widthisTrue && startflag) {
            editText_imageSizeX.setFocusable(true);
            editText_imageSizeX.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText_imageSizeX, InputMethodManager.SHOW_IMPLICIT);
        }
        else {
            if (startflag) {
                editText_imageSizeY.setFocusable(true);
                editText_imageSizeY.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText_imageSizeY, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    private void getImageFromStorage(){
        bgImage = dataStorage.getBitmap();
        old_imageSizeX = dataStorage.getOriginalImageWidth();
        old_imageSizeY = dataStorage.getOriginalImageHeight();
    }

    private void displayOfSettingWindow(){
        imageSizeAll.setText(old_imageSizeX + " * " + old_imageSizeY);

        editText_imageSizeX.setText(old_imageSizeX + "", TextView.BufferType.EDITABLE);
        editText_imageSizeY.setText(old_imageSizeY + "", TextView.BufferType.EDITABLE);
        editText_imageSizeX.addTextChangedListener(new GenericTextWatcher(editText_imageSizeX));
        editText_imageSizeY.addTextChangedListener(new GenericTextWatcher(editText_imageSizeY));
    }

    private void sizeChangeAndDisplay(int sizeValue){
        if (chainOn){
            new_imageSizeX = sizeValue;
            new_imageSizeY = sizeValue;
        }else {
            if (widthisTrue) {
                new_imageSizeX = sizeValue;
                new_imageSizeY = Integer.parseInt(editText_imageSizeY.getText().toString());
            }
            else {
                new_imageSizeY = sizeValue;
                new_imageSizeX = Integer.parseInt(editText_imageSizeX.getText().toString());
            }
        }
        editText_imageSizeX.setText(new_imageSizeX + "");
        editText_imageSizeY.setText(new_imageSizeY + "");
    }




    private boolean innerAction = true;
    private class GenericTextWatcher implements TextWatcher{

        private View view;
        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            int i = view.getId();
            if (i == R.id.imageSizX) {
                if (chainOn && innerAction) {
                    innerAction = false;
                    editText_imageSizeY.setText(text);
                    new_imageSizeX = Integer.parseInt(text);
                    new_imageSizeY = Integer.parseInt(text);
                }
                new_imageSizeX = Integer.parseInt(text);


            } else if (i == R.id.imageSizeY) {
                if (chainOn && innerAction) {
                    innerAction = false;
                    editText_imageSizeX.setText(text);
                    new_imageSizeX = Integer.parseInt(text);
                    new_imageSizeY = Integer.parseInt(text);
                }
                new_imageSizeY = Integer.parseInt(text);

            }
//            innerAction = false;
        }
    }


    //// Gallery Adapter
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> parent, View v, int position,
                                long id) {

            effectSelectFlag = position;
            switch (position){
                case 0:
                    sizeChangeAndDisplay(240);
                    break;
                case 1:
                    sizeChangeAndDisplay(320);
                    break;
                case 2:
                    sizeChangeAndDisplay(480);
                    break;
                case 3:
                    sizeChangeAndDisplay(640);
                    break;
                case 4:
                    sizeChangeAndDisplay(800);
                    break;
                case 5:
                    sizeChangeAndDisplay(960);
                    break;
                case 6:
                    sizeChangeAndDisplay(1024);
                    break;
                case 7:
                    sizeChangeAndDisplay(2048);
                    break;
            }
            innerAction = true;
            startflag = true;
            editTextSetting();
        }
    };




    Boolean widthisTrue = true;
    private View.OnClickListener widthOrHeightChangeListener = new View.OnClickListener() {
        public void onClick(View v) {
            widthOrHeightImageButton.setImageResource(widthisTrue ? R.drawable.btn_height : R.drawable.btn_width);
            widthisTrue = widthisTrue ? false : true;
        }
    };

    Boolean chainOn = true;
    private View.OnClickListener imageChangeOnOffListener = new View.OnClickListener() {
        public void onClick(View v) {
            chainOnOffImageButton.setImageResource(chainOn ? R.drawable.btn_chain_off : R.drawable.btn_chain_on);
            chainOn = chainOn ? false : true;
        }
    };

    private View.OnClickListener okButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
//            CropCommand command = new CropCommand(0, 0, new_imageSizeX, new_imageSizeY);
//            bgImage = command.process(bgImage);
            bgImage = Bitmap.createScaledBitmap(bgImage, new_imageSizeX, new_imageSizeY, false);
            dataStorage.setOriginalImageWidthAndHeight(new_imageSizeX, new_imageSizeY);

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
        getMenuInflater().inflate(R.menu.menu_resize, menu);
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
