package com.han.downloadmanager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.han.login.R;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import at.grabner.circleprogress.CircleProgressView;

/**
 * Created by hic on 11/3/2015.
 */
public class MyDownloadManager {
    private DownloadQueue queue;
    private int max_count;
    private Timer timer;
    private TimerTask timerTask;

    public MyDownloadManager(int max_count) {
        this.max_count = max_count;
        queue = new DownloadQueue(max_count);
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (queue.size() > 0) {
                    DownloadData data = queue.remove();
                    Downloader downloader = new Downloader(data);
                    downloader.execute();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 100);
    }

    public void insert(Context context, String url, LinearLayout layout) {
        queue.insert(new DownloadData(context, url, layout));
    }
}
class DownloadQueue {
    private int maxSize;
    private DownloadData[] queArray;
    private int front;
    private int rear;
    private int nItems;

    public DownloadQueue(int s) {
        maxSize = s;
        queArray = new DownloadData[maxSize];
        front = 0;
        rear = -1;
        nItems = 0;
    }

    //   put item at end of a queue
    public void insert(DownloadData j) {
        if (rear == maxSize - 1) // deal with wraparound
            rear = -1;
        queArray[++rear] = j; // increment rear and insert
        nItems++;
    }

    //   take item from front of queue
    public DownloadData remove() {
        DownloadData temp = queArray[front++]; // get value and incr front
        if (front == maxSize) // deal with wraparound
            front = 0;
        nItems--; // one less item
        return temp;
    }

    public DownloadData peekFront() {
        return queArray[front];
    }

    public boolean isEmpty() {
        return (nItems == 0);
    }

    public boolean isFull() {
        return (nItems == maxSize);
    }

    public int size() {
        return nItems;
    }
}

class DownloadData {
    public Context context;
    public String url;
    public LinearLayout layout;

    public DownloadData(Context context, String url, LinearLayout layout) {
        this.context = context;
        this.url = url;
        this.layout = layout;
    }
}

class Downloader extends AsyncTask<String, Integer, Drawable>
{
    private Drawable d;
    private HttpURLConnection conn;
    private InputStream stream; //to read
    private ByteArrayOutputStream out; //to write

    private double fileSize;
    private double downloaded; // number of bytes downloaded
    private CircleProgressView progressView;

    private static final int MAX_BUFFER_SIZE = 1024; //1kb

    public DownloadData downloadData;
    private View view = null;

    public Downloader(DownloadData data)
    {
        this.downloadData = data;
        d          = null;
        conn       = null;
        fileSize   = 0;
        downloaded = 0;
    }

    @Override
    protected Drawable doInBackground(String... url1)
    {


        String url = downloadData.url;
        String filename = url.substring( url.lastIndexOf('/')+1, url.length());
        Context context = downloadData.context;

        Log.v("DEBUG", "sourceUrl: " + url);
        Log.v("DEBUG", "destinationPath: "+filename);

        InputStream urlInputStream=null;

        URLConnection urlConnection ;

        try
        {
            //Form a new URL
            URL finalUrl =new URL(url);

            urlConnection = finalUrl.openConnection();

            //Get the size of the (file) inputstream from server..
            fileSize = urlConnection.getContentLength();
            downloaded = 0;

            Log.d("1URL","Streaming from " + url + "....");
            DataInputStream stream = new DataInputStream(finalUrl.openStream());
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);

            Log.d("2FILE", "Buffering the received stream(size=" + fileSize + ") ...");
            while (true) {
                byte buffer[];
                if (fileSize - downloaded > MAX_BUFFER_SIZE)
                {
                    buffer = new byte[MAX_BUFFER_SIZE];
                }
                else
                {
                    buffer = new byte[(int) (fileSize - downloaded)];
                }
                int read = stream.read(buffer);

                if (read == -1)
                {
                    publishProgress(100);
                    break;
                }

                // writing to buffer
                fos.write(buffer, 0, read);
                downloaded += read;
                // update progress bar
                publishProgress((int) ((downloaded / fileSize) * 100));
//                    Log.d("6.1FILE", "Progress ..." + (int) ((downloaded / fileSize) * 100));


//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        Log.v("9Timer", "Sleep");
//                    }
            }
            stream.close();
            fos.flush();
            fos.close();
            Log.d("6.1FILE", "Created the new file & returning 'true'..");
            return null;
        }
        catch (Exception e)
        {
            Log.e("9ERROR", "Failed to open urlConnection/Stream the connection(From catch block) & returning 'false'..");
            System.out.println("Exception: " + e);
            e.printStackTrace();
            return null;
        }
        finally
        {
            try
            {
                Log.d("10URL", "Closing urlInputStream... ");
                if (urlInputStream != null) urlInputStream.close();

            }
            catch (Exception e)
            {
                Log.e("11ERROR", "Failed to close urlInputStream(From finally block)..");
            }
        }
    } // end of class DownloadManager()

    @Override
    protected void onProgressUpdate(Integer... changed) {
        progressView.setValueAnimated(changed[0], 10);
    }

    @Override
    protected void onPreExecute()
    {
        LayoutInflater inflater = (LayoutInflater) downloadData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try{
            view = inflater.inflate (R.layout.custom_circle_progress, null);
            progressView = (CircleProgressView)view.findViewById(R.id.kcjCircleView);
            progressView.setValueAnimated(0, 10);
            downloadData.layout.addView(view);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Drawable result)
    {
        downloadData.layout.removeView(view);
    }
}
