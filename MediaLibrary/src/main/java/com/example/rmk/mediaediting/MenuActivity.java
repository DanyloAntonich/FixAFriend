package com.example.rmk.mediaediting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import java.io.File;

public class MenuActivity extends Activity implements OnClickListener {
//	private final static int ACTIVITY_PICK_IMAGE = 0;
//	private final static int ACTIVITY_TAKE_PHOTO = 1;

	private ImageButton galleryButton;
	private ImageButton cameraButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		galleryButton = (ImageButton) findViewById(R.id.choose_from_sd_button);
		galleryButton.setOnClickListener(this);
		cameraButton = (ImageButton) findViewById(R.id.take_picture_button);
		cameraButton.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.choose_from_sd_button:
//			imageReadyStartAct();
//			break;
//		case R.id.take_picture_button:
//			videoReadyStartAct();
//			break;
//		default:
//			break;
		}
	}

	private void videoReadyStartAct() {
		Uri uri = Uri.fromFile(getTempFile(getApplicationContext()));
		startVideoEditorActivity(uri.toString());
	}

	private void startVideoEditorActivity(String url) {
		Intent i = new Intent(this, VideoEditorActivity.class);
		i.putExtra("VIDEO_URI", url);
		startActivity(i);
	}


	private File getTempFile(Context context) {
		String fileName = "temp_photo.jpg";
		File path = new File(Environment.getExternalStorageDirectory(),
				context.getPackageName());
		if (!path.exists()) {
			path.mkdir();
		}
		return new File(path, fileName);
	}















	private void imageReadyStartAct()
	{
		Uri uri = Uri.fromFile(getTempFile(getApplicationContext()));
		startEditorActivity(uri.toString());
	}

	private void startEditorActivity(String url) {
		Intent i = new Intent(this, EditorActivity.class);
		i.putExtra(getString(R.string.image_uri_flag), url);
		startActivity(i);
	}
	

}
