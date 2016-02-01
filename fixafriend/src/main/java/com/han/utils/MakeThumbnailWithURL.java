package com.han.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 10/25/2015.
 */
public class MakeThumbnailWithURL {
    private final int TIMEOUT_CONNECTION = 5000;// 5sec
    private final int TIMEOUT_SOCKET = 300000;// 30sec


    private final String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator ;
//    String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    private Bitmap getVideoFrame(String uri) throws IOException {
        FileOutputStream out = null;
        File land=new File(Environment.getExternalStorageDirectory().getAbsoluteFile()
                +"/article.jpg");

        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(uri, MediaStore.Video.Thumbnails.MICRO_KIND);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        try {
            out=new FileOutputStream(land.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        out.write(byteArray);
        out.close();
        return bitmap;
    }


    public Bitmap makeThumbnail(String URL){
        String imgurl = URL;
        try {
            FileInputStream fstream = new FileInputStream(imgurl);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(imgurl,   MediaStore.Images.Thumbnails.MINI_KIND);
        Matrix matrix = new Matrix();
        Bitmap bmThumbnail = Bitmap.createBitmap(thumb, 0, 0, thumb.getWidth(), thumb.getHeight(), matrix, true);

        return bmThumbnail;
    }
    public String SaveImage(Bitmap finalBitmap, String username) {

//        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(PATH + "saved_images");
        myDir.mkdirs();

        String fname = username + ".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public Bitmap DownloadFromUrl(String VideoURL, String fileName) throws IOException {  //this is the downloader method
        URL url = null;
        try {
            url = new URL(VideoURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        long startTime = System.currentTimeMillis();
//        Log.i(TAG, "image download beginning: "+imageURL);

        //Open a connection to that URL.
        URLConnection ucon = null;
        try {
            ucon = url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //this timeout affects how long it takes for the app to realize there's a connection problem
        ucon.setReadTimeout(TIMEOUT_CONNECTION);
        ucon.setConnectTimeout(TIMEOUT_SOCKET);


        //Define InputStreams to read from the URLConnection.
        // uses 3KB download buffer
        InputStream is = null;
        try {
            is = ucon.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(PATH + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] buff = new byte[5 * 1024];

        //Read bytes (and store them) until there is nothing more to read(-1)
        int len;
        try {
            while ((len = inStream.read(buff)) != -1)
            {
                outStream.write(buff,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //clean up
        try {
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("download", "download completed in "
                + ((System.currentTimeMillis() - startTime) / 1000)
                + " sec");
        Log.d("download", "download completed in " + ((System.currentTimeMillis() - startTime) / 1000) + " sec");
        return  makeThumbnail(fileName);
    }

}
