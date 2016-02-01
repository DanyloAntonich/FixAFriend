package com.example.rmk.mediaediting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by RMK on 10/18/2015.
 */
public class ImageDownloader extends AsyncTask<Void, Integer, Void> {

    private ProgressBar pb;
    private String url;
    private Button save;
    private Context c;
    private int progress;
    private ImageView img;
    private Bitmap bmp;
    private TextView percent;
    private ImageLoaderListener listener;


    /*--- constructor ---*/
    public ImageDownloader(String url, ProgressBar pb, Button save,
                           ImageView img, TextView percent, Context c, Bitmap bmp, ImageLoaderListener listener) {
/*--- we need to pass some objects we are going to work with ---*/
        this.url = url;
        this.pb = pb;
        this.save = save;
        this.c = c;
        this.img = img;
        this.percent = percent;
        this.bmp = bmp;
        this.listener = listener;
    }

    /*--- we need this interface for keeping the reference to our Bitmap from the MainActivity.
     *  Otherwise, bmp would be null in our MainActivity*/
    public interface ImageLoaderListener {

        void onImageDownloaded(Bitmap bmp);

    }

    @Override
    protected void onPreExecute() {

        progress = 0;
        pb.setVisibility(View.VISIBLE);
        percent.setVisibility(View.VISIBLE);
        Toast.makeText(c, "starting download", Toast.LENGTH_SHORT).show();

        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        bmp = getBitmapFromURL(url);

        while (progress < 100) {

            progress += 1;

            publishProgress(progress);

        /*--- an image download usually happens very fast so you would not notice
         * how the ProgressBar jumps from 0 to 100 percent. You can use the method below
         * to visually "slow down" the download and see the progress bein updated ---*/

            SystemClock.sleep(200);

        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

/*--- show download progress on main UI thread---*/
        pb.setProgress(values[0]);
        percent.setText(values[0] + "%");

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {

        if (listener != null) {
            listener.onImageDownloaded(bmp);
        }
        img.setImageBitmap(bmp);
        save.setEnabled(true);
        Toast.makeText(c, "download complete", Toast.LENGTH_SHORT).show();

        super.onPostExecute(result);
    }

    public static Bitmap getBitmapFromURL(String link) {
    /*--- this method downloads an Image from the given URL,
     *  then decodes and returns a Bitmap object
     ---*/
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

}