package com.han.gcm;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.han.login.R;
import com.han.main.FeedViewActivity;
import com.han.utils.Constant;
import com.han.utils.GlobalVariable;
import com.han.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	
	public static final String MESSAGE_RECEIVED = "GotMessage";
	
	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	SharedPreferences prefs;
	GlobalVariable globalVariable = new GlobalVariable();
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);
		
//		prefs = getSharedPreferences(Constant.PREF_USER_DETAILS, Context.MODE_PRIVATE);

		prefs = Utilities.getSharedPreferences(this);
		//{message={"aps":{"receiver":"qingqing@gmail.com","alert":"Lee Test : Hello","sender":"brightsunshine224@hotmail.com","photo_url":"http:\/\/imathhub.com\/Services\/MathApp\/Images\/User\/Profile\/image_55eea85b5c5e220150908052027.20150908_051956","message":"Hello","type":"message","chat_id":"336","username":"Lee Test"}}, android.support.content.wakelockid=2, collapse_key=do_not_collapse, from=602965766021}]
		if (!extras.isEmpty()) {
			try {
				parseData(extras);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void parseData(Bundle extra) throws JSONException {


		String str = extra.getString("aps");
		JSONObject jsonObject = new JSONObject(str);

			String type = jsonObject.getString("type");
			if(type.equals("invitation")){
				String alert = jsonObject.getString("alert");
				String sound = jsonObject.getString("sound");
				sendNotification1(type, alert, sound, "","", "", "");

			}
			else if(type.equals("message")){
				String alert = jsonObject.getString("alert");
				String photo = jsonObject.getString("photo");
				String message = jsonObject.getString("message");
				String name = jsonObject.getString("name");
				String sound = jsonObject.getString("sound");
				sendNotification1(type, alert, sound, photo, message, name, "");
			}
			else if(type.equals("request")){
				String alert = jsonObject.getString("alert");
				String sound = jsonObject.getString("sound");
				sendNotification1(type, alert, sound, "", "", "", "");
			}
//			else if(type.equals("like")){
//				String alert = jsonObject.getString("alert");
//				String sound = jsonObject.getString("sound");
//				sendNotification1(type, alert, sound, "","", "", "");
//			}
//			else if(type.equals("follow")){
//				String alert = jsonObject.getString("alert");
//				String sound = jsonObject.getString("sound");
//				sendNotification1(type, alert, sound, "", "", "", "");
//			}

//			else if(type.equals("comment")){
//				String alert = jsonObject.getString("alert");
//				String sound = jsonObject.getString("sound");
//				String article_id = jsonObject.getString("article_id");
//				sendNotification1(type, alert, sound, "", "", "", article_id);
//
//			}

//			else if(type.equals("reject")){
//				String alert = jsonObject.getString("alert");
//				String sound = jsonObject.getString("sound");
//				sendNotification1(type, alert, sound, "", "", "", "");
//			}

	}
	private void sendNotification1(String type, String alert, String sound, String photo, String message, String name, String article_id ){

		int icon = R.drawable.icon16;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager)	this.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		Intent notificationIntent = null;
		if(type.equals("message")) {

			globalVariable.increaseValue(prefs, Constant.N_MESSAGE);
			int msg_count = prefs.getInt(Constant.N_MESSAGE, 0);
			msg_count ++ ;

			NotificationCompat.Builder notificationBuilder;
			NotificationManager manager;

//			Bundle args = new Bundle();
//			args.putString("photo", photo);
//			args.putString("message", message);
//			args.putString("name", name);
//			args.putString("type", type);
//			args.putString("alert", alert);

			Intent chat = new Intent(this, FeedViewActivity.class);
			chat.putExtra("type", type);
			notificationBuilder = new NotificationCompat.Builder(this);
			notificationBuilder.setContentTitle(name);
			notificationBuilder.setContentText(alert);
			notificationBuilder.setTicker("FixAFriend");
			notificationBuilder.setSmallIcon(R.drawable.app_icon);

			PendingIntent contentIntent = PendingIntent.getActivity(this, 1000,	chat, PendingIntent.FLAG_CANCEL_CURRENT);
			notificationBuilder.setContentIntent(contentIntent);
			notificationBuilder.setAutoCancel(true);
			manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

			manager.notify(0, notificationBuilder.build());

			////////////////////
//			notificationIntent = new Intent(this, MessageActivity.class);
//			notificationIntent.putExtra("type", type);
		}else {
			if(type.equals("invitation")){

				globalVariable.increaseValue(prefs, Constant.N_INVITATION);

				notificationIntent = new Intent(this, FeedViewActivity.class);
				notificationIntent.putExtra("type", type);

			}else if(type.equals("request")){

				globalVariable.increaseValue(prefs, Constant.N_REQUEST);

				int test = globalVariable.getValue(prefs, Constant.N_REQUEST);
				test ++;
				Bundle args = new Bundle();
				args.putString("alert", alert);

				notificationIntent = new Intent(this, FeedViewActivity.class);
				notificationIntent.putExtras(args);
//			}else if(type.equals("like")){
//				globalVariable.increaseLike();
//
//				notificationIntent = new Intent(this, FeedViewActivity.class);
//				notificationIntent.putExtra("type", type);

//			}else if(type.equals("follow")){

//				globalVariable.increaseFollow();
//
//				notificationIntent = new Intent(this, ProfileActivity.class);
//				notificationIntent.putExtra("type", type);

//			}else if(type.equals("comment")){

//				globalVariable.increaseComment();
//
//				Bundle args = new Bundle();
//
//				args.putString("article_id", article_id);
//
//				notificationIntent = new Intent(this, ProfileActivity.class);
//				notificationIntent.putExtras(args);



//				sendNotification(notificationIntent, type);

//			}else if(type.equals("reject")){
//				globalVariable.increaseReject();
//			notificationIntent = new Intent(this, ProfileActivity.class);
//			notificationIntent.putExtra("type", type);
			}
		}
		if(notificationIntent != null){
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

			PendingIntent intent =	PendingIntent.getActivity(this, 0, notificationIntent, 0);

			notification.setLatestEventInfo(this, "FixAFriend", alert, intent);

			notification.flags |= Notification.FLAG_AUTO_CANCEL;

			// Play default notification sound
			notification.defaults |= Notification.DEFAULT_SOUND;

			notification.number |= getTotalNoticeNum() ;

			// Vibrate if vibrate is enabled
			notification.defaults |= Notification.DEFAULT_VIBRATE;

			notificationManager.notify(0, notification);
		}

	}
	private void inputDataToPreference(int value, String key){
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	private int getTotalNoticeNum(){
		int sum = 0;
		sum += globalVariable.getValue(prefs, Constant.N_MESSAGE);
		sum += globalVariable.getValue(prefs, Constant.N_REQUEST);
		sum += globalVariable.getValue(prefs, Constant.N_FOLLOW);
		sum += globalVariable.getValue(prefs, Constant.N_COMMENT);
		sum += globalVariable.getValue(prefs, Constant.N_INVITATION);
		sum += globalVariable.getValue(prefs, Constant.N_LIKE);
		sum += globalVariable.getValue(prefs, Constant.N_REJECT);

		return  sum;
	}
}
//	private void sendNotification(Intent intent, String type) {
//		NotificationCompat.Builder notificationBuilder;
//		NotificationManager manager;
//
//
////		Intent chat = new Intent(this, ChatActivity.class);
//		intent.putExtra("type", type);
//		notificationBuilder = new NotificationCompat.Builder(this);
//		notificationBuilder.setContentTitle(type);
////		notificationBuilder.setContentText(alert);
//		notificationBuilder.setTicker("FixAFriend");
//		notificationBuilder.setSmallIcon(R.drawable.icon16);
//
//		PendingIntent contentIntent = PendingIntent.getActivity(this, 1000,	intent, PendingIntent.FLAG_CANCEL_CURRENT);
//		notificationBuilder.setContentIntent(contentIntent);
//		notificationBuilder.setAutoCancel(true);
//		manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//		manager.notify(1, notificationBuilder.build());
//
//		////////////////////
//
//
//	}


