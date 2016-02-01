package com.han.capture;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.han.login.R;

public class CutPhotoActivity extends AppCompatActivity {

    private TextView tvRetake, tvUsePhoto;

    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut_photo);

        initVariables();
        initUI();
    }
    private void initUI(){
        tvRetake = (TextView)findViewById(R.id.tv_retake);
        tvUsePhoto = (TextView)findViewById(R.id.tv_use_photo);
    }
    private void initVariables(){
        url = "";
    }
}
