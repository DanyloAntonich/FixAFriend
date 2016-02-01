package com.han.http;

import android.content.Context;
import android.content.Intent;

import com.han.utils.Constant;

public final class CommonUtilities {

	// give your server registration url here
//	public static final String SERVER_URL = "http://192.168.0.134/Studypost/MathApp_final/MathApp/API/registration.php";
	public static final String SERVER_URL = Constant.BASE_URL + "registration.php";
	// Google project id
	public static final String SENDER_ID = "433675293423";

	static final String TAG = "AndroidHive GCM";

	public static final String DISPLAY_MESSAGE_ACTION = "com.androidhive.pushnotifications.DISPLAY_MESSAGE";

	public static final String EXTRA_MESSAGE = "message";

	public static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
}
