package com.han.login;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class PolicyActivity extends AppCompatActivity {

    private WebView wb;
    private TextView btnBack;
    private TextView tvBanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        initUI();
        setFont();

    }
    private void initUI(){
        wb = (WebView) findViewById(R.id.webview);
//        wb.loadUrl("android.resource://" + getPackageName() + "/" + R.drawable.privacy);
        wb.loadUrl("file:///android_asset/privacy.html");
        btnBack = (TextView)findViewById(R.id.btn_back3);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvBanner = (TextView)findViewById(R.id.textView2);
    }
    private void setFont(){

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "Calibri.ttf");
        Typeface custom_font_bold = Typeface.createFromAsset(getAssets(),  "Calibri-Bold.ttf");
        tvBanner.setTypeface(custom_font_bold);

    }

}
