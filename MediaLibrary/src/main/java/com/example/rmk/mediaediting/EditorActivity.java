package com.example.rmk.mediaediting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rmk.mediaediting.GallerySet.ImageAdapter;
import com.example.rmk.mediaediting.graphic_process.Adjustment;
import com.example.rmk.mediaediting.graphic_process.Blur_FocusActivity;
import com.example.rmk.mediaediting.graphic_process.CropActivity;
import com.example.rmk.mediaediting.graphic_process.DataStorage;
import com.example.rmk.mediaediting.graphic_process.DrawActivity;
import com.example.rmk.mediaediting.graphic_process.EffectActivity;
import com.example.rmk.mediaediting.graphic_process.EmoticonsActivity;
import com.example.rmk.mediaediting.graphic_process.FiltersActivity;
import com.example.rmk.mediaediting.graphic_process.FrameActivity;
import com.example.rmk.mediaediting.graphic_process.PostPhotoActivity;
import com.example.rmk.mediaediting.graphic_process.ResizeActivity;
import com.example.rmk.mediaediting.graphic_process.RotateActivity;
import com.example.rmk.mediaediting.graphic_process.SplashActivity;
import com.example.rmk.mediaediting.graphic_process.StickerActivity;
import com.example.rmk.mediaediting.graphic_process.ToneCurveActivity;
import com.example.rmk.mediaediting.graphics.MenuTool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class EditorActivity extends Activity
{
	private ImageView imageView;
	private ImageView rButtonToPreviousScreenokButton;
	private ImageView rButtonToPostScreen;
	private DataStorage dataStorage;
	private Gallery gallery;

	private Bitmap bgImage = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor);
		initializeComponents();
	}
	private void initFont(){

		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Calibri-Bold.ttf");
		TextView topbar_title = (TextView)findViewById(R.id.topbar_title);
		topbar_title.setTypeface(font);
	}
	private void initializeComponents() {
		dataStorage = DataStorage.getInstance();

		initFont();

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int screensizeWidth = size.x;
		int screensizeHeight = size.y;
		dataStorage.setScreenWidthAndHeight(screensizeWidth, screensizeHeight);

		rButtonToPostScreen = (ImageView)findViewById(R.id.ok_button);
		rButtonToPostScreen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveProgressimageToSDCARD(bgImage);
				Intent intent = new Intent(EditorActivity.this, PostPhotoActivity.class);
				startActivityForResult(intent, 100);
			}
		});
		rButtonToPreviousScreenokButton = (ImageView)findViewById(R.id.cancel_button);
		rButtonToPreviousScreenokButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});




		imageView = (ImageView) findViewById(R.id.image_view);
		gallery = (Gallery) findViewById(R.id.editor_gallery);
		gallery.setAdapter(new ImageAdapter(this, MenuTool.Names, MenuTool.ImageIds));
		gallery.setSpacing(20);
		gallery.setOnItemClickListener(listener);
		gallery.setSelection(2);
//		Test bit
//		String ss = "http://159.203.64.134//faf//resource//article//0Py0ucVHHtGNhhsz.jpg";
//		openBitmap(ss);

		// when this work with module
		String data = getIntent().getExtras().getString("url");
		String invitaionId = getIntent().getExtras().getString("invitationId");
		dataStorage.setInvitationId(invitaionId);

		openBitmap(data);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,Intent data) {
		switch(resultCode){
			case 100:
				setResult(100);
				finish();            // to close this activity
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	protected void onResume() {
		super.onResume();
		dataStorage = DataStorage.getInstance();
		try{
            if (dataStorage.getisModified()){
                bgImage = dataStorage.getBitmap();
                imageView.setImageBitmap(bgImage);
                imageView.invalidate();
            }
		}catch (Exception e){
			e.printStackTrace();
		}
	}
//	private void openBitmap(String image){
//		Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.testimage);
//		bgImage = b;
//		dataStorage.setBitmap(bgImage);
//		imageView.setImageBitmap(b);
//	}

	private void openBitmap(String imageUri) {
		Log.i("Photo Editor", "Open Bitmap");
		Bitmap b;
		b = imageDownLoad(imageUri);

		dataStorage.setOriginalImageWidthAndHeight(b.getWidth(), b.getHeight());
		dataStorage.setOriginalBitmap(b);
		b = Bitmap.createScaledBitmap(b, dataStorage.getScreenWidth(), dataStorage.getScreenWidth(), false);

		bgImage = b;
		if (b != null) {
            Log.i("REC Photo Editor", "Opened Bitmap Size: " + b.getWidth()
                    + " " + b.getHeight());
			dataStorage.setBitmap(bgImage);
        }
		imageView.setImageBitmap(b);
	}

	public static Bitmap imageDownLoad(String link)
	{
		try {
			URL url = new URL(link);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;

		} catch (IOException e) {
			e.printStackTrace();
			Log.e("getBmpFromUrl error: ", e.getMessage().toString());
			return null;
		}
	}


	String SDCardRootOfprocessImage = Environment.getExternalStorageDirectory().getPath() + "/com.mobvcasting.mjpegffmpeg/" + "article.jpg";
	private String FolderName = "/com.mobvcasting.mjpegffmpeg/";
	private void saveProgressimageToSDCARD(Bitmap bitmap){
		File sdCardDirectory = Environment.getExternalStorageDirectory();
		File image = new File(sdCardDirectory, FolderName + "article.jpg");
		boolean success = false;

		// Encode the file as a PNG image.
		FileOutputStream outStream;
		try {

			outStream = new FileOutputStream(image);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        /* 100 to keep full quality of the image */

			outStream.flush();
			outStream.close();
			success = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (success) {
			dataStorage.setEditedImageFilePath(SDCardRootOfprocessImage);
//            Toast.makeText(getApplicationContext(), "Image saved with success",
//                    Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getApplicationContext(),
					"Error during image saving", Toast.LENGTH_LONG).show();
		}
	}




















	private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View v, int position,
								long id) {
//			Toast.makeText(getBaseContext(),"Processing: " + MenuTool.Names.get(position), Toast.LENGTH_SHORT).show();
			switch (MenuTool.Names.get(position)){
				case "Filter":
					Intent i = new Intent(getBaseContext(), FiltersActivity.class);
					startActivity(i);
					break;
				case "Adjustment":
					Intent i1 = new Intent(getBaseContext(), Adjustment.class);
					startActivity(i1);
					break;
				case "Effect":
					Intent i2 = new Intent(getBaseContext(), EffectActivity.class);
					startActivity(i2);
					break;
				case "Blur":
					Intent i3 = new Intent(getBaseContext(), Blur_FocusActivity.class);
					startActivity(i3);
					break;
				case "Draw":
					Intent i4 = new Intent(getBaseContext(), DrawActivity.class);
					startActivity(i4);
					break;
				case "Splash":
					Intent intent = new Intent(getBaseContext(), SplashActivity.class);
					startActivity(intent);
					break;
				case "Rotate":
					Intent intent1 = new Intent(getBaseContext(), RotateActivity.class);
					startActivity(intent1);
					break;
				case "Resize":
					Intent intent2 = new Intent(getBaseContext(), ResizeActivity.class);
					startActivity(intent2);
					break;
				case "Crop":
					Intent intent3 = new Intent(getBaseContext(), CropActivity.class);
					startActivity(intent3);
					break;
				case "Frame":
					Intent intent4 = new Intent(getBaseContext(), FrameActivity.class);
					startActivity(intent4);
					break;
				case "Emoticon":
					Intent intent5 = new Intent(getBaseContext(), EmoticonsActivity.class);
					startActivity(intent5);
					break;
				case "Sticker":
					Intent intent6 = new Intent(getBaseContext(), StickerActivity.class);
					startActivity(intent6);
					break;
				case "Tonecurve":
					Intent intent7 = new Intent(getBaseContext(), ToneCurveActivity.class);
					startActivity(intent7);
					break;
				case "Text":
//					Intent intent8 = new Intent(getBaseContext(), TextActivity.class);
//					startActivity(intent8);
					break;
			}
		}
	};
}
