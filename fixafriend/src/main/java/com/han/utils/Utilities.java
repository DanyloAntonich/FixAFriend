package com.han.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.han.login.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

//import org.apache.http.util.TextUtils;

/**
 * Created by Administrator on 10/12/2015.
 */
public class Utilities {
    public static boolean haveNetworkConnection(Context activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static void saveImageToLocal( String path){
        Bitmap finalBitmap = BitmapFactory.decodeFile(path);
        File myDir = new File(Constant.CAMERA_ROLL);
        myDir.mkdirs();
        Random generator = new Random();
        ///generate random number
//        int n = 10000;
//        n = generator.nextInt(n);
//        String fname = "Image-"+ n +".jpg";

        File file = new File (myDir, path.substring(path.lastIndexOf("/") + 1));
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            if(finalBitmap != null){
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            }

            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public static Dialog showAlert(final Context context, String message,
//                                   final boolean finish) {
//        final Dialog dialog = new Dialog(context, R.style.FullHeightDialog);
//        dialog.setContentView(R.layout.dialog_alert);
//
//        TextView tvError = (TextView) dialog.findViewById(R.id.tvError);
//        tvError.setText(message);
//
//        Button btnOK = (Button) dialog.findViewById(R.id.btnOk);
//        btnOK.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.cancel();
//                if (finish) {
//                    ((Activity) context).finish();
//                }
//            }
//        });
//
//        dialog.setCancelable(true);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
//
//        return dialog;
//    }
    public static void showOKDialog(Context context, String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Fix-A-Friend");
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
//        builder1.setNegativeButton("No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    public static void showToast(final Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static Dialog showProgressDialog(Context mContext, String text,
                                            boolean cancelable) {
        Dialog mDialog = new Dialog(mContext, R.style.ProgressBarTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        mDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        View layout = mInflater.inflate(R.layout.custom_progressbar, null);
        mDialog.setContentView(layout);

        TextView tvProgressMessage = (TextView) layout
                .findViewById(R.id.tvProgressMessage);

        if (text.equals(""))
            tvProgressMessage.setVisibility(View.GONE);
        else
            tvProgressMessage.setText(text);

        mDialog.setCancelable(cancelable);
        mDialog.setCanceledOnTouchOutside(false);
		/*
		 * mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND
		 * );
		 */
        mDialog.show();

        return mDialog;
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        SharedPreferences objSharedPreferences = null;
        try {
            objSharedPreferences = context.getSharedPreferences(
                    Constant.PREF_USER_DETAILS, Context.MODE_PRIVATE);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return objSharedPreferences;
    }


    public static void sendEmail(Context mContext, String toAddress) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[] { toAddress });
        email.putExtra(Intent.EXTRA_SUBJECT, "Perceptual Yoga");
        email.putExtra(
                Intent.EXTRA_TEXT,
                "Namaste\n\nKeep track of your Yoga activity such as Practice, Teaching and Learning, browse our members database, share your achievements and more. \nDownload Perpetual Yoga \n Find it at your App Store and Google Play \n https://play.google.com/store?hl=en&tab=i8");
        email.setType("message/rfc822");
        mContext.startActivity(Intent.createChooser(email,
                "Choose an Email client :"));
    }

//    public static void call(Context mContext, String phone) {
//        Intent callIntent = new Intent(Intent.ACTION_CALL);
//        callIntent.setData(Uri.parse("tel:" + phone));
//        mContext.startActivity(callIntent);
//    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public static long[] getHours(String start, String end) {
        long[] difs = new long[2];

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm a");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(start);
            d2 = format.parse(end);

            // in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            difs[0] = diffHours;
            difs[1] = diffMinutes;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return difs;

    }

    public static String dateWithCustomFormat(String date, String curFormat,
                                              String format) {
        DateFormat dateFormat = new SimpleDateFormat(curFormat);
        Date convertedDate = null;

        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat1 = new SimpleDateFormat(format);

        return dateFormat1.format(convertedDate);
    }

    public static Typeface setFont(TextView tv, Context ctx) {

        AssetManager assetManager = ctx.getAssets();

        Typeface tf = Typeface.createFromAsset(assetManager, "");

        return tf;
    }

    public static String parseDateFormat(String dateString) {
        if (dateString == null) {
            return "";
        }
        Date date = null;

        String mDate = dateString;
        DateFormat dateFormat = new SimpleDateFormat(Constant.DATE_PATTERN_MMM_dd_yyyy,
                Locale.ENGLISH);

        try {
            if (date == null) {
                date = dateFormat.parse(dateString);
            }
            String mdateString = dateFormat.format(date);
            date = dateFormat.parse(mdateString);
            Date currentdate = new Date();
            String when = dateFormat.format(currentdate);
            currentdate = dateFormat.parse(when);

            long diffInMillisec = currentdate.getTime() - date.getTime();
            long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMillisec);
            long seconds = diffInSec % 60;
            diffInSec /= 60;
            long minutes = diffInSec % 60;
            diffInSec /= 60;
            long hours = diffInSec % 24;
            diffInSec /= 24;
            long days = diffInSec % 30;
            diffInSec /= 30;
            long months = diffInSec % 12;
            diffInSec /= 12;
            long year = diffInSec;
            mDate = String.valueOf(seconds);
            if (year > 0) {

                int length = mdateString.lastIndexOf(":");
                mdateString = mdateString.substring(0, length);
                mdateString = mdateString.replace(";", " at ");
                mDate = mdateString;
            } else if (months > 0) {

                int length = mdateString.lastIndexOf(":");
                mdateString = mdateString.substring(0, length);
                mdateString = mdateString.replace(";", " at,");
                mDate = mdateString;
            } else if (days > 1) {

                int length = mdateString.lastIndexOf(":");
                mdateString = mdateString.substring(0, length);
                mdateString = mdateString.replace(";", " at,");
                mDate = mdateString;
            } else if (days == 1) {
                int end = mdateString.lastIndexOf(";");
                int length = mdateString.lastIndexOf(":");
                mDate = "Yesterday at, "
                        + mdateString.substring(end + 1, length);
            } else if (days > 0) {
                mDate = String.valueOf(days) + " days ago";
            } else if (hours > 0) {
                mDate = String.valueOf(hours) + " hours ago";
            } else {
                mDate = String.valueOf(minutes) + " minutes ago";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDate;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getMyDateFromGMTString(String gmtDateString) {
        Date date = null;
        try {
            DateFormat df = new SimpleDateFormat(Constant.GMT_PATTERN_24);
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = df.parse(gmtDateString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    static AlertDialog alertDialog = null;

//    public static void showCallAlert(final Context mContext,
//                                     final String phoneNo) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//
//        View view = LayoutInflater.from(mContext).inflate(
//                R.layout.dialog_callalert, null);
//
//        Button cancel = (Button) view.findViewById(R.id.cancel);
//        Button call = (Button) view.findViewById(R.id.call);
//
//        call.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                alertDialog.dismiss();
//                Intent intent = new Intent(Intent.ACTION_CALL);
//                intent.setData(Uri.parse("tel:" + phoneNo));
//                mContext.startActivity(intent);
//
//            }
//        });
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-geanerated method stub
//                alertDialog.dismiss();
//            }
//        });
//        builder.setTitle("Phone");
//
//        builder.setView(view);
//
//        alertDialog = builder.create();
//        alertDialog.show();
//
//    }

    public static String getDatewithFormat(String date) {

        Date mDate = new Date(date.replace("-", "/"));
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        String showDate = df.format(mDate);

        return showDate;
    }

    public static String getDatewithFormat2(Date date) {
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MMM/yyyy");
        String showDate1 = df2.format(date);

        return showDate1;
    }

    public static String getDatewithFormat3(Date date) {

        SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
        String showDate1 = df2.format(date);

        return showDate1;
    }

    public static int getCurrentDAY() {
        // TODO Auto-generated method stub
        int showhours = 1;
        try {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            Date mDate = cal.getTime();
            SimpleDateFormat df2 = new SimpleDateFormat("dd");
            String date = df2.format(mDate);
            showhours = Integer.parseInt(date);
        } catch (Exception e) {

        }
        return showhours;
    }

    public static Integer getdaysFromdate(String date) {
        int days = 1;
        try {
            Date mDate = new Date(date.replace("-", "/"));



            SimpleDateFormat df = new SimpleDateFormat("dd");
            String sdays = df.format(mDate);
            days = Integer.parseInt(sdays);
        } catch (Exception e) {

        }
        return days;

    }

    public static void hide_keyboard_from(Context context, View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissKeyboard(Context mContext, View registerPlan) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                registerPlan.getWindowToken(), 0);

    }

    public static JSONObject getJSONGetPractitionerRecords(String contactNo) {
        HashMap<String, Object> outerMap = new HashMap<String, Object>();
        HashMap<String, String> innerMap = new HashMap<String, String>();

        innerMap.put("contactNo", contactNo);
        outerMap.put("practitionerDetails", new JSONObject(innerMap));

        return new JSONObject(outerMap);
    }

    public static String convertStreamToString(InputStream is)
            throws IOException {
        Writer writer = new StringWriter();
        char[] buffer = new char[2048];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is,
                    "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }
        String text = writer.toString();
        return text;
    }

    public static String getSimCountryIso(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimCountryIso().toUpperCase();
    }

    public static String getNetworkCountryIso(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkCountryIso().toUpperCase();
    }

//    public static boolean isValidPhoneNumber(CharSequence phoneNumber) {
//        if (!TextUtils.isEmpty(phoneNumber)) {
//            return Patterns.PHONE.matcher(phoneNumber).matches();
//        }
//        return false;
//    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }
    }
    ////clear cache
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        else if(dir!= null && dir.isFile())
            return dir.delete();
        else {
            return false;
        }
    }
}
